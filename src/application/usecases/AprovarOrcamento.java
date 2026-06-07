package application.usecases;

import domain.entities.OrdemDeServico;
import domain.entities.StatusOrdem;
import domain.entities.Veiculo;
import domain.repositories.IOrdemDeServicoRepository;
import domain.repositories.IVeiculoRepository;
import domain.services.INotificacaoWhatsApp;

import java.util.NoSuchElementException;

/**
 * Caso de Uso: Aprovar (ou Recusar) o Orçamento pelo Cliente.
 * Contexto DDD: Orçamentação / Fluxo de Aprovação (State Pattern)
 *
 * Regras de negócio:
 * - A O.S. deve estar no status AGUARDANDO_APROVACAO.
 * - Aprovação → status APROVADA.
 * - Recusa    → status RECUSADA.
 * - Em ambos os casos, o cliente é notificado via WhatsApp.
 * - Falha na notificação NÃO reverte a transação de estado.
 */
public class AprovarOrcamento {

    private final IOrdemDeServicoRepository ordemRepo;
    private final IVeiculoRepository        veiculoRepo;
    private final INotificacaoWhatsApp      whatsApp;

    public AprovarOrcamento(IOrdemDeServicoRepository ordemRepo,
                            IVeiculoRepository veiculoRepo,
                            INotificacaoWhatsApp whatsApp) {
        this.ordemRepo   = ordemRepo;
        this.veiculoRepo = veiculoRepo;
        this.whatsApp    = whatsApp;
    }

    // ── DTO de Entrada ────────────────────────────────────────────────────────

    public static class Input {
        public final String  ordemId;
        /** {@code true} = cliente aprovou; {@code false} = recusou. */
        public final boolean aprovado;

        public Input(String ordemId, boolean aprovado) {
            this.ordemId  = ordemId;
            this.aprovado = aprovado;
        }
    }

    // ── DTO de Saída ──────────────────────────────────────────────────────────

    public static class Output {
        public final String  ordemId;
        public final String  statusAnterior;
        public final String  statusAtual;
        public final double  valorTotal;
        public final boolean notificacaoEnviada;
        public final String  mensagem;

        Output(OrdemDeServico ordem, StatusOrdem statusAnterior,
               boolean aprovado, boolean notificacaoEnviada) {
            this.ordemId            = ordem.getId();
            this.statusAnterior     = statusAnterior.name();
            this.statusAtual        = ordem.getStatus().name();
            this.valorTotal         = ordem.getValorTotal();
            this.notificacaoEnviada = notificacaoEnviada;
            this.mensagem = aprovado
                    ? "Orçamento da O.S. #" + ordem.getId() + " aprovado. Serviço aguardando início."
                    : "Orçamento da O.S. #" + ordem.getId() + " recusado pelo cliente.";
        }
    }

    // ── Execução ──────────────────────────────────────────────────────────────

    public Output executar(Input input) {
        // 1. Busca a O.S.
        OrdemDeServico ordem = ordemRepo.buscarPorId(input.ordemId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Ordem de Serviço #" + input.ordemId + " não encontrada."));

        StatusOrdem statusAnterior = ordem.getStatus();

        // 2. Aplica a transição de estado (a entidade valida o status)
        if (input.aprovado) {
            ordem.aprovar();
        } else {
            ordem.recusar();
        }

        // 3. Persiste
        ordemRepo.atualizar(ordem);

        // 4. Notifica o cliente — falha não reverte o estado
        boolean notificacaoEnviada = false;
        try {
            Veiculo veiculo = veiculoRepo.buscarPorId(ordem.getVeiculoId()).orElse(null);
            if (veiculo != null) {
                String mensagemWpp = input.aprovado
                        ? String.format("Olá, %s! ✅ Seu orçamento foi *aprovado*. " +
                                        "Iniciaremos o serviço em breve.", veiculo.getProprietarioNome())
                        : String.format("Olá, %s! ❌ Seu orçamento foi *recusado*. " +
                                        "Entre em contato para mais informações.", veiculo.getProprietarioNome());

                INotificacaoWhatsApp.ResultadoEnvio resultado = whatsApp.enviarMensagem(
                        veiculo.getProprietarioTelefone(),
                        veiculo.getProprietarioNome(),
                        mensagemWpp,
                        ordem.getId());
                notificacaoEnviada = resultado.isSucesso();
            }
        } catch (Exception e) {
            System.err.println("[AprovarOrcamento] Falha ao enviar notificação WhatsApp: " + e.getMessage());
        }

        System.out.printf("[AprovarOrcamento] O.S. #%s %s | WhatsApp: %s%n",
                ordem.getId(), ordem.getStatus(), notificacaoEnviada ? "✓" : "✗");

        return new Output(ordem, statusAnterior, input.aprovado, notificacaoEnviada);
    }
}
