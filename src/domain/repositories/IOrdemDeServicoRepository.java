package domain.repositories;

import domain.entities.OrdemDeServico;
import domain.entities.StatusOrdem;
import java.util.List;
import java.util.Optional;

public interface IOrdemDeServicoRepository {

    /** Persiste uma nova Ordem de Serviço. */
    void salvar(OrdemDeServico ordem);

    /** Atualiza uma Ordem de Serviço já existente. */
    void atualizar(OrdemDeServico ordem);

    /** Busca por ID; retorna Optional vazio se não encontrada. */
    Optional<OrdemDeServico> buscarPorId(String id);

    /** Retorna todas as ordens de um veículo específico. */
    List<OrdemDeServico> buscarPorVeiculo(String veiculoId);

    /** Retorna ordens filtradas por status. */
    List<OrdemDeServico> buscarPorStatus(StatusOrdem status);

    /** Lista todas as ordens cadastradas. */
    List<OrdemDeServico> listarTodas();

    /** Remove uma ordem (uso restrito — apenas testes/admin). */
    void remover(String id);
}
