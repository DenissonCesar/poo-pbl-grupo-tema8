package application;

import application.usecases.AprovarOrcamento;
import application.usecases.FinalizarServico;
import domain.entities.OrdemDeServico;
import domain.entities.Veiculo;
import domain.valueobjects.ItemOrcamento;
import infrastructure.notifications.FakeWhatsAppNotificacao;
import infrastructure.repositories.OrdemDeServicoRepositorioMemoria;
import infrastructure.repositories.VeiculoRepositorioMemoria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC: AprovarOrcamento e FinalizarServico")
class AprovarOrcamentoEFinalizarServicoTest {

    private OrdemDeServicoRepositorioMemoria ordemRepo;
    private VeiculoRepositorioMemoria        veiculoRepo;
    private FakeWhatsAppNotificacao          fakeWhatsApp;
    private AprovarOrcamento                 ucAprovar;
    private FinalizarServico                 ucFinalizar;

    @BeforeEach
    void setUp() {
        ordemRepo    = new OrdemDeServicoRepositorioMemoria();
        veiculoRepo  = new VeiculoRepositorioMemoria();
        fakeWhatsApp = new FakeWhatsAppNotificacao();
        ucAprovar    = new AprovarOrcamento(ordemRepo, veiculoRepo, fakeWhatsApp);
        ucFinalizar  = new FinalizarServico(ordemRepo, veiculoRepo, fakeWhatsApp);

        // Veículo base
        veiculoRepo.salvar(new Veiculo(
                "VEI-001", "ABC1234", "Fiat", "Strada", 2021,
                "Carlos Mendes", "+55 82 99123-4567"));

        // O.S. pronta para aprovação
        OrdemDeServico os = new OrdemDeServico(
                "OS-001", "VEI-001", "Motor falhando", "João Silva");
        os.adicionarItem(new ItemOrcamento(
                "PECA-001", "Junta cabeçote", 1, 280.0, ItemOrcamento.Tipo.PECA));
        os.adicionarItem(new ItemOrcamento(
                "MO-001", "Mão de obra", 1, 350.0, ItemOrcamento.Tipo.MAO_DE_OBRA));
        os.enviarParaAprovacao("Junta do cabeçote danificada.", "Revisão geral recomendada.");
        ordemRepo.salvar(os);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // AprovarOrcamento
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Aprovar: deve mudar status para APROVADA e enviar WhatsApp")
    void deveAprovarOrcamentoENotificarCliente() {
        AprovarOrcamento.Output output = ucAprovar.executar(
                new AprovarOrcamento.Input("OS-001", true));

        assertEquals("AGUARDANDO_APROVACAO", output.statusAnterior);
        assertEquals("APROVADA",             output.statusAtual);
        assertTrue(output.notificacaoEnviada);
        assertEquals(1, fakeWhatsApp.getHistorico().size());
    }

    @Test
    @DisplayName("Recusar: deve mudar status para RECUSADA e notificar cliente")
    void deveRecusarOrcamentoENotificarCliente() {
        AprovarOrcamento.Output output = ucAprovar.executar(
                new AprovarOrcamento.Input("OS-001", false));

        assertEquals("RECUSADA", output.statusAtual);
        assertTrue(output.notificacaoEnviada);
    }

    @Test
    @DisplayName("Aprovar: deve lançar exceção para O.S. inexistente")
    void deveLancarExcecaoParaOSInexistenteAoAprovar() {
        assertThrows(java.util.NoSuchElementException.class,
                () -> ucAprovar.executar(
                        new AprovarOrcamento.Input("OS-NAOEXISTE", true)));
    }

    @Test
    @DisplayName("Aprovar: deve lançar exceção se O.S. não está aguardando aprovação")
    void deveLancarExcecaoSeStatusIncorretoAoAprovar() {
        // Coloca a O.S. em um status diferente de AGUARDANDO_APROVACAO
        OrdemDeServico osNova = new OrdemDeServico(
                "OS-002", "VEI-001", "Outro problema", "João");
        ordemRepo.salvar(osNova); // status = AGUARDANDO_DIAGNOSTICO

        assertThrows(IllegalStateException.class,
                () -> ucAprovar.executar(
                        new AprovarOrcamento.Input("OS-002", true)));
    }

    @Test
    @DisplayName("Aprovar: falha no WhatsApp não deve reverter aprovação")
    void falhaNoWhatsAppNaoDeveReverterAprovacao() {
        fakeWhatsApp.simularFalha = true;

        AprovarOrcamento.Output output = ucAprovar.executar(
                new AprovarOrcamento.Input("OS-001", true));

        assertEquals("APROVADA", output.statusAtual);
        assertFalse(output.notificacaoEnviada);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FinalizarServico
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Finalizar: deve finalizar O.S. a partir de APROVADA e notificar")
    void deveFinalizarOSDeAprovadaENotificar() {
        // Aprova primeiro
        ucAprovar.executar(new AprovarOrcamento.Input("OS-001", true));
        fakeWhatsApp.limparHistorico();

        FinalizarServico.Output output = ucFinalizar.executar(
                new FinalizarServico.Input("OS-001"));

        assertEquals("FINALIZADA", output.statusFinal);
        assertEquals(630.0,        output.valorTotal, 0.001);
        assertNotNull(output.dataFinalizacao);
        assertTrue(output.notificacaoEnviada);
        assertEquals(1, fakeWhatsApp.getHistorico().size());
    }

    @Test
    @DisplayName("Finalizar: deve funcionar a partir de EM_EXECUCAO")
    void deveFinalizarOSDeEmExecucao() {
        OrdemDeServico os = ordemRepo.buscarPorId("OS-001").orElseThrow();
        os.aprovar();
        os.iniciarExecucao();
        ordemRepo.atualizar(os);

        FinalizarServico.Output output = ucFinalizar.executar(
                new FinalizarServico.Input("OS-001"));

        assertEquals("FINALIZADA", output.statusFinal);
    }

    @Test
    @DisplayName("Finalizar: deve lançar exceção para O.S. não aprovada")
    void deveLancarExcecaoAoFinalizarOSNaoAprovada() {
        // OS ainda está AGUARDANDO_APROVACAO
        assertThrows(IllegalStateException.class,
                () -> ucFinalizar.executar(
                        new FinalizarServico.Input("OS-001")));
    }

    @Test
    @DisplayName("Finalizar: deve lançar exceção para O.S. inexistente")
    void deveLancarExcecaoParaOSInexistenteAoFinalizar() {
        assertThrows(java.util.NoSuchElementException.class,
                () -> ucFinalizar.executar(
                        new FinalizarServico.Input("OS-NAOEXISTE")));
    }

    @Test
    @DisplayName("Finalizar: deve retornar resumo de itens correto")
    void deveRetornarResumoDeItensCorreto() {
        ucAprovar.executar(new AprovarOrcamento.Input("OS-001", true));
        FinalizarServico.Output output = ucFinalizar.executar(
                new FinalizarServico.Input("OS-001"));

        assertEquals(2, output.itens.size());
        assertEquals(280.0, output.itens.get(0).subtotal, 0.001);
        assertEquals(350.0, output.itens.get(1).subtotal, 0.001);
    }
}
