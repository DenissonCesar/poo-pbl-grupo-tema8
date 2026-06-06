import domain.entities.Veiculo;
import domain.entities.OrdemDeServico;
import domain.entities.Orcamento;
import domain.states.PendenteState;
import domain.states.AprovadoState;
import domain.states.ConcluidoState;
import domain.valueobjects.Placa;
import domain.valueobjects.Quilometragem;
import domain.valueobjects.ItemDeOrcamento;
import domain.valueobjects.ValorMonetario;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrdemDeServicoTest {

    private Veiculo criarVeiculoMock() {
        return new Veiculo(UUID.randomUUID(), new Placa("ABC-1234"), "Civic", "Honda", 2020, new Quilometragem(10000));
    }

    @Test
    public void deveLancarExcecaoAoAdicionarItemSemIniciarOrcamento() {
        Veiculo veiculo = criarVeiculoMock();
        OrdemDeServico os = new OrdemDeServico(UUID.randomUUID(), veiculo, "Barulho na suspensão", LocalDate.now());

        ValorMonetario valorUnitario = new ValorMonetario(15000);
        ItemDeOrcamento item = new ItemDeOrcamento("Amortecedor", valorUnitario, 2);

        assertThrows(IllegalStateException.class, () -> {
            os.adicionarItemAoOrcamento(item);
        });
    }

    @Test
    public void deveExecutarFluxoCompletoDoOrcamentoComSucesso() {
        Veiculo veiculo = criarVeiculoMock();
        OrdemDeServico os = new OrdemDeServico(UUID.randomUUID(), veiculo, "Revisão geral", LocalDate.now());

        assertNull(os.getOrcamento());

        // Inicia Orçamento
        os.iniciarOrcamento();
        assertNotNull(os.getOrcamento());
        assertTrue(os.getOrcamento().getStatus() instanceof PendenteState);

        // Adiciona itens
        ItemDeOrcamento item1 = new ItemDeOrcamento("Óleo de motor", new ValorMonetario(4500), 4);
        ItemDeOrcamento item2 = new ItemDeOrcamento("Filtro de óleo", new ValorMonetario(3000), 1);
        os.adicionarItemAoOrcamento(item1);
        os.adicionarItemAoOrcamento(item2);

        // Aprova orçamento
        os.aprovarOrcamento();
        assertTrue(os.getOrcamento().getStatus() instanceof AprovadoState);

        // Conclui orçamento
        os.concluirOrcamento();
        assertTrue(os.getOrcamento().getStatus() instanceof ConcluidoState);
    }

    @Test
    public void deveCalcularTotalCorretoComMultiplosItens() {
        Veiculo veiculo = criarVeiculoMock();
        OrdemDeServico os = new OrdemDeServico(UUID.randomUUID(), veiculo, "Revisão de freios", LocalDate.now());

        os.iniciarOrcamento();
        
        ItemDeOrcamento pastilhas = new ItemDeOrcamento("Pastilha de freio", new ValorMonetario(12000), 2);
        ItemDeOrcamento fluido = new ItemDeOrcamento("Fluido de freio", new ValorMonetario(3500), 1);
        
        os.adicionarItemAoOrcamento(pastilhas);
        os.adicionarItemAoOrcamento(fluido);

        ValorMonetario total = os.getOrcamento().calcularTotal();
        assertEquals(27500, total.getCentavos());
    }
}
