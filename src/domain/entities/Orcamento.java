package domain.entities;

import domain.states.StatusOrcamento;
import domain.states.PendenteState;
import domain.valueobjects.ItemDeOrcamento;
import domain.valueobjects.ValorMonetario;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade Orcamento.
 * Possui identificação única (ID), lista de itens e o status atual controlado pelo padrão State.
 */
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
        return Collections.unmodifiableList(itens);
    }

    public StatusOrcamento getStatus() {
        return status;
    }

    public void adicionarItem(ItemDeOrcamento item) {
        if (!(status instanceof PendenteState)) {
            throw new IllegalStateException("Não é possível adicionar itens a um orçamento que não está Pendente.");
        }
        if (item == null) {
            throw new IllegalArgumentException("O item não pode ser nulo.");
        }
        this.itens.add(item);
    }

    public ValorMonetario calcularTotal() {
        ValorMonetario total = new ValorMonetario(0);
        for (ItemDeOrcamento item : itens) {
            total = total.somar(item.subtotal());
        }
        return total;
    }

    public void aprovar() {
        this.status = this.status.aprovar();
    }

    public void rejeitar() {
        this.status = this.status.rejeitar();
    }

    public void concluir() {
        this.status = this.status.concluir();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Orcamento orcamento = (Orcamento) o;
        return Objects.equals(id, orcamento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
