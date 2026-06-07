package infrastructure.notifications;

import domain.services.INotificacaoWhatsApp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Implementação FAKE (simulada) do serviço de WhatsApp.
 *
 * Não realiza chamadas HTTP reais. Registra todas as mensagens em memória
 * para inspeção nos testes. O flag {@code simularFalha} permite testar
 * cenários de erro sem infraestrutura real.
 *
 * Em produção, substitua esta classe por uma implementação concreta
 * (ex.: Twilio, Meta Cloud API) sem alterar nenhum caso de uso.
 */
public class FakeWhatsAppNotificacao implements INotificacaoWhatsApp {

    // ── Registro de mensagens enviadas ────────────────────────────────────────

    public static class MensagemRegistrada {
        private final String        mensagemId;
        private final String        telefone;
        private final String        nomeDestinatario;
        private final String        conteudo;
        private final String        ordemId;
        private final LocalDateTime enviadaEm;

        MensagemRegistrada(String mensagemId, String telefone, String nomeDestinatario,
                           String conteudo, String ordemId) {
            this.mensagemId       = mensagemId;
            this.telefone         = telefone;
            this.nomeDestinatario = nomeDestinatario;
            this.conteudo         = conteudo;
            this.ordemId          = ordemId;
            this.enviadaEm        = LocalDateTime.now();
        }

        public String        getMensagemId()       { return mensagemId; }
        public String        getTelefone()         { return telefone; }
        public String        getNomeDestinatario() { return nomeDestinatario; }
        public String        getConteudo()         { return conteudo; }
        public String        getOrdemId()          { return ordemId; }
        public LocalDateTime getEnviadaEm()        { return enviadaEm; }
    }

    // ── Estado interno ────────────────────────────────────────────────────────

    private final List<MensagemRegistrada> historico = new ArrayList<>();

    /**
     * Quando {@code true}, o próximo envio retorna falha simulada.
     * Útil para testar tratamento de erro nos casos de uso.
     */
    public boolean simularFalha = false;

    // ── Interface INotificacaoWhatsApp ────────────────────────────────────────

    @Override
    public ResultadoEnvio enviarMensagem(String telefone, String nomeDestinatario,
                                         String mensagem, String ordemId) {
        if (simularFalha) {
            simularFalha = false; // reseta após um uso
            return ResultadoEnvio.falha("[FAKE] Timeout simulado ao conectar ao servidor WhatsApp.");
        }

        String id = "WA-FAKE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        MensagemRegistrada registro = new MensagemRegistrada(
                id, telefone, nomeDestinatario, mensagem, ordemId);
        historico.add(registro);
        imprimirNoConsole(registro);

        return ResultadoEnvio.sucesso(id);
    }

    @Override
    public ResultadoEnvio notificarOrcamentoDisponivel(String telefone, String nome,
                                                       String ordemId, double valorTotal,
                                                       String descricaoServico) {
        String mensagem = String.format(
                "Olá, %s! \n\n" +
                        "Seu orçamento para a O.S. #%s está pronto.\n\n" +
                        "*Serviço:* %s\n" +
                        "*Valor total:* R$ %.2f\n\n" +
                        "Responda *SIM* para aprovar ou *NÃO* para recusar.\n\n" +
                        "_Oficina Mecânica — Sistema de Gestão_",
                nome, ordemId, descricaoServico, valorTotal);

        return enviarMensagem(telefone, nome, mensagem, ordemId);
    }

    @Override
    public ResultadoEnvio notificarServicoFinalizado(String telefone, String nome,
                                                     String ordemId, double valorTotal) {
        String mensagem = String.format(
                "Olá, %s! \n\n" +
                        "O serviço da O.S. #%s foi *concluído com sucesso*.\n\n" +
                        "*Total a pagar:* R$ %.2f\n\n" +
                        "Seu veículo está pronto para retirada. Obrigado pela confiança!\n\n" +
                        "_Oficina Mecânica — Sistema de Gestão_",
                nome, ordemId, valorTotal);

        return enviarMensagem(telefone, nome, mensagem, ordemId);
    }

    // ── Helpers de inspeção (para testes) ─────────────────────────────────────

    public List<MensagemRegistrada> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public List<MensagemRegistrada> getMensagensParaTelefone(String telefone) {
        List<MensagemRegistrada> resultado = new ArrayList<>();
        for (MensagemRegistrada m : historico)
            if (m.getTelefone().equals(telefone)) resultado.add(m);
        return resultado;
    }

    public List<MensagemRegistrada> getMensagensParaOrdem(String ordemId) {
        List<MensagemRegistrada> resultado = new ArrayList<>();
        for (MensagemRegistrada m : historico)
            if (m.getOrdemId().equals(ordemId)) resultado.add(m);
        return resultado;
    }

    public void limparHistorico() {
        historico.clear();
    }

    // ── Impressão no console ──────────────────────────────────────────────────

    private void imprimirNoConsole(MensagemRegistrada m) {
        String sep = "═".repeat(58);
        String fmt = "dd/MM/yyyy HH:mm:ss";
        System.out.println("\n" + sep);
        System.out.println("[FAKE WhatsApp] Mensagem enviada");
        System.out.println(sep);
        System.out.println("ID        : " + m.getMensagemId());
        System.out.println("Para      : " + m.getNomeDestinatario() + " (" + m.getTelefone() + ")");
        System.out.println("Ordem     : #" + m.getOrdemId());
        System.out.println("Enviada em: " +
                m.getEnviadaEm().format(DateTimeFormatter.ofPattern(fmt)));
        System.out.println("─".repeat(58));
        System.out.println("Mensagem:");
        System.out.println(m.getConteudo());
        System.out.println(sep + "\n");
    }
}
