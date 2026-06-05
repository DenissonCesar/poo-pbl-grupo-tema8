import domain.valueobjects.Placa;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlacaTest {

    @Test
    public void deveCriarPlacaValidaComFormatoAntigo() {
        Placa placa = new Placa("ABC-1234");
        assertEquals("ABC-1234", placa.getValor());
    }

    @Test
    public void deveCriarPlacaValidaComFormatoMercosul() {
        Placa placa = new Placa("ABC1D23");
        assertEquals("ABC1D23", placa.getValor());
    }

    @Test
    public void deveLancarexcecaoParaPlacaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Placa("AB-123");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Placa("ABC1234");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Placa("ABC12D3");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Placa(null);
        });
    }

    @Test
    public void deveCompararPlacasCorretamenteComEqualsEHashCode() {
        Placa placa1 = new Placa("ABC-1234");
        Placa placa2 = new Placa("ABC-1234");
        Placa placa3 = new Placa("ABC1D23");

        assertEquals(placa1, placa2);
        assertNotEquals(placa1, placa3);
        assertEquals(placa1.hashCode(), placa2.hashCode());
    }
}
