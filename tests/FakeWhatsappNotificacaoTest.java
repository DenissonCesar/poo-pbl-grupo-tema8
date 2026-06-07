package application;

import domain.services.INotificacaoWhatsApp.ResultadoEnvio;
import infrastructure.notifications.FakeWhatsAppNotificacao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FakeWhatsAppNotificacao")
class FakeWhatsAppNotificacaoTest {

    private FakeWhatsAppNotificacao fake;

    @BeforeEach
    void setUp() {
        fake = new FakeWhatsAppNotificacao();
    }

    @Test
    @DisplayName("Deve registrar mensagem no histórico após envio")
    void deveRegistrarMensagemNoHistorico() {
        fake.enviarMensagem("+55 82 99999-0000", "João", "Teste de mensagem", "OS-001");

        assertEquals(1, fake.getHistorico().size());
        assertEquals("OS-001", fake.getHistorico().get(0).getOrdemId());
    }

    @Test
    @DisplayName("Deve retornar sucesso com mensagemId gerado")
    void deveRetornarSucessoComMensagemId() {
        ResultadoEnvio resultado = fake.enviarMensagem(
                "+55 82 99999-0000", "João", "Olá!", "OS-001");

        assertTrue(resultado.isSucesso());
        assertNotNull(resultado.getMensagemId());
        assertTrue(resultado.getMensagemId().startsWith("WA-FAKE-"));
    }

    @Test
    @DisplayName("Deve simular falha quando simularFalha = true")
    void deveSimularFalha() {
        fake.simularFalha = true;

        ResultadoEnvio resultado = fake.enviarMensagem(
                "+55 82 99999-0000", "João", "Olá!", "OS-001");

        assertFalse(resultado.isSucesso());
        assertNotNull(resultado.getErro());
        assertEquals(0, fake.getHistorico().size()); // mensagem com falha não é registrada
    }

    @Test
    @DisplayName("Deve resetar simularFalha após um uso")
    void deveResetarSimularFalhaAposUmUso() {
        fake.simularFalha = true;

        fake.enviarMensagem("+55 82 99999-0000", "João", "Msg 1", "OS-001"); // falha
        ResultadoEnvio segundo = fake.enviarMensagem(
                "+55 82 99999-0000", "João", "Msg 2", "OS-001"); // deve ter sucesso

        assertTrue(segundo.isSucesso());
    }

    @Test
    @DisplayName("notificarOrcamentoDisponivel deve mencionar valor e ordemId")
    void notificarOrcamentoDisponivelDeveConterDadosCorretos() {
        fake.notificarOrcamentoDisponivel(
                "+55 82 99999-0000", "Carlos", "OS-001", 630.00, "Troca de junta");

        String conteudo = fake.getHistorico().get(0).getConteudo();

        assertTrue(conteudo.contains("OS-001"));
        assertTrue(conteudo.contains("630,00") || conteudo.contains("630.00"));
        assertTrue(conteudo.contains("Carlos"));
    }

    @Test
    @DisplayName("notificarServicoFinalizado deve mencionar valor e ordemId")
    void notificarServicoFinalizadoDeveConterDadosCorretos() {
        fake.notificarServicoFinalizado(
                "+55 82 99999-0000", "Carlos", "OS-001", 630.00);

        String conteudo = fake.getHistorico().get(0).getConteudo();

        assertTrue(conteudo.contains("OS-001"));
        assertTrue(conteudo.contains("Carlos"));
    }

    @Test
    @DisplayName("getMensagensParaTelefone deve filtrar por telefone")
    void deveFiltrarMensagensPorTelefone() {
        fake.enviarMensagem("+55 82 99999-0000", "Carlos", "Msg 1", "OS-001");
        fake.enviarMensagem("+55 82 88888-1111", "Ana",    "Msg 2", "OS-002");
        fake.enviarMensagem("+55 82 99999-0000", "Carlos", "Msg 3", "OS-003");

        assertEquals(2, fake.getMensagensParaTelefone("+55 82 99999-0000").size());
        assertEquals(1, fake.getMensagensParaTelefone("+55 82 88888-1111").size());
    }

    @Test
    @DisplayName("getMensagensParaOrdem deve filtrar por ordemId")
    void deveFiltrarMensagensPorOrdem() {
        fake.enviarMensagem("+55 82 99999-0000", "Carlos", "Msg 1", "OS-001");
        fake.enviarMensagem("+55 82 99999-0000", "Carlos", "Msg 2", "OS-001");
        fake.enviarMensagem("+55 82 99999-0000", "Carlos", "Msg 3", "OS-002");

        assertEquals(2, fake.getMensagensParaOrdem("OS-001").size());
        assertEquals(1, fake.getMensagensParaOrdem("OS-002").size());
    }

    @Test
    @DisplayName("limparHistorico deve apagar todas as mensagens")
    void deveLimparHistorico() {
        fake.enviarMensagem("+55 82 99999-0000", "Carlos", "Msg", "OS-001");
        fake.limparHistorico();

        assertEquals(0, fake.getHistorico().size());
    }
}
