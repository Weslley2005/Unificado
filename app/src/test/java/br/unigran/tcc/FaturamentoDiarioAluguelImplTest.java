package br.unigran.tcc;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.unigran.tcc.ViewModel.FaturamentoDiarioAlugueisImpl;

public class FaturamentoDiarioAluguelImplTest {
    private FaturamentoDiarioAlugueisImpl.ProdutoFaturamento produto;

    @Before
    public void setup() {
        produto = new FaturamentoDiarioAlugueisImpl.ProdutoFaturamento("ProdutoTeste");
    }

    @Test
    public void testAdicionarQuantidade() {
        produto.adicionarQuantidade(5);
        produto.adicionarQuantidade(3);
        assertEquals(8, produto.getQuantidade());
    }

    @Test
    public void testAdicionarPrecoCompra() {
        produto.adicionarPrecoCompra(10.0);
        produto.adicionarPrecoCompra(15.0);
        assertEquals(25.0, produto.getTotalPrecoCompra(), 0.01);
    }

    @Test
    public void testAdicionarPrecoVenda() {
        produto.adicionarPrecoVenda(20.0);
        produto.adicionarPrecoVenda(30.0);
        assertEquals(50.0, produto.getTotalPrecoVenda(), 0.01);
    }

    @Test
    public void testGetPrecoLiquido() {
        produto.adicionarPrecoCompra(10.0);
        produto.adicionarPrecoVenda(25.0);
        assertEquals(15.0, produto.getPrecoLiquido(), 0.01);
    }
}
