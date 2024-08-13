package br.unigran.tcc.Inte;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.unigran.tcc.Model.Produtos;

public class ProdutosTest {
    @Test
    public void testCadastroCompletoProduto() {
        Produtos produto = new Produtos();

        produto.setId(1);
        produto.setNome("Produto A");
        produto.setQtdProduto(10);
        produto.setProcoCompra(50.0f);
        produto.setPrecoVenda(100.0f);
        produto.setTipo(TipoProdutos.Alimento);

        assertEquals(Integer.valueOf(1), produto.getId());
        assertEquals("Produto A", produto.getNome());
        assertEquals(Integer.valueOf(10), produto.getQtdProduto());
        assertEquals(Float.valueOf(50.0f), produto.getProcoCompra());
        assertEquals(Float.valueOf(100.0f), produto.getPrecoVenda());
        assertEquals(TipoProdutos.Alimento, produto.getTipo());

        assertTrue("Cadastro Valido",validarCadastroCompletoProduto(produto));
    }

    private boolean validarCadastroCompletoProduto(Produtos produto) {
        return produto.getId() != null && produto.getNome() != null &&
                produto.getQtdProduto() != null && produto.getProcoCompra() != null &&
                produto.getPrecoVenda() != null && produto.getTipo() != null &&
                !produto.getNome().isEmpty() && !produto.getTipo().toString().isEmpty() &&
                produto.getQtdProduto() > 0 && produto.getProcoCompra() > 0 &&
                produto.getPrecoVenda() > 0;
    }
}
