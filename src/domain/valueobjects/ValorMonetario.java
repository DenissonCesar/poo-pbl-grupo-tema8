package domain.valueobjects;

import java.util.Objects;

/**
 * Representa um valor monetário imutável expresso em centavos.
 * Evita o uso de tipos de ponto flutuante para garantir precisão e impede valores negativos.
 */
public class ValorMonetario {
    private final long centavos;

    public ValorMonetario(long centavos) {
        if (centavos < 0) {
            throw new IllegalArgumentException("O valor monetário não pode ser negativo.");
        }
        this.centavos = centavos;
    }

    public long getCentavos() {
        return centavos;
    }

    public ValorMonetario somar(ValorMonetario outro) {
        if (outro == null) {
            throw new IllegalArgumentException("O valor a somar não pode ser nulo.");
        }
        return new ValorMonetario(this.centavos + outro.centavos);
    }

    public ValorMonetario subtrair(ValorMonetario outro) {
        if (outro == null) {
            throw new IllegalArgumentException("O valor a subtrair não pode ser nulo.");
        }
        return new ValorMonetario(this.centavos - outro.centavos);
    }

    public ValorMonetario multiplicar(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade de multiplicação não pode ser negativa.");
        }
        return new ValorMonetario(this.centavos * quantidade);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValorMonetario that = (ValorMonetario) o;
        return centavos == that.centavos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(centavos);
    }

    @Override
    public String toString() {
        long reais = centavos / 100;
        long cents = Math.abs(centavos % 100);
        return String.format("R$ %d,%02d", reais, cents);
    }
}
