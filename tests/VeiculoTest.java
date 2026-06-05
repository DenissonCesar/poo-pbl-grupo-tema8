import domain.entities.Veiculo;
import domain.valueobjects.Placa;
import domain.valueobjects.Quilometragem;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VeiculoTest {

    @Test
    public void deveCriarVeiculoComSucesso() {
        UUID id = UUID.randomUUID();
        Placa placa = new Placa("ABC-1234");
        Quilometragem q = new Quilometragem(10000);
        Veiculo veiculo = new Veiculo(id, placa, "Civic", "Honda", 2020, q);

        assertEquals(id, veiculo.getId());
        assertEquals(placa, veiculo.getPlaca());
        assertEquals("Civic", veiculo.getModelo());
        assertEquals("Honda", veiculo.getMarca());
        assertEquals(2020, veiculo.getAnoFabricacao());
        assertEquals(q, veiculo.getQuilometragem());
    }

    @Test
    public void deveAtualizarQuilometragemComSucesso() {
        Veiculo veiculo = new Veiculo(UUID.randomUUID(), new Placa("ABC-1234"), "Civic", "Honda", 2020, new Quilometragem(10000));
        
        veiculo.atualizarQuilometragem(new Quilometragem(15000));
        assertEquals(new Quilometragem(15000), veiculo.getQuilometragem());
    }

    @Test
    public void deveLancarExcecaoAoAtualizarQuilometragemParaValorMenor() {
        Veiculo veiculo = new Veiculo(UUID.randomUUID(), new Placa("ABC-1234"), "Civic", "Honda", 2020, new Quilometragem(10000));
        
        assertThrows(IllegalArgumentException.class, () -> {
            veiculo.atualizarQuilometragem(new Quilometragem(9999));
        });
    }
}
