package domain.valueobjects;

import java.util.Objects;

/**
 * Representa uma Placa de veículo no formato brasileiro tradicional (ABC-1234)
 * ou no padrão Mercosul (ABC1D23).
 */
public class Placa {
    private final String valor;

    public Placa(String valor) {
        if (valor == null || (!valor.matches("^[A-Z]{3}-\\d{4}$") && !valor.matches("^[A-Z]{3}\\d[A-Z]\\d{2}$"))) {
            throw new IllegalArgumentException("Formato de placa inválido. Formatos aceitos: ABC-1234 ou ABC1D23.");
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Placa placa = (Placa) o;
        return Objects.equals(valor, placa.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
