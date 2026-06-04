package domain.triagem;

import domain.valueobjects.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Placa (Value Object)")
class PlacaTest {

    // ─── Cenários de SUCESSO ───────────────────────────────────────────────

    @Test
    @DisplayName("deve criar placa no formato antigo válido (AAA-9999)")
    void deveCriarPlacaFormatoAntigo() {
        Placa placa = new Placa("ABC-1234");
        assertEquals("ABC-1234", placa.getValor());
    }

    @Test
    @DisplayName("deve criar placa no formato Mercosul válido (AAA9A99)")
    void deveCriarPlacaFormatoMercosul() {
        Placa placa = new Placa("ABC1D23");
        assertEquals("ABC1D23", placa.getValor());
    }

    @Test
    @DisplayName("duas placas com mesmo valor devem ser iguais (imutabilidade e equals)")
    void duasPlacasComMesmoValorDevemSerIguais() {
        Placa p1 = new Placa("ABC-1234");
        Placa p2 = new Placa("ABC-1234");
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    @DisplayName("duas placas com valores diferentes devem ser diferentes")
    void duasPlacasComValoresDiferentesDevemSerDiferentes() {
        Placa p1 = new Placa("ABC-1234");
        Placa p2 = new Placa("XYZ-9999");
        assertNotEquals(p1, p2);
    }

    // ─── Cenários de FALHA ────────────────────────────────────────────────

    @Test
    @DisplayName("deve lançar exceção para placa nula")
    void deveLancarExcecaoParaPlacaNula() {
        assertThrows(IllegalArgumentException.class, () -> new Placa(null));
    }

    @Test
    @DisplayName("deve lançar exceção para placa vazia")
    void deveLancarExcecaoParaPlacaVazia() {
        assertThrows(IllegalArgumentException.class, () -> new Placa(""));
    }

    @Test
    @DisplayName("deve lançar exceção para formato de placa inválido")
    void deveLancarExcecaoParaFormatoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Placa("INVALIDA"));
    }

    @Test
    @DisplayName("deve lançar exceção para placa com letras minúsculas")
    void deveLancarExcecaoParaPlacaComLetrasMinusculas() {
        assertThrows(IllegalArgumentException.class, () -> new Placa("abc-1234"));
    }

    @Test
    @DisplayName("deve lançar exceção para placa com espaços")
    void deveLancarExcecaoParaPlacaComEspacos() {
        assertThrows(IllegalArgumentException.class, () -> new Placa("ABC 1234"));
    }
}
