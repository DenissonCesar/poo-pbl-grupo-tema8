package domain.states;

public class AprovadoState implements StatusOrcamento {
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
        return "Aprovado";
    }
}
