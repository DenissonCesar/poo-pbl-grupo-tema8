package domain.repositories;

import domain.entities.Veiculo;
import java.util.List;
import java.util.Optional;

public interface IVeiculoRepository {

    /** Persiste um novo veículo. */
    void salvar(Veiculo veiculo);

    /** Busca por ID. */
    Optional<Veiculo> buscarPorId(String id);

    /** Busca pela placa (case-insensitive). */
    Optional<Veiculo> buscarPorPlaca(String placa);

    /** Lista todos os veículos cadastrados. */
    List<Veiculo> listarTodos();

    /** Remove um veículo. */
    void remover(String id);
}
