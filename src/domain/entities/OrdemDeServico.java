package domain.entities;

import domain.valueobjects.ItemDeOrcamento;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade OrdemDeServico (Aggregate Root).
 * Toda interação com o orçamento associado é feita através desta raiz de agregado.
 */
public class OrdemDeServico {
    private final UUID id;
    private final Veiculo veiculo;
    private final String descricaoProblema;
    private final LocalDate dataEntrada;
    private Orcamento orcamento;

    public OrdemDeServico(UUID id, Veiculo veiculo, String descricaoProblema, LocalDate dataEntrada) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da ordem de serviço não pode ser nulo.");
        }
        if (veiculo == null) {
            throw new IllegalArgumentException("O veículo não pode ser nulo.");
        }
        if (descricaoProblema == null || descricaoProblema.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do problema não pode ser vazia.");
        }
        if (dataEntrada == null) {
            throw new IllegalArgumentException("A data de entrada não pode ser nula.");
        }
        this.id = id;
        this.veiculo = veiculo;
        this.descricaoProblema = descricaoProblema;
        this.dataEntrada = dataEntrada;
        this.orcamento = null;
    }

    public UUID getId() {
        return id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    /**
     * Inicia um novo orçamento associado a esta ordem de serviço.
     */
    public void iniciarOrcamento() {
        if (this.orcamento != null) {
            throw new IllegalStateException("Um orçamento já foi iniciado para esta Ordem de Serviço.");
        }
        this.orcamento = new Orcamento();
    }

    /**
     * Adiciona um item ao orçamento associado.
     * Valida se o orçamento foi iniciado.
     */
    public void adicionarItemAoOrcamento(ItemDeOrcamento item) {
        if (this.orcamento == null) {
            throw new IllegalStateException("Nenhum orçamento foi iniciado para esta Ordem de Serviço.");
        }
        this.orcamento.adicionarItem(item);
    }

    /**
     * Aprova o orçamento associado.
     */
    public void aprovarOrcamento() {
        if (this.orcamento == null) {
            throw new IllegalStateException("Nenhum orçamento foi iniciado para esta Ordem de Serviço.");
        }
        this.orcamento.aprovar();
    }

    /**
     * Rejeita o orçamento associado.
     */
    public void rejeitarOrcamento() {
        if (this.orcamento == null) {
            throw new IllegalStateException("Nenhum orçamento foi iniciado para esta Ordem de Serviço.");
        }
        this.orcamento.rejeitar();
    }

    /**
     * Conclui o orçamento associado.
     */
    public void concluirOrcamento() {
        if (this.orcamento == null) {
            throw new IllegalStateException("Nenhum orçamento foi iniciado para esta Ordem de Serviço.");
        }
        this.orcamento.concluir();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdemDeServico that = (OrdemDeServico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("OS %s - Veículo: %s - Entrada: %s", id, veiculo.getPlaca(), dataEntrada);
    }
}
