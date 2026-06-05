package domain.states;

public interface StatusOrcamento {
    StatusOrcamento aprovar();
    StatusOrcamento rejeitar();
    StatusOrcamento concluir();
    String getDescricao();
}
