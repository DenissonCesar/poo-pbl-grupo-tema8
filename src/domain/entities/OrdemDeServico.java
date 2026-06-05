package domain.entities;

import domain.valueobjects.ItemDeOrcamento;
import java.time.LocalDate;
import java.util.UUID;

public class OrdemDeServico {
    private final UUID id;
    private final Veiculo veiculo;
    private final String descricaoProblema;
    private final LocalDate dataEntrada;
    private Orcamento orcamento;

    public OrdemDeServico(UUID id, Veiculo veiculo, String descricaoProblema, LocalDate dataEntrada) {
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

    public void iniciarOrcamento() {
    }

    public void adicionarItemAoOrcamento(ItemDeOrcamento item) {
    }

    public void aprovarOrcamento() {
    }

    public void rejeitarOrcamento() {
    }

    public void concluirOrcamento() {
    }
}
