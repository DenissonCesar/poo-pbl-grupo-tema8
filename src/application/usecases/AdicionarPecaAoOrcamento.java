package application.usecases;

import domain.entities.OrdemDeServico;
import domain.repositories.IOrdemDeServicoRepository;
import domain.valueobjects.ItemOrcamento;

import java.util.NoSuchElementException;

/**
 * Caso de Uso: Adicionar Peça ou Serviço ao Orçamento de uma O.S.
 * Contexto DDD: Orçamentação / Estoque de Peças
 *
 * Regras de negócio:
 * - A O.S. deve existir e estar no status AGUARDANDO_DIAGNOSTICO.
 * - A mesma peça (mesmo pecaId) não pode ser adicionada duas vezes.
 */
public class AdicionarPecaAoOrcamento {

    private final IOrdemDeServicoRepository ordemRepo;

    public AdicionarPecaAoOrcamento(IOrdemDeServicoRepository ordemRepo) {
        this.ordemRepo = ordemRepo;
    }

    // ── DTO de Entrada ────────────────────────────────────────────────────────

    public static class Input {
        public final String           ordemId;
        public final String           pecaId;
        public final String           descricao;
        public final int              quantidade;
        public final double           precoUnitario;
        public final ItemOrcamento.Tipo tipo;

        public Input(String ordemId, String pecaId, String descricao,
                     int quantidade, double precoUnitario, ItemOrcamento.Tipo tipo) {
            this.ordemId      = ordemId;
            this.pecaId       = pecaId;
            this.descricao    = descricao;
            this.quantidade   = quantidade;
            this.precoUnitario = precoUnitario;
            this.tipo         = tipo;
        }
    }

    // ── DTO de Saída ──────────────────────────────────────────────────────────

    public static class Output {
        public final String ordemId;
        public final String itemDescricao;
        public final double itemSubtotal;
        public final double totalOrcamento;
        public final int    quantidadeItens;
        public final String mensagem;

        Output(OrdemDeServico ordem, ItemOrcamento item) {
            this.ordemId         = ordem.getId();
            this.itemDescricao   = item.getDescricao();
            this.itemSubtotal    = item.getSubtotal();
            this.totalOrcamento  = ordem.getValorTotal();
            this.quantidadeItens = ordem.getItens().size();
            this.mensagem = String.format(
                    "Item \"%s\" adicionado à O.S. #%s. Total: R$ %.2f",
                    item.getDescricao(), ordem.getId(), ordem.getValorTotal());
        }
    }

    // ── Execução ──────────────────────────────────────────────────────────────

    public Output executar(Input input) {
        // 1. Busca a O.S.
        OrdemDeServico ordem = ordemRepo.buscarPorId(input.ordemId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Ordem de Serviço #" + input.ordemId + " não encontrada."));

        // 2. Cria o Value Object (já valida internamente)
        ItemOrcamento novoItem = new ItemOrcamento(
                input.pecaId, input.descricao,
                input.quantidade, input.precoUnitario, input.tipo);

        // 3. Delega à entidade (que valida o estado e duplicidade)
        ordem.adicionarItem(novoItem);

        // 4. Persiste
        ordemRepo.atualizar(ordem);

        System.out.printf("[AdicionarPecaAoOrcamento] %s → O.S. #%s | Total: R$ %.2f%n",
                novoItem, input.ordemId, ordem.getValorTotal());

        return new Output(ordem, novoItem);
    }
}
