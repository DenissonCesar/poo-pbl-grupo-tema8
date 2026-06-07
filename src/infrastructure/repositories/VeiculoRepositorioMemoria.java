package infrastructure.repositories;

import domain.entities.Veiculo;
import domain.repositories.IVeiculoRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação concreta em memória do IVeiculoRepository.
 */
public class VeiculoRepositorioMemoria implements IVeiculoRepository {

    private final Map<String, Veiculo> armazenamento = new HashMap<>();

    // ── Escrita ───────────────────────────────────────────────────────────────

    @Override
    public void salvar(Veiculo veiculo) {
        if (veiculo == null)
            throw new IllegalArgumentException("Veículo não pode ser nulo.");

        // Verifica duplicidade de placa
        Optional<Veiculo> existentePorPlaca = buscarPorPlaca(veiculo.getPlaca());
        if (existentePorPlaca.isPresent() &&
                !existentePorPlaca.get().getId().equals(veiculo.getId())) {
            throw new IllegalStateException(
                    "Já existe um veículo com a placa \"" + veiculo.getPlaca() +
                            "\" (ID: " + existentePorPlaca.get().getId() + ").");
        }

        armazenamento.put(veiculo.getId(), veiculo);
    }

    @Override
    public void remover(String id) {
        if (!armazenamento.containsKey(id))
            throw new NoSuchElementException("Veículo com ID \"" + id + "\" não encontrado.");
        armazenamento.remove(id);
    }

    // ── Leitura ───────────────────────────────────────────────────────────────

    @Override
    public Optional<Veiculo> buscarPorId(String id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public Optional<Veiculo> buscarPorPlaca(String placa) {
        String placaNorm = placa.toUpperCase().replaceAll("[^A-Z0-9]", "");
        return armazenamento.values().stream()
                .filter(v -> v.getPlaca().equals(placaNorm))
                .findFirst();
    }

    @Override
    public List<Veiculo> listarTodos() {
        return new ArrayList<>(armazenamento.values());
    }

    // ── Helpers para testes ───────────────────────────────────────────────────

    public int tamanho() {
        return armazenamento.size();
    }

    public void limpar() {
        armazenamento.clear();
    }
}
