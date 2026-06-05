package domain.states;

/**
 * Estado representando um orçamento Aprovado.
 * Transições válidas: para Concluido.
 */
public class AprovadoState implements StatusOrcamento {
    @Override
    public StatusOrcamento aprovar() {
        throw new IllegalStateException("Não é possível aprovar um orçamento que já está Aprovado.");
    }

    @Override
    public StatusOrcamento rejeitar() {
        throw new IllegalStateException("Não é possível rejeitar um orçamento que já está Aprovado.");
    }

    @Override
    public StatusOrcamento concluir() {
        return new ConcluidoState();
    }

    @Override
    public String getDescricao() {
        return "Aprovado";
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
