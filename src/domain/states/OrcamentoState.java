package domain.states;

/**
 * Interface base para os estados de um Orçamento.
 * Define as transições válidas de negócio no fluxo do Orçamento.
 */
public interface OrcamentoState {
    OrcamentoState aprovar();
    OrcamentoState rejeitar();
    OrcamentoState concluir();
    String getDescricao();
}
