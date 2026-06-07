package infrastructure.repositories;

import domain.entities.OrdemDeServico;
import domain.entities.StatusOrdem;
import domain.repositories.IOrdemDeServicoRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação concreta em memória do IOrdemDeServicoRepository.
 *
 * Usa um HashMap como mecanismo de persistência em tempo de execução.
 * Ideal para testes de unidade e desenvolvimento sem banco de dados.
 *
 * Para usar com banco real (ex.: JPA/Hibernate), basta criar outra classe
 * que implemente IOrdemDeServicoRepository e trocar no ponto de injeção —
 * os casos de uso em application/ não precisam ser alterados.
 */
public class OrdemDeServicoRepositorioMemoria implements IOrdemDeServicoRepository {

    private final Map<String, OrdemDeServico> armazenamento = new HashMap<>();

    // ── Escrita ───────────────────────────────────────────────────────────────

    @Override
    public void salvar(OrdemDeServico ordem) {
        if (ordem == null)
            throw new IllegalArgumentException("Ordem de Serviço não pode ser nula.");
        if (armazenamento.containsKey(ordem.getId()))
            throw new IllegalStateException(
                    "Ordem de Serviço com ID \"" + ordem.getId() + "\" já existe. Use atualizar().");
        armazenamento.put(ordem.getId(), ordem);
    }

    @Override
    public void atualizar(OrdemDeServico ordem) {
        if (ordem == null)
            throw new IllegalArgumentException("Ordem de Serviço não pode ser nula.");
        if (!armazenamento.containsKey(ordem.getId()))
            throw new NoSuchElementException(
                    "Ordem de Serviço com ID \"" + ordem.getId() + "\" não encontrada para atualização.");
        armazenamento.put(ordem.getId(), ordem);
    }

    @Override
    public void remover(String id) {
        if (!armazenamento.containsKey(id))
            throw new NoSuchElementException(
                    "Ordem de Serviço com ID \"" + id + "\" não encontrada.");
        armazenamento.remove(id);
    }

    // ── Leitura ───────────────────────────────────────────────────────────────

    @Override
    public Optional<OrdemDeServico> buscarPorId(String id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public List<OrdemDeServico> buscarPorVeiculo(String veiculoId) {
        return armazenamento.values().stream()
                .filter(o -> o.getVeiculoId().equals(veiculoId))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdemDeServico> buscarPorStatus(StatusOrdem status) {
        return armazenamento.values().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdemDeServico> listarTodas() {
        return new ArrayList<>(armazenamento.values());
    }

    // ── Helpers para testes ───────────────────────────────────────────────────

    /** Quantidade de ordens armazenadas. */
    public int tamanho() {
        return armazenamento.size();
    }

    /** Limpa o repositório — use em @BeforeEach nos testes. */
    public void limpar() {
        armazenamento.clear();
    }
}
