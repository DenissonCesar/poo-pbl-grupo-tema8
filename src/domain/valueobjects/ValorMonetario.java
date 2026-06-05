package domain.valueobjects;

public class ValorMonetario {
    private final long centavos;

    public ValorMonetario(long centavos) {
        this.centavos = centavos;
    }

    public long getCentavos() {
        return centavos;
    }

    public ValorMonetario somar(ValorMonetario outro) {
        return this;
    }

    public ValorMonetario subtrair(ValorMonetario outro) {
        return this;
    }

    public ValorMonetario multiplicar(int quantidade) {
        return this;
    }
}
