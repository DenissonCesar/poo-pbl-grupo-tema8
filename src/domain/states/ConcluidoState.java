package domain.states;

public class ConcluidoState implements StatusOrcamento {
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
        return "Concluido";
    }
}
