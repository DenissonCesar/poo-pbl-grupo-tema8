package domain.states;

/**
 * Estado representando um orçamento Rejeitado.
 * Não são permitidas transições adicionais.
 */
public class RejeitadoState implements OrcamentoState {
    @Override
    public OrcamentoState aprovar() {
        throw new IllegalStateException("Não é possível aprovar um orçamento que está Rejeitado.");
    }

    @Override
    public OrcamentoState rejeitar() {
        throw new IllegalStateException("Não é possível rejeitar um orçamento que já está Rejeitado.");
    }

    @Override
    public OrcamentoState concluir() {
        throw new IllegalStateException("Não é possível concluir um orçamento que está Rejeitado.");
    }

    @Override
    public String getDescricao() {
        return "Rejeitado";
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
