package application.usecases;

import domain.entities.OrdemDeServico;
import domain.entities.Veiculo;
import domain.repositories.IOrdemDeServicoRepository;
import domain.repositories.IVeiculoRepository;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Caso de Uso: Abrir Ordem de Serviço (check-in do veículo na oficina).
 * Contexto DDD: Triagem / Check-in
 *
 * Regras de negócio:
 * - O veículo deve estar cadastrado no sistema.
 * - Não pode existir outra O.S. aberta para o mesmo veículo.
 */
public class AbrirOrdemDeServico {

    private final IOrdemDeServicoRepository ordemRepo;
    private final IVeiculoRepository        veiculoRepo;

    public AbrirOrdemDeServico(IOrdemDeServicoRepository ordemRepo,
                               IVeiculoRepository veiculoRepo) {
        this.ordemRepo   = ordemRepo;
        this.veiculoRepo = veiculoRepo;
    }

    // ── DTO de Entrada ────────────────────────────────────────────────────────

    public static class Input {
        public final String ordemId;
        public final String veiculoId;
        public final String descricaoProblema;
        public final String mecanicoResponsavel;

        public Input(String ordemId, String veiculoId,
                     String descricaoProblema, String mecanicoResponsavel) {
            this.ordemId             = ordemId;
            this.veiculoId           = veiculoId;
            this.descricaoProblema   = descricaoProblema;
            this.mecanicoResponsavel = mecanicoResponsavel;
        }
    }

    // ── DTO de Saída ──────────────────────────────────────────────────────────

    public static class Output {
        public final String ordemId;
        public final String veiculoDescricao;
        public final String status;
        public final String mecanicoResponsavel;
        public final String mensagem;

        Output(OrdemDeServico ordem, Veiculo veiculo) {
            this.ordemId             = ordem.getId();
            this.veiculoDescricao    = veiculo.toString();
            this.status              = ordem.getStatus().name();
            this.mecanicoResponsavel = ordem.getMecanicoResponsavel();
            this.mensagem            = "O.S. #" + ordem.getId() +
                    " aberta com sucesso para o veículo " + veiculo + ".";
        }
    }

    // ── Execução ──────────────────────────────────────────────────────────────

    public Output executar(Input input) {
        // 1. Valida existência do veículo
        Veiculo veiculo = veiculoRepo.buscarPorId(input.veiculoId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Veículo com ID \"" + input.veiculoId +
                                "\" não encontrado. Realize o cadastro antes de abrir a O.S."));

        // 2. Verifica se já existe O.S. aberta para este veículo
        List<OrdemDeServico> ordensDoVeiculo =
                ordemRepo.buscarPorVeiculo(input.veiculoId);
        ordensDoVeiculo.stream()
                .filter(OrdemDeServico::estaAberta)
                .findFirst()
                .ifPresent(aberta -> {
                    throw new IllegalStateException(
                            "Já existe uma O.S. aberta para este veículo " +
                                    "(O.S. #" + aberta.getId() + ", status: " + aberta.getStatus() + ").");
                });

        // 3. Cria e persiste a nova O.S.
        OrdemDeServico novaOrdem = new OrdemDeServico(
                input.ordemId,
                input.veiculoId,
                input.descricaoProblema,
                input.mecanicoResponsavel
        );
        ordemRepo.salvar(novaOrdem);

        System.out.printf("[AbrirOrdemDeServico] O.S. #%s aberta — %s — Mecânico: %s%n",
                novaOrdem.getId(), veiculo, input.mecanicoResponsavel);

        return new Output(novaOrdem, veiculo);
    }
}
