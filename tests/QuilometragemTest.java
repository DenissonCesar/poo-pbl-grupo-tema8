package domain.triagem;

import domain.valueobjects.Quilometragem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Quilometragem (Value Object)")
class QuilometragemTest {

    // ─── Cenários de SUCESSO ───────────────────────────────────────────────

    @Test
    @DisplayName("deve criar quilometragem com valor positivo")
    void deveCriarQuilometragemPositiva() {
        Quilometragem km = new Quilometragem(50000);
        assertEquals(50000, km.getValor());
    }

    @Test
    @DisplayName("deve aceitar quilometragem zero (veículo novo)")
    void deveAceitarQuilometragemZero() {
        Quilometragem km = new Quilometragem(0);
        assertEquals(0, km.getValor());
    }

    @Test
    @DisplayName("duas quilometragens com mesmo valor devem ser iguais")
    void duasQuilometragemComMesmoValorDevemSerIguais() {
        Quilometragem km1 = new Quilometragem(30000);
        Quilometragem km2 = new Quilometragem(30000);
        assertEquals(km1, km2);
        assertEquals(km1.hashCode(), km2.hashCode());
    }

    // ─── Cenários de FALHA ────────────────────────────────────────────────

    @Test
    @DisplayName("deve lançar exceção para quilometragem negativa")
    void deveLancarExcecaoParaQuilometragemNegativa() {
        assertThrows(IllegalArgumentException.class, () -> new Quilometragem(-1));
    }
}
