package domain.valueobjects;

public class ItemDeOrcamento {
    private final String descricao;
    private final ValorMonetario valorUnitario;
    private final int quantidade;

    public ItemDeOrcamento(String descricao, ValorMonetario valorUnitario, int quantidade) {
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
        return valorUnitario;
    }
}
