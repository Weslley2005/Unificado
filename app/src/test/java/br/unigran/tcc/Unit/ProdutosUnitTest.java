package br.unigran.tcc.Unit;

import org.junit.Test;
import static org.junit.Assert.*;

import br.unigran.tcc.Model.Produtos;

public class ProdutosUnitTest {
    @Test
    public void testCamposObrigatorios() {
        Produtos produto = new Produtos();

        produto.setId(1);
        produto.setNome("Produto A");
        produto.setQtdProduto(5);
        produto.setProcoCompra(10.50f);
        produto.setPrecoVenda(15.75f);
        produto.setTipo(TipoProdutos.Alimento);

        assertEquals(Integer.valueOf(1), produto.getId());
        assertEquals("Produto A", produto.getNome());
        assertEquals(Integer.valueOf(5), produto.getQtdProduto());
        assertEquals(Float.valueOf(10.50f), produto.getProcoCompra());
        assertEquals(Float.valueOf(15.75f), produto.getPrecoVenda());
        assertEquals(TipoProdutos.Alimento, produto.getTipo());

        assertTrue("Campos Validos",validarCamposObrigatorios(produto));

        produto.setId(null);
        produto.setNome(null);
        produto.setQtdProduto(null);
        produto.setProcoCompra(null);
        produto.setPrecoVenda(null);
        produto.setTipo(null);

        assertEquals(null, produto.getId());
        assertEquals(null, produto.getNome());
        assertEquals(null, produto.getQtdProduto());
        assertEquals(null, produto.getProcoCompra());
        assertEquals(null, produto.getPrecoVenda());
        assertEquals(null, produto.getTipo());

        assertFalse("Campos Invalidos",validarCamposObrigatorios(produto));
    }

    private boolean validarCamposObrigatorios(Produtos produto) {
        return produto.getId() != null && produto.getNome() != null &&
                produto.getQtdProduto() != null && produto.getProcoCompra() != null &&
                produto.getPrecoVenda() != null && produto.getTipo() != null &&
                !produto.getNome().isEmpty() && !produto.getTipo().toString().isEmpty();
    }

    @Test
    public void testValidacaoQuantidadeProduto() {
        Produtos produto = new Produtos();

        produto.setQtdProduto(10);
        assertEquals(Integer.valueOf(10), produto.getQtdProduto());
        assertTrue("Quantidade Valida",validarQuantidadeProduto(produto));

        produto.setQtdProduto(0);
        assertEquals(Integer.valueOf(0), produto.getQtdProduto());
        assertFalse("Quantidade Invalida",validarQuantidadeProduto(produto));

        produto.setQtdProduto(-5);
        assertEquals(Integer.valueOf(-5), produto.getQtdProduto());
        assertFalse("Quantidade Invalida",validarQuantidadeProduto(produto));
    }

    private boolean validarQuantidadeProduto(Produtos produto) {
        return produto.getQtdProduto() != null && produto.getQtdProduto() > 0;
    }

    @Test
    public void testValidacaoPrecosCompraVenda() {
        Produtos produto = new Produtos();

        produto.setProcoCompra(50.0f);
        produto.setPrecoVenda(100.0f);
        assertEquals(Float.valueOf(50.0f), produto.getProcoCompra());
        assertEquals(Float.valueOf(100.0f), produto.getPrecoVenda());
        assertTrue("Proço Valido",validarPrecosCompraVenda(produto));

        produto.setProcoCompra(0.0f);
        produto.setPrecoVenda(0.0f);
        assertEquals(Float.valueOf(0.0f), produto.getProcoCompra());
        assertEquals(Float.valueOf(0.0f), produto.getPrecoVenda());
        assertFalse("Proço Invalido",validarPrecosCompraVenda(produto));

        produto.setProcoCompra(-50.0f);
        produto.setPrecoVenda(-100.0f);
        assertEquals(Float.valueOf(-50.0f), produto.getProcoCompra());
        assertEquals(Float.valueOf(-100.0f), produto.getPrecoVenda());
        assertFalse("Preço Invalido",validarPrecosCompraVenda(produto));
    }

    private boolean validarPrecosCompraVenda(Produtos produto) {
        return produto.getProcoCompra() != null && produto.getPrecoVenda() != null &&
                produto.getProcoCompra() > 0 && produto.getPrecoVenda() > 0;
    }
}
