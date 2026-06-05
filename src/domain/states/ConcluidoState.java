package domain.states;

/**
 * Estado representando um orçamento Concluído.
 * Não são permitidas transições adicionais.
 */
public class ConcluidoState implements StatusOrcamento {
    @Override
    public StatusOrcamento aprovar() {
        throw new IllegalStateException("Não é possível aprovar um orçamento que está Concluido.");
    }

    @Override
    public StatusOrcamento rejeitar() {
        throw new IllegalStateException("Não é possível rejeitar um orçamento que está Concluido.");
    }

    @Override
    public StatusOrcamento concluir() {
        throw new IllegalStateException("Não é possível concluir um orçamento que já está Concluido.");
    }

    @Override
    public String getDescricao() {
        return "Concluido";
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
