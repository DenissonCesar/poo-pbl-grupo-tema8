package domain.services;

/**
 * Abstração do serviço de notificação via WhatsApp.
 *
 * Definida em domain/services/ como uma Port (hexagonal architecture).
 * A implementação fake fica em infrastructure/notifications/.
 * Em produção, substitua por uma implementação real (ex.: Twilio, Meta Cloud API).
 */
public interface INotificacaoWhatsApp {

    /**
     * Resultado do envio de uma mensagem.
     * Classe imutável aninhada para evitar dependência de classes externas.
     */
    class ResultadoEnvio {
        private final boolean sucesso;
        private final String  mensagemId;
        private final String  erro;

        private ResultadoEnvio(boolean sucesso, String mensagemId, String erro) {
            this.sucesso    = sucesso;
            this.mensagemId = mensagemId;
            this.erro       = erro;
        }

        public static ResultadoEnvio sucesso(String mensagemId) {
            return new ResultadoEnvio(true, mensagemId, null);
        }

        public static ResultadoEnvio falha(String motivo) {
            return new ResultadoEnvio(false, null, motivo);
        }

        public boolean isSucesso()    { return sucesso; }
        public String  getMensagemId(){ return mensagemId; }
        public String  getErro()      { return erro; }
    }

    /**
     * Envia uma mensagem de texto livre.
     */
    ResultadoEnvio enviarMensagem(String telefone, String nomeDestinatario,
                                  String mensagem, String ordemId);

    /**
     * Notificação padrão: orçamento disponível para aprovação do cliente.
     */
    ResultadoEnvio notificarOrcamentoDisponivel(String telefone, String nome,
                                                String ordemId, double valorTotal,
                                                String descricaoServico);

    /**
     * Notificação padrão: serviço finalizado, veículo disponível para retirada.
     */
    ResultadoEnvio notificarServicoFinalizado(String telefone, String nome,
                                              String ordemId, double valorTotal);
}
