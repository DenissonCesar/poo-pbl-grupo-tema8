package domain.entities;

import domain.states.StatusOrcamento;
import domain.states.PendenteState;
import domain.valueobjects.ItemDeOrcamento;
import domain.valueobjects.ValorMonetario;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Orcamento {
    private final UUID id;
    private final List<ItemDeOrcamento> itens;
    private StatusOrcamento status;

    public Orcamento() {
        this.id = UUID.randomUUID();
        this.itens = new ArrayList<>();
        this.status = new PendenteState();
    }

    public UUID getId() {
        return id;
    }

    public List<ItemDeOrcamento> getItens() {
        return itens;
    }

    public StatusOrcamento getStatus() {
        return status;
    }

    public void adicionarItem(ItemDeOrcamento item) {
    }

    public ValorMonetario calcularTotal() {
        return new ValorMonetario(0);
    }

    public void aprovar() {
    }

    public void rejeitar() {
    }

    public void concluir() {
    }
}
