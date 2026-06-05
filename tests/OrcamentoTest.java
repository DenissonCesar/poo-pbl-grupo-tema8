import domain.entities.Orcamento;
import domain.states.PendenteState;
import domain.states.AprovadoState;
import domain.states.RejeitadoState;
import domain.states.ConcluidoState;
import domain.valueobjects.ItemDeOrcamento;
import domain.valueobjects.ValorMonetario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrcamentoTest {

    @Test
    public void deveIniciarOrcamentoComoPendente() {
        Orcamento orcamento = new Orcamento();
        assertTrue(orcamento.getStatus() instanceof PendenteState);
        assertEquals("Pendente", orcamento.getStatus().getDescricao());
    }

    @Test
    public void deveTransitionarDePendenteParaAprovadoComSucesso() {
        Orcamento orcamento = new Orcamento();
        orcamento.aprovar();
        assertTrue(orcamento.getStatus() instanceof AprovadoState);
        assertEquals("Aprovado", orcamento.getStatus().getDescricao());
    }

    @Test
    public void deveTransitionarDePendenteParaRejeitadoComSucesso() {
        Orcamento orcamento = new Orcamento();
        orcamento.rejeitar();
        assertTrue(orcamento.getStatus() instanceof RejeitadoState);
        assertEquals("Rejeitado", orcamento.getStatus().getDescricao());
    }

    @Test
    public void deveTransitionarDeAprovadoParaConcluidoComSucesso() {
        Orcamento orcamento = new Orcamento();
        orcamento.aprovar();
        orcamento.concluir();
        assertTrue(orcamento.getStatus() instanceof ConcluidoState);
        assertEquals("Concluido", orcamento.getStatus().getDescricao());
    }

    @Test
    public void deveLancarExcecaoAoTransitionarDeAprovadoParaRejeitado() {
        Orcamento orcamento = new Orcamento();
        orcamento.aprovar();
        assertThrows(IllegalStateException.class, () -> {
            orcamento.rejeitar();
        });
    }

    @Test
    public void deveLancarExcecaoAoTransitionarDeRejeitadoParaAprovado() {
        Orcamento orcamento = new Orcamento();
        orcamento.rejeitar();
        assertThrows(IllegalStateException.class, () -> {
            orcamento.aprovar();
        });
    }

    @Test
    public void deveLancarExcecaoAoAdicionarItemEmOrcamentoNaoPendente() {
        Orcamento orcamento = new Orcamento();
        orcamento.aprovar();
        
        ValorMonetario valorUnitario = new ValorMonetario(1000);
        ItemDeOrcamento item = new ItemDeOrcamento("Serviço", valorUnitario, 1);

        assertThrows(IllegalStateException.class, () -> {
            orcamento.adicionarItem(item);
        });
    }
}
