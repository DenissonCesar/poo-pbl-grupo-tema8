package application;

import application.usecases.AbrirOrdemDeServico;
import domain.entities.Veiculo;
import infrastructure.repositories.OrdemDeServicoRepositorioMemoria;
import infrastructure.repositories.VeiculoRepositorioMemoria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC: AbrirOrdemDeServico")
class AbrirOrdemDeServicoTest {

    private OrdemDeServicoRepositorioMemoria ordemRepo;
    private VeiculoRepositorioMemoria        veiculoRepo;
    private AbrirOrdemDeServico              useCase;

    @BeforeEach
    void setUp() {
        ordemRepo   = new OrdemDeServicoRepositorioMemoria();
        veiculoRepo = new VeiculoRepositorioMemoria();
        useCase     = new AbrirOrdemDeServico(ordemRepo, veiculoRepo);
    }

    // ── Cenários de sucesso ───────────────────────────────────────────────────

    @Test
    @DisplayName("Deve abrir O.S. com sucesso quando veículo existe")
    void deveAbrirOSComSucesso() {
        veiculoRepo.salvar(veiculoValido("VEI-001", "ABC1234"));

        AbrirOrdemDeServico.Input input = new AbrirOrdemDeServico.Input(
                "OS-001", "VEI-001", "Motor falhando ao acelerar", "João Silva");

        AbrirOrdemDeServico.Output output = useCase.executar(input);

        assertEquals("OS-001", output.ordemId);
        assertEquals("AGUARDANDO_DIAGNOSTICO", output.status);
        assertEquals("João Silva", output.mecanicoResponsavel);
        assertEquals(1, ordemRepo.tamanho());
    }

    // ── Cenários de falha ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção quando veículo não existe")
    void deveLancarExcecaoSeVeiculoNaoExiste() {
        AbrirOrdemDeServico.Input input = new AbrirOrdemDeServico.Input(
                "OS-001", "VEI-INEXISTENTE", "Problema no freio", "João");

        assertThrows(java.util.NoSuchElementException.class,
                () -> useCase.executar(input));
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existe O.S. aberta para o veículo")
    void deveLancarExcecaoSeJaExisteOSAberta() {
        veiculoRepo.salvar(veiculoValido("VEI-001", "ABC1234"));

        // Abre a primeira O.S.
        useCase.executar(new AbrirOrdemDeServico.Input(
                "OS-001", "VEI-001", "Primeiro problema", "João"));

        // Tenta abrir segunda para o mesmo veículo
        assertThrows(IllegalStateException.class,
                () -> useCase.executar(new AbrirOrdemDeServico.Input(
                        "OS-002", "VEI-001", "Segundo problema", "João")));
    }

    @Test
    @DisplayName("Deve permitir nova O.S. após a anterior ser finalizada")
    void devePermitirNovaOSAposFinalizacao() {
        veiculoRepo.salvar(veiculoValido("VEI-001", "ABC1234"));

        // Abre e finaliza a primeira O.S. manualmente
        useCase.executar(new AbrirOrdemDeServico.Input(
                "OS-001", "VEI-001", "Primeiro problema", "João"));

        domain.entities.OrdemDeServico os =
                ordemRepo.buscarPorId("OS-001").orElseThrow();
        os.adicionarItem(new domain.valueobjects.ItemOrcamento(
                "P1", "Filtro", 1, 50.0, domain.valueobjects.ItemOrcamento.Tipo.PECA));
        os.enviarParaAprovacao("Filtro obstruído", null);
        os.aprovar();
        os.iniciarExecucao();
        os.finalizar();
        ordemRepo.atualizar(os);

        // Deve conseguir abrir nova O.S. para o mesmo veículo
        AbrirOrdemDeServico.Output output = useCase.executar(
                new AbrirOrdemDeServico.Input(
                        "OS-002", "VEI-001", "Segundo problema", "Maria"));

        assertEquals("OS-002", output.ordemId);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Veiculo veiculoValido(String id, String placa) {
        return new Veiculo(id, placa, "Fiat", "Uno", 2020,
                "Carlos Silva", "+55 82 99999-0000");
    }
}
