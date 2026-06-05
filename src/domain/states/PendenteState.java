package domain.states;

/**
 * Estado representando um orçamento Pendente.
 * Transições válidas: para Aprovado ou Rejeitado.
 */
public class PendenteState implements OrcamentoState {
    @Override
    public OrcamentoState aprovar() {
        return new AprovadoState();
    }

    @Override
    public OrcamentoState rejeitar() {
        return new RejeitadoState();
    }

    @Override
    public OrcamentoState concluir() {
        throw new IllegalStateException("Não é possível concluir um orçamento que está Pendente.");
    }

    @Override
    public String getDescricao() {
        return "Pendente";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        return getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
