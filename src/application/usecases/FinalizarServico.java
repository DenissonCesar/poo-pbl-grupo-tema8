package application.usecases;

import domain.entities.OrdemDeServico;
import domain.entities.StatusOrdem;
import domain.entities.Veiculo;
import domain.repositories.IOrdemDeServicoRepository;
import domain.repositories.IVeiculoRepository;
import domain.services.INotificacaoWhatsApp;
import domain.valueobjects.ItemOrcamento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Caso de Uso: Finalizar Serviço após execução completa.
 * Contexto DDD: Execução do Serviço
 *
 * Regras de negócio:
 * - Se a O.S. está APROVADA, inicia a execução e depois finaliza (conveniência).
 * - Se a O.S. já está EM_EXECUCAO, apenas finaliza.
 * - Ao finalizar, notifica o cliente via WhatsApp.
 * - Falha na notificação NÃO reverte a finalização.
 */
public class FinalizarServico {

    private final IOrdemDeServicoRepository ordemRepo;
    private final IVeiculoRepository        veiculoRepo;
    private final INotificacaoWhatsApp      whatsApp;

    public FinalizarServico(IOrdemDeServicoRepository ordemRepo,
                            IVeiculoRepository veiculoRepo,
                            INotificacaoWhatsApp whatsApp) {
        this.ordemRepo   = ordemRepo;
        this.veiculoRepo = veiculoRepo;
        this.whatsApp    = whatsApp;
    }

    // ── DTO de Entrada ────────────────────────────────────────────────────────

    public static class Input {
        public final String ordemId;

        public Input(String ordemId) {
            this.ordemId = ordemId;
        }
    }

    // ── DTO de Saída ──────────────────────────────────────────────────────────

    public static class ItemResumo {
        public final String descricao;
        public final int    quantidade;
        public final double subtotal;

        ItemResumo(ItemOrcamento item) {
            this.descricao  = item.getDescricao();
            this.quantidade = item.getQuantidade();
            this.subtotal   = item.getSubtotal();
        }
    }

    public static class Output {
        public final String           ordemId;
        public final String           veiculoId;
        public final String           statusFinal;
        public final double           valorTotal;
        public final LocalDateTime    dataAbertura;
        public final LocalDateTime    dataFinalizacao;
        public final boolean          notificacaoEnviada;
        public final List<ItemResumo> itens;
        public final String           mensagem;

        Output(OrdemDeServico ordem, boolean notificacaoEnviada) {
            this.ordemId            = ordem.getId();
            this.veiculoId          = ordem.getVeiculoId();
            this.statusFinal        = ordem.getStatus().name();
            this.valorTotal         = ordem.getValorTotal();
            this.dataAbertura       = ordem.getDataAbertura();
            this.dataFinalizacao    = ordem.getDataFinalizacao();
            this.notificacaoEnviada = notificacaoEnviada;
            this.mensagem = String.format(
                    "Serviço da O.S. #%s finalizado. Total: R$ %.2f.",
                    ordem.getId(), ordem.getValorTotal());

            List<ItemResumo> lista = new ArrayList<>();
            for (ItemOrcamento item : ordem.getItens()) lista.add(new ItemResumo(item));
            this.itens = java.util.Collections.unmodifiableList(lista);
        }
    }

    // ── Execução ──────────────────────────────────────────────────────────────

    public Output executar(Input input) {
        // 1. Busca a O.S.
        OrdemDeServico ordem = ordemRepo.buscarPorId(input.ordemId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Ordem de Serviço #" + input.ordemId + " não encontrada."));

        // 2. Se APROVADA, inicia execução antes de finalizar (conveniência)
        if (ordem.getStatus() == StatusOrdem.APROVADA) {
            ordem.iniciarExecucao();
        }

        // 3. Finaliza (entidade valida que está EM_EXECUCAO)
        ordem.finalizar();

        // 4. Persiste
        ordemRepo.atualizar(ordem);

        // 5. Notifica o cliente — falha não reverte
        boolean notificacaoEnviada = false;
        try {
            Veiculo veiculo = veiculoRepo.buscarPorId(ordem.getVeiculoId()).orElse(null);
            if (veiculo != null) {
                INotificacaoWhatsApp.ResultadoEnvio resultado =
                        whatsApp.notificarServicoFinalizado(
                                veiculo.getProprietarioTelefone(),
                                veiculo.getProprietarioNome(),
                                ordem.getId(),
                                ordem.getValorTotal());
                notificacaoEnviada = resultado.isSucesso();
            }
        } catch (Exception e) {
            System.err.println("[FinalizarServico] Falha ao enviar notificação WhatsApp: " + e.getMessage());
        }

        System.out.printf("[FinalizarServico] O.S. #%s FINALIZADA | Total: R$ %.2f | WhatsApp: %s%n",
                ordem.getId(), ordem.getValorTotal(), notificacaoEnviada ? "✓" : "✗");

        return new Output(ordem, notificacaoEnviada);
    }
}
