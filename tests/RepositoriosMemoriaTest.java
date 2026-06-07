package application;

import domain.entities.OrdemDeServico;
import domain.entities.StatusOrdem;
import domain.entities.Veiculo;
import domain.valueobjects.ItemOrcamento;
import infrastructure.repositories.OrdemDeServicoRepositorioMemoria;
import infrastructure.repositories.VeiculoRepositorioMemoria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Repositórios em Memória")
class RepositoriosMemoriaTest {

    private OrdemDeServicoRepositorioMemoria ordemRepo;
    private VeiculoRepositorioMemoria        veiculoRepo;

    @BeforeEach
    void setUp() {
        ordemRepo   = new OrdemDeServicoRepositorioMemoria();
        veiculoRepo = new VeiculoRepositorioMemoria();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // OrdemDeServicoRepositorioMemoria
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("OrdemRepo: deve salvar e recuperar por ID")
    void ordemRepo_deveSalvarEBuscarPorId() {
        OrdemDeServico os = new OrdemDeServico("OS-001", "VEI-001", "Problema no freio", "João");
        ordemRepo.salvar(os);

        Optional<OrdemDeServico> encontrado = ordemRepo.buscarPorId("OS-001");

        assertTrue(encontrado.isPresent());
        assertEquals("OS-001", encontrado.get().getId());
    }

    @Test
    @DisplayName("OrdemRepo: deve retornar Optional vazio para ID inexistente")
    void ordemRepo_deveRetornarVazioParaIdInexistente() {
        assertTrue(ordemRepo.buscarPorId("NAO-EXISTE").isEmpty());
    }

    @Test
    @DisplayName("OrdemRepo: deve lançar exceção ao salvar ID duplicado")
    void ordemRepo_deveLancarExcecaoAoSalvarDuplicado() {
        OrdemDeServico os = new OrdemDeServico("OS-001", "VEI-001", "Problema", "João");
        ordemRepo.salvar(os);

        assertThrows(IllegalStateException.class, () -> ordemRepo.salvar(os));
    }

    @Test
    @DisplayName("OrdemRepo: deve lançar exceção ao atualizar ID inexistente")
    void ordemRepo_deveLancarExcecaoAoAtualizarInexistente() {
        OrdemDeServico os = new OrdemDeServico("OS-NAOEXISTE", "VEI-001", "Problema", "João");

        assertThrows(NoSuchElementException.class, () -> ordemRepo.atualizar(os));
    }

    @Test
    @DisplayName("OrdemRepo: deve filtrar por veiculoId")
    void ordemRepo_deveFiltrarPorVeiculo() {
        ordemRepo.salvar(new OrdemDeServico("OS-001", "VEI-001", "Problema A", "João"));
        ordemRepo.salvar(new OrdemDeServico("OS-002", "VEI-002", "Problema B", "Maria"));
        ordemRepo.salvar(new OrdemDeServico("OS-003", "VEI-001", "Problema C", "João"));

        // Finaliza OS-001 para que OS-003 possa existir no mesmo veículo
        OrdemDeServico os1 = ordemRepo.buscarPorId("OS-001").orElseThrow();
        os1.adicionarItem(new ItemOrcamento("P1", "Peça", 1, 10.0, ItemOrcamento.Tipo.PECA));
        os1.enviarParaAprovacao("Diagnóstico", null);
        os1.aprovar();
        os1.iniciarExecucao();
        os1.finalizar();
        ordemRepo.atualizar(os1);

        List<OrdemDeServico> doVeiculo1 = ordemRepo.buscarPorVeiculo("VEI-001");

        assertEquals(2, doVeiculo1.size());
    }

    @Test
    @DisplayName("OrdemRepo: deve filtrar por status")
    void ordemRepo_deveFiltrarPorStatus() {
        OrdemDeServico os1 = new OrdemDeServico("OS-001", "VEI-001", "Problema A", "João");
        OrdemDeServico os2 = new OrdemDeServico("OS-002", "VEI-002", "Problema B", "Maria");

        // Avança OS-002 para AGUARDANDO_APROVACAO
        os2.adicionarItem(new ItemOrcamento("P1", "Peça", 1, 50.0, ItemOrcamento.Tipo.PECA));
        os2.enviarParaAprovacao("Diagnóstico feito", null);

        ordemRepo.salvar(os1);
        ordemRepo.salvar(os2);

        List<OrdemDeServico> aguardandoDiag =
                ordemRepo.buscarPorStatus(StatusOrdem.AGUARDANDO_DIAGNOSTICO);
        List<OrdemDeServico> aguardandoAprov =
                ordemRepo.buscarPorStatus(StatusOrdem.AGUARDANDO_APROVACAO);

        assertEquals(1, aguardandoDiag.size());
        assertEquals(1, aguardandoAprov.size());
        assertEquals("OS-001", aguardandoDiag.get(0).getId());
        assertEquals("OS-002", aguardandoAprov.get(0).getId());
    }

    @Test
    @DisplayName("OrdemRepo: deve remover ordem existente")
    void ordemRepo_deveRemoverOrdem() {
        ordemRepo.salvar(new OrdemDeServico("OS-001", "VEI-001", "Problema", "João"));
        assertEquals(1, ordemRepo.tamanho());

        ordemRepo.remover("OS-001");

        assertEquals(0, ordemRepo.tamanho());
        assertTrue(ordemRepo.buscarPorId("OS-001").isEmpty());
    }

    @Test
    @DisplayName("OrdemRepo: deve lançar exceção ao remover ID inexistente")
    void ordemRepo_deveLancarExcecaoAoRemoverInexistente() {
        assertThrows(NoSuchElementException.class,
                () -> ordemRepo.remover("NAO-EXISTE"));
    }

    @Test
    @DisplayName("OrdemRepo: listarTodas deve retornar todas as ordens")
    void ordemRepo_deveListarTodas() {
        ordemRepo.salvar(new OrdemDeServico("OS-001", "VEI-001", "Problema A", "João"));
        ordemRepo.salvar(new OrdemDeServico("OS-002", "VEI-002", "Problema B", "Maria"));

        assertEquals(2, ordemRepo.listarTodas().size());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // VeiculoRepositorioMemoria
    // ══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("VeiculoRepo: deve salvar e recuperar por ID")
    void veiculoRepo_deveSalvarEBuscarPorId() {
        veiculoRepo.salvar(veiculoValido("VEI-001", "ABC1234"));

        Optional<Veiculo> encontrado = veiculoRepo.buscarPorId("VEI-001");

        assertTrue(encontrado.isPresent());
        assertEquals("VEI-001", encontrado.get().getId());
    }

    @Test
    @DisplayName("VeiculoRepo: deve buscar por placa case-insensitive")
    void veiculoRepo_deveBuscarPorPlacaCaseInsensitive() {
        veiculoRepo.salvar(veiculoValido("VEI-001", "ABC1234"));

        assertTrue(veiculoRepo.buscarPorPlaca("abc1234").isPresent());
        assertTrue(veiculoRepo.buscarPorPlaca("ABC-1234").isPresent());
        assertTrue(veiculoRepo.buscarPorPlaca("ABC1234").isPresent());
    }

    @Test
    @DisplayName("VeiculoRepo: deve lançar exceção ao salvar placa duplicada")
    void veiculoRepo_deveLancarExcecaoParaPlacaDuplicada() {
        veiculoRepo.salvar(veiculoValido("VEI-001", "ABC1234"));

        Veiculo outro = new Veiculo("VEI-002", "ABC1234", "VW", "Gol", 2019,
                "Ana Paula", "+55 82 91111-2222");

        assertThrows(IllegalStateException.class, () -> veiculoRepo.salvar(outro));
    }

    @Test
    @DisplayName("VeiculoRepo: deve retornar Optional vazio para placa inexistente")
    void veiculoRepo_deveRetornarVazioParaPlacaInexistente() {
        assertTrue(veiculoRepo.buscarPorPlaca("XYZ9999").isEmpty());
    }

    @Test
    @DisplayName("VeiculoRepo: deve remover veículo existente")
    void veiculoRepo_deveRemoverVeiculo() {
        veiculoRepo.salvar(veiculoValido("VEI-001", "ABC1234"));
        veiculoRepo.remover("VEI-001");

        assertTrue(veiculoRepo.buscarPorId("VEI-001").isEmpty());
    }

    @Test
    @DisplayName("VeiculoRepo: deve lançar exceção ao remover ID inexistente")
    void veiculoRepo_deveLancarExcecaoAoRemoverInexistente() {
        assertThrows(NoSuchElementException.class,
                () -> veiculoRepo.remover("NAO-EXISTE"));
    }

    @Test
    @DisplayName("VeiculoRepo: listarTodos deve retornar todos os veículos")
    void veiculoRepo_deveListarTodos() {
        veiculoRepo.salvar(veiculoValido("VEI-001", "ABC1234"));
        veiculoRepo.salvar(veiculoValido("VEI-002", "XYZ9876"));

        assertEquals(2, veiculoRepo.listarTodos().size());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Veiculo veiculoValido(String id, String placa) {
        return new Veiculo(id, placa, "Fiat", "Uno", 2020,
                "Carlos Silva", "+55 82 99999-0000");
    }
}
