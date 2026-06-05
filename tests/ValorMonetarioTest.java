import domain.valueobjects.ValorMonetario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValorMonetarioTest {

    @Test
    public void deveSomarCorretamenteDoisValores() {
        ValorMonetario v1 = new ValorMonetario(1000);
        ValorMonetario v2 = new ValorMonetario(550);
        ValorMonetario resultado = v1.somar(v2);

        assertEquals(1550, resultado.getCentavos());
    }

    @Test
    public void deveSubtrairCorretamenteDoisValores() {
        ValorMonetario v1 = new ValorMonetario(1000);
        ValorMonetario v2 = new ValorMonetario(450);
        ValorMonetario resultado = v1.subtrair(v2);

        assertEquals(550, resultado.getCentavos());
    }

    @Test
    public void deveLancarExcecaoParaValorNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ValorMonetario(-100);
        });
    }

    @Test
    public void deveLancarExcecaoQuandoSubtracaoResultaEmNegativo() {
        ValorMonetario v1 = new ValorMonetario(500);
        ValorMonetario v2 = new ValorMonetario(1000);
        assertThrows(IllegalArgumentException.class, () -> {
            v1.subtrair(v2);
        });
    }

    @Test
    public void deveMultiplicarCorretamentePorQuantidade() {
        ValorMonetario v = new ValorMonetario(250);
        ValorMonetario resultado = v.multiplicar(3);

        assertEquals(750, resultado.getCentavos());
    }

    @Test
    public void deveCompararValoresMonetariosComEqualsEHashCode() {
        ValorMonetario v1 = new ValorMonetario(1000);
        ValorMonetario v2 = new ValorMonetario(1000);
        ValorMonetario v3 = new ValorMonetario(2000);

        assertEquals(v1, v2);
        assertNotEquals(v1, v3);
        assertEquals(v1.hashCode(), v2.hashCode());
    }
}
