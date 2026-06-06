package domain.triagem;

import domain.entities.Veiculo;
import domain.valueobjects.Placa;
import domain.valueobjects.Quilometragem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Veiculo (Entity)")
class VeiculoTest {

    private Placa placaValida;
    private Quilometragem kmInicial;

    @BeforeEach
    void setUp() {
        placaValida = new Placa("ABC-1234");
        kmInicial   = new Quilometragem(50000);
    }

    // ─── Cenários de SUCESSO ───────────────────────────────────────────────

    @Test
    @DisplayName("deve criar veículo com todos os campos válidos")
    void deveCriarVeiculoValido() {
        Veiculo v = new Veiculo("v-001", placaValida, "Fiat Uno", 2015, kmInicial);

        assertEquals("v-001",     v.getId());
        assertEquals(placaValida, v.getPlaca());
        assertEquals("Fiat Uno",  v.getModelo());
        assertEquals(2015,        v.getAnoFabricacao());
        assertEquals(kmInicial,   v.getQuilometragem());
    }

    @Test
    @DisplayName("deve atualizar quilometragem para valor maior")
    void deveAtualizarQuilometragemParaValorMaior() {
        Veiculo v = new Veiculo("v-001", placaValida, "Fiat Uno", 2015, kmInicial);
        Quilometragem novaKm = new Quilometragem(60000);

        v.atualizarQuilometragem(novaKm);

        assertEquals(novaKm, v.getQuilometragem());
    }

    @Test
    @DisplayName("dois veículos com mesmo ID devem ser iguais (identidade)")
    void doisVeiculosComMesmoIdDevemSerIguais() {
        Veiculo v1 = new Veiculo("v-001", placaValida, "Fiat Uno", 2015, kmInicial);
        Veiculo v2 = new Veiculo("v-001", new Placa("XYZ-9999"), "Honda Civic", 2020, new Quilometragem(0));

        assertEquals(v1, v2); // identidade por ID, não por valor
    }

    // ─── Cenários de FALHA ────────────────────────────────────────────────

    @Test
    @DisplayName("deve lançar exceção para ID nulo")
    void deveLancarExcecaoParaIdNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Veiculo(null, placaValida, "Fiat Uno", 2015, kmInicial));
    }

    @Test
    @DisplayName("deve lançar exceção para placa nula")
    void deveLancarExcecaoParaPlacaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Veiculo("v-001", null, "Fiat Uno", 2015, kmInicial));
    }

    @Test
    @DisplayName("deve lançar exceção para modelo nulo ou vazio")
    void deveLancarExcecaoParaModeloVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Veiculo("v-001", placaValida, "", 2015, kmInicial));
    }

    @Test
    @DisplayName("deve lançar exceção ao atualizar quilometragem para valor menor")
    void deveLancarExcecaoAoAtualizarKmParaValorMenor() {
        Veiculo v = new Veiculo("v-001", placaValida, "Fiat Uno", 2015, kmInicial);
        Quilometragem kmMenor = new Quilometragem(40000);

        assertThrows(IllegalArgumentException.class,
                () -> v.atualizarQuilometragem(kmMenor));
    }

    @Test
    @DisplayName("deve lançar exceção para ano de fabricação inválido (futuro)")
    void deveLancarExcecaoParaAnoFuturo() {
        int anoFuturo = java.time.Year.now().getValue() + 1;
        assertThrows(IllegalArgumentException.class,
                () -> new Veiculo("v-001", placaValida, "Fiat Uno", anoFuturo, kmInicial));
    }
}
