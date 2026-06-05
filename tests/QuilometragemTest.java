import domain.valueobjects.Quilometragem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuilometragemTest {

    @Test
    public void deveCriarQuilometragemValida() {
        Quilometragem q = new Quilometragem(15000);
        assertEquals(15000, q.getValor());
    }

    @Test
    public void deveCriarQuilometragemZero() {
        Quilometragem q = new Quilometragem(0);
        assertEquals(0, q.getValor());
    }

    @Test
    public void deveLancarExcecaoParaQuilometragemNegativa() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Quilometragem(-1);
        });
    }

    @Test
    public void deveCompararQuilometragensCorretamenteComEqualsEHashCode() {
        Quilometragem q1 = new Quilometragem(100);
        Quilometragem q2 = new Quilometragem(100);
        Quilometragem q3 = new Quilometragem(200);

        assertEquals(q1, q2);
        assertNotEquals(q1, q3);
        assertEquals(q1.hashCode(), q2.hashCode());
    }
}
