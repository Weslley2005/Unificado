package br.unigran.tcc.Unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import br.unigran.tcc.Model.ProdutoPP;

public class ProdutoPPUnitTest {

    @Test
    public void testCamposObrigatorios() {
        ProdutoPP produto = new ProdutoPP();

        produto.setId(1);
        produto.setNome("Produto A");
        produto.setPrecoVP(10.0f);
        produto.setTipoProdutoPP(TipoProdutoPP.Alimento_P_Próprio);

        assertEquals(Integer.valueOf(1), produto.getId());
        assertEquals("Produto A", produto.getNome());
        assertEquals(Float.valueOf(10.0f), produto.getPrecoVP());
        assertEquals(TipoProdutoPP.Alimento_P_Próprio, produto.getTipoProdutoPP());

        assertTrue("Caompos Validos",validarCamposObrigatorios(produto));

        produto.setId(null);
        produto.setNome(null);
        produto.setPrecoVP(null);
        produto.setTipoProdutoPP(null);

        assertEquals(null, produto.getId());
        assertEquals(null, produto.getNome());
        assertEquals(null, produto.getPrecoVP());
        assertEquals(null, produto.getTipoProdutoPP());

        assertFalse("Campos Inavalidos",validarCamposObrigatorios(produto));
    }

    private boolean validarCamposObrigatorios(ProdutoPP produto) {
        return produto.getId() != null && produto.getNome() != null &&
                produto.getPrecoVP() != null && produto.getTipoProdutoPP() != null &&
                !produto.getNome().isEmpty();
    }

    @Test
    public void testValidacaoPrecoVP() {
        ProdutoPP produto = new ProdutoPP();

        produto.setPrecoVP(50.0f);
        assertEquals(Float.valueOf(50.0f), produto.getPrecoVP());
        assertTrue("Preço Valido",validarPrecoVP(produto));

        produto.setPrecoVP(0.0f);
        assertEquals(Float.valueOf(0.0f), produto.getPrecoVP());
        assertFalse("Proço Invalido",validarPrecoVP(produto));

        produto.setPrecoVP(-50.0f);
        assertEquals(Float.valueOf(-50.0f), produto.getPrecoVP());
        assertFalse("Proço Invalido",validarPrecoVP(produto));
    }

    private boolean validarPrecoVP(ProdutoPP produto) {
        return produto.getPrecoVP() != null && produto.getPrecoVP() > 0;
    }
}

