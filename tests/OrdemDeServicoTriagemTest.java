package domain.triagem;

import domain.entities.OrdemDeServico;
import domain.entities.Veiculo;
import domain.enums.StatusOrdemDeServico;
import domain.valueobjects.Placa;
import domain.valueobjects.Quilometragem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrdemDeServico — Contexto de Triagem/Check-in")
class OrdemDeServicoTriagemTest {

    private Veiculo veiculoPadrao;

    @BeforeEach
    void setUp() {
        veiculoPadrao = new Veiculo(
                "v-001",
                new Placa("ABC-1234"),
                "Fiat Uno",
                2015,
                new Quilometragem(50000)
        );
    }

    // ─── Abertura da OS ───────────────────────────────────────────────────

    @Test
    @DisplayName("deve criar OS com status inicial ABERTA")
    void deveCriarOsComStatusAberta() {
        OrdemDeServico os = new OrdemDeServico("os-001", veiculoPadrao);

        assertEquals(StatusOrdemDeServico.ABERTA, os.getStatus());
    }

    @Test
    @DisplayName("deve registrar o veículo corretamente ao abrir a OS")
    void deveRegistrarVeiculoAoAbrirOs() {
        OrdemDeServico os = new OrdemDeServico("os-001", veiculoPadrao);

        assertEquals(veiculoPadrao, os.getVeiculo());
    }

    @Test
    @DisplayName("deve registrar data de abertura automaticamente")
    void deveRegistrarDataDeAbertura() {
        OrdemDeServico os = new OrdemDeServico("os-001", veiculoPadrao);

        assertNotNull(os.getDataAbertura());
    }

    // ─── Diagnóstico ──────────────────────────────────────────────────────

    @Test
    @DisplayName("deve registrar diagnóstico e mudar status para EM_DIAGNOSTICO")
    void deveRegistrarDiagnosticoEMudarStatus() {
        OrdemDeServico os = new OrdemDeServico("os-001", veiculoPadrao);

        os.registrarDiagnostico("Barulho no motor, possível problema na correia dentada.");

        assertEquals(StatusOrdemDeServico.EM_DIAGNOSTICO, os.getStatus());
        assertEquals("Barulho no motor, possível problema na correia dentada.", os.getDiagnostico());
    }

    @Test
    @DisplayName("deve encaminhar para orçamento e mudar status para AGUARDANDO_ORCAMENTO")
    void deveEncaminharParaOrcamento() {
        OrdemDeServico os = new OrdemDeServico("os-001", veiculoPadrao);
        os.registrarDiagnostico("Troca de óleo e filtro.");

        os.encaminharParaOrcamento();

        assertEquals(StatusOrdemDeServico.AGUARDANDO_ORCAMENTO, os.getStatus());
    }

    // ─── Cancelamento ────────────────────────────────────────────────────

    @Test
    @DisplayName("deve cancelar OS quando status é ABERTA")
    void deveCancelarOsAberta() {
        OrdemDeServico os = new OrdemDeServico("os-001", veiculoPadrao);

        os.cancelar();

        assertEquals(StatusOrdemDeServico.CANCELADA, os.getStatus());
    }

    @Test
    @DisplayName("deve cancelar OS quando status é EM_DIAGNOSTICO")
    void deveCancelarOsEmDiagnostico() {
        OrdemDeServico os = new OrdemDeServico("os-001", veiculoPadrao);
        os.registrarDiagnostico("Problema no freio.");

        os.cancelar();

        assertEquals(StatusOrdemDeServico.CANCELADA, os.getStatus());
    }

    // ─── Transições inválidas ─────────────────────────────────────────────

    @Test
    @DisplayName("deve lançar exceção ao registrar diagnóstico em OS já cancelada")
    void deveLancarExcecaoAoRegistrarDiagnosticoEmOsCancelada() {
        OrdemDeServico os = new OrdemDeServico("os-001", veiculoPadrao);
        os.cancelar();

        assertThrows(IllegalStateException.class,
                () -> os.registrarDiagnostico("Diagnóstico tardio."));
    }

    @Test
    @DisplayName("deve lançar exceção ao encaminhar para orçamento sem diagnóstico")
    void deveLancarExcecaoAoEncaminharSemDiagnostico() {
        OrdemDeServico os = new OrdemDeServico("os-001", veiculoPadrao);

        // status ainda é ABERTA — não pode pular para AGUARDANDO_ORCAMENTO
        assertThrows(IllegalStateException.class, os::encaminharParaOrcamento);
    }

    @Test
    @DisplayName("deve lançar exceção ao cancelar OS já concluída")
    void deveLancarExcecaoAoCancelarOsConcluida() {
        OrdemDeServico os = new OrdemDeServico("os-001", veiculoPadrao);
        os.registrarDiagnostico("Troca de óleo.");
        os.encaminharParaOrcamento();
        os.concluir();

        assertThrows(IllegalStateException.class, os::cancelar);
    }

    // ─── Criação inválida ─────────────────────────────────────────────────

    @Test
    @DisplayName("deve lançar exceção ao criar OS com ID nulo")
    void deveLancarExcecaoParaIdNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrdemDeServico(null, veiculoPadrao));
    }

    @Test
    @DisplayName("deve lançar exceção ao criar OS com veículo nulo")
    void deveLancarExcecaoParaVeiculoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrdemDeServico("os-001", null));
    }
}
