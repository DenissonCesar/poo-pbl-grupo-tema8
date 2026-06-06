package domain.valueobjects;

import java.util.Objects;

/**
 * Representa a Quilometragem de um veículo, garantindo ser um valor numérico maior ou igual a zero.
 */
public class Quilometragem {
    private final int valor;

    public Quilometragem(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("A quilometragem não pode ser negativa.");
        }
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quilometragem that = (Quilometragem) o;
        return valor == that.valor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}
