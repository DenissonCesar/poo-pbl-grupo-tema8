package application;

import application.usecases.AdicionarPecaAoOrcamento;
import domain.entities.OrdemDeServico;
import domain.valueobjects.ItemOrcamento;
import infrastructure.repositories.OrdemDeServicoRepositorioMemoria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC: AdicionarPecaAoOrcamento")
class AdicionarPecaAoOrcamentoTest {

    private OrdemDeServicoRepositorioMemoria ordemRepo;
    private AdicionarPecaAoOrcamento          useCase;

    @BeforeEach
    void setUp() {
        ordemRepo = new OrdemDeServicoRepositorioMemoria();
        useCase   = new AdicionarPecaAoOrcamento(ordemRepo);

        // OS base para os testes
        ordemRepo.salvar(new OrdemDeServico(
                "OS-001", "VEI-001", "Barulho no motor", "João"));
    }

    // ── Cenários de sucesso ───────────────────────────────────────────────────

    @Test
    @DisplayName("Deve adicionar peça e calcular subtotal corretamente")
    void deveAdicionarPecaComSubtotalCorreto() {
        AdicionarPecaAoOrcamento.Input input = new AdicionarPecaAoOrcamento.Input(
                "OS-001", "PECA-001", "Filtro de óleo", 2, 45.00,
                ItemOrcamento.Tipo.PECA);

        AdicionarPecaAoOrcamento.Output output = useCase.executar(input);

        assertEquals(90.00, output.itemSubtotal, 0.001);
        assertEquals(90.00, output.totalOrcamento, 0.001);
        assertEquals(1,     output.quantidadeItens);
    }

    @Test
    @DisplayName("Deve acumular total ao adicionar múltiplas peças")
    void deveAcumularTotalComMultiplasPecas() {
        useCase.executar(new AdicionarPecaAoOrcamento.Input(
                "OS-001", "PECA-001", "Filtro de óleo", 1, 45.00,
                ItemOrcamento.Tipo.PECA));

        AdicionarPecaAoOrcamento.Output output = useCase.executar(
                new AdicionarPecaAoOrcamento.Input(
                        "OS-001", "MO-001", "Mão de obra", 1, 200.00,
                        ItemOrcamento.Tipo.MAO_DE_OBRA));

        assertEquals(245.00, output.totalOrcamento, 0.001);
        assertEquals(2,      output.quantidadeItens);
    }

    // ── Cenários de falha ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção para O.S. inexistente")
    void deveLancarExcecaoParaOSInexistente() {
        assertThrows(java.util.NoSuchElementException.class,
                () -> useCase.executar(new AdicionarPecaAoOrcamento.Input(
                        "OS-INEXISTENTE", "P1", "Peça", 1, 10.00,
                        ItemOrcamento.Tipo.PECA)));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar peça duplicada")
    void deveLancarExcecaoParaPecaDuplicada() {
        AdicionarPecaAoOrcamento.Input input = new AdicionarPecaAoOrcamento.Input(
                "OS-001", "PECA-001", "Filtro", 1, 45.00,
                ItemOrcamento.Tipo.PECA);

        useCase.executar(input); // primeira vez — OK

        assertThrows(IllegalStateException.class,
                () -> useCase.executar(input)); // segunda vez — deve falhar
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar item com quantidade zero")
    void deveLancarExcecaoParaQuantidadeZero() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.executar(new AdicionarPecaAoOrcamento.Input(
                        "OS-001", "PECA-001", "Filtro", 0, 45.00,
                        ItemOrcamento.Tipo.PECA)));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar item com preço negativo")
    void deveLancarExcecaoParaPrecoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.executar(new AdicionarPecaAoOrcamento.Input(
                        "OS-001", "PECA-001", "Filtro", 1, -10.00,
                        ItemOrcamento.Tipo.PECA)));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar peça com O.S. em outro status")
    void deveLancarExcecaoSeOSNaoEstaEmDiagnostico() {
        // Avança a OS para fora do status AGUARDANDO_DIAGNOSTICO
        OrdemDeServico os = ordemRepo.buscarPorId("OS-001").orElseThrow();
        os.adicionarItem(new ItemOrcamento("P-TEMP", "Temp", 1, 10.0, ItemOrcamento.Tipo.PECA));
        os.enviarParaAprovacao("Diagnóstico feito", null);
        ordemRepo.atualizar(os);

        assertThrows(IllegalStateException.class,
                () -> useCase.executar(new AdicionarPecaAoOrcamento.Input(
                        "OS-001", "PECA-002", "Nova peça", 1, 50.00,
                        ItemOrcamento.Tipo.PECA)));
    }
}
