package domain.entities;

import domain.valueobjects.Placa;
import domain.valueobjects.Quilometragem;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade Veiculo.
 * Possui identificação única (ID), placa, modelo, marca, ano de fabricação e quilometragem mutável.
 * Encapsulamento rigoroso: todos os campos privados e sem setters desnecessários.
 */
public class Veiculo {
    private final UUID id;
    private final Placa placa;
    private final String modelo;
    private final String marca;
    private final int anoFabricacao;
    private Quilometragem quilometragem;

    public Veiculo(UUID id, Placa placa, String modelo, String marca, int anoFabricacao, Quilometragem quilometragem) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do veículo não pode ser nulo.");
        }
        if (placa == null) {
            throw new IllegalArgumentException("A placa do veículo não pode ser nula.");
        }
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException("O modelo do veículo não pode ser vazio.");
        }
        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("A marca do veículo não pode ser vazia.");
        }
        if (anoFabricacao <= 0) {
            throw new IllegalArgumentException("O ano de fabricação deve ser maior que zero.");
        }
        if (quilometragem == null) {
            throw new IllegalArgumentException("A quilometragem não pode ser nula.");
        }
        this.id = id;
        this.placa = placa;
        this.modelo = modelo;
        this.marca = marca;
        this.anoFabricacao = anoFabricacao;
        this.quilometragem = quilometragem;
    }

    public UUID getId() {
        return id;
    }

    public Placa getPlaca() {
        return placa;
    }

    public String getModelo() {
        return modelo;
    }

    public String getMarca() {
        return marca;
    }

    public int getAnoFabricacao() {
        return anoFabricacao;
    }

    public Quilometragem getQuilometragem() {
        return quilometragem;
    }

    /**
     * Atualiza a quilometragem do veículo.
     * Só é aceito um valor que seja maior ou igual ao valor atual de quilometragem.
     */
    public void atualizarQuilometragem(Quilometragem nova) {
        if (nova == null) {
            throw new IllegalArgumentException("A nova quilometragem não pode ser nula.");
        }
        if (nova.getValor() < this.quilometragem.getValor()) {
            throw new IllegalArgumentException("A nova quilometragem não pode ser menor que a quilometragem atual.");
        }
        this.quilometragem = nova;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veiculo veiculo = (Veiculo) o;
        return Objects.equals(id, veiculo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s - %s %s (%d) - KM: %s", placa, marca, modelo, anoFabricacao, quilometragem);
    }
}
