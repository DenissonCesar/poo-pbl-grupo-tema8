package domain.valueobjects;

import java.util.Objects;

/**
 * Representa um item de um orçamento, composto por descrição, valor unitário e quantidade.
 * É um objeto de valor imutável.
 */
public class ItemDeOrcamento {
    private final String descricao;
    private final ValorMonetario valorUnitario;
    private final int quantidade;

    public ItemDeOrcamento(String descricao, ValorMonetario valorUnitario, int quantidade) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do item de orçamento não pode ser vazia.");
        }
        if (valorUnitario == null) {
            throw new IllegalArgumentException("O valor unitário do item de orçamento não pode ser nulo.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        this.descricao = descricao;
        this.valorUnitario = valorUnitario;
        this.quantidade = quantidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public ValorMonetario getValorUnitario() {
        return valorUnitario;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public ValorMonetario subtotal() {
        return valorUnitario.multiplicar(quantidade);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDeOrcamento that = (ItemDeOrcamento) o;
        return quantidade == that.quantidade &&
                Objects.equals(descricao, that.descricao) &&
                Objects.equals(valorUnitario, that.valorUnitario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descricao, valorUnitario, quantidade);
    }

    @Override
    public String toString() {
        return String.format("%s (%d x %s) - Subtotal: %s", descricao, quantidade, valorUnitario, subtotal());
    }
}
