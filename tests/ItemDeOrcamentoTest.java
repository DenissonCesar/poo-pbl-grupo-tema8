import domain.valueobjects.ItemDeOrcamento;
import domain.valueobjects.ValorMonetario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemDeOrcamentoTest {

    @Test
    public void deveCalcularSubtotalCorretamente() {
        ValorMonetario valorUnitario = new ValorMonetario(1500);
        ItemDeOrcamento item = new ItemDeOrcamento("Troca de óleo", valorUnitario, 3);

        ValorMonetario subtotal = item.subtotal();
        assertEquals(4500, subtotal.getCentavos());
    }

    @Test
    public void deveLancarExcecaoParaDescricaoVazia() {
        ValorMonetario valorUnitario = new ValorMonetario(1000);
        
        assertThrows(IllegalArgumentException.class, () -> {
            new ItemDeOrcamento("", valorUnitario, 1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new ItemDeOrcamento("   ", valorUnitario, 1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new ItemDeOrcamento(null, valorUnitario, 1);
        });
    }

    @Test
    public void deveLancarExcecaoParaQuantidadeZeroOuNegativa() {
        ValorMonetario valorUnitario = new ValorMonetario(1000);

        assertThrows(IllegalArgumentException.class, () -> {
            new ItemDeOrcamento("Item", valorUnitario, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new ItemDeOrcamento("Item", valorUnitario, -5);
        });
    }

    @Test
    public void deveCompararItensDeOrcamentoComEqualsEHashCode() {
        ValorMonetario valorUnitario1 = new ValorMonetario(1000);
        ValorMonetario valorUnitario2 = new ValorMonetario(1000);
        
        ItemDeOrcamento item1 = new ItemDeOrcamento("Alinhamento", valorUnitario1, 2);
        ItemDeOrcamento item2 = new ItemDeOrcamento("Alinhamento", valorUnitario2, 2);
        ItemDeOrcamento item3 = new ItemDeOrcamento("Balanceamento", valorUnitario1, 2);

        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
        assertEquals(item1.hashCode(), item2.hashCode());
    }
}
