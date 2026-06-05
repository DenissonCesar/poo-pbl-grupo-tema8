package domain.states;

public class RejeitadoState implements StatusOrcamento {
    @Override
    public StatusOrcamento aprovar() {
        return this;
    }

    @Override
    public StatusOrcamento rejeitar() {
        return this;
    }

    @Override
    public StatusOrcamento concluir() {
        return this;
    }

    @Override
    public String getDescricao() {
        return "Rejeitado";
    }
}
