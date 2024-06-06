package br.unigran.tcc.Inte;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.unigran.tcc.Model.ProdutoPP;
import br.unigran.tcc.Model.TipoProdutoPP;

public class ProdutoPPTest {
    @Test
    public void testCadastroCompletoProduto() {
        ProdutoPP produto = new ProdutoPP();

        produto.setId(1);
        produto.setNome("Produto A");
        produto.setPrecoVP(50.0f);
        produto.setTipoProdutoPP(TipoProdutoPP.Alimento_P_Próprio);

        assertEquals(Integer.valueOf(1), produto.getId());
        assertEquals("Produto A", produto.getNome());
        assertEquals(Float.valueOf(50.0f), produto.getPrecoVP());
        assertEquals(TipoProdutoPP.Alimento_P_Próprio, produto.getTipoProdutoPP());

        assertTrue("Cadastro Valido",validarCadastroCompletoProduto(produto));
    }

    private boolean validarCadastroCompletoProduto(ProdutoPP produto) {
        return produto.getId() != null && produto.getNome() != null &&
                produto.getPrecoVP() != null && produto.getTipoProdutoPP() != null &&
                !produto.getNome().isEmpty() && produto.getPrecoVP() > 0;
    }
}
