package domain.entities;

import domain.valueobjects.Placa;
import domain.valueobjects.Quilometragem;
import java.util.UUID;

public class Veiculo {
    private final UUID id;
    private final Placa placa;
    private final String modelo;
    private final String marca;
    private final int anoFabricacao;
    private Quilometragem quilometragem;

    public Veiculo(UUID id, Placa placa, String modelo, String marca, int anoFabricacao, Quilometragem quilometragem) {
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

    public void atualizarQuilometragem(Quilometragem nova) {
    }
}
