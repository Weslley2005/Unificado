package br.unigran.tcc.Inte;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Date;

import br.unigran.tcc.Model.Venda;

public class VendaTest {
    @Test
    public void testCalculoProcoTotal() {
        Venda venda = new Venda();

        venda.setTotal(200.0f);
        venda.setDesconto(20.0f);
        assertEquals(Float.valueOf(180.0f), venda.getProcoTotal());
        assertTrue("Preço Total Valido",180.0f == venda.getProcoTotal());

        venda.setTotal(200.0f);
        venda.setDesconto(0.0f);
        assertEquals(Float.valueOf(200.0f), venda.getProcoTotal());
        assertTrue(200.0f == venda.getProcoTotal());

        venda.setTotal(200.0f);
        venda.setDesconto(200.0f);
        assertEquals(Float.valueOf(0.0f), venda.getProcoTotal());
        assertTrue("Preço Total Valido",0.0f == venda.getProcoTotal());

        venda.setTotal(0.0f);
        venda.setDesconto(20.0f);
        assertEquals(Float.valueOf(-20.0f), venda.getProcoTotal());
        assertFalse("Preço Total Invalido",-20.0f == venda.getProcoTotal());

        venda.setTotal(null);
        venda.setDesconto(20.0f);
        assertNull(venda.getProcoTotal());
        assertFalse("Preço Total Invalido",venda.getProcoTotal() != null);

        venda.setTotal(200.0f);
        venda.setDesconto(null);
        assertNull(venda.getProcoTotal());
        assertFalse("Preço Total Invalido",venda.getProcoTotal() != null);
    }

    @Test
    public void testCadastroCompletoVenda() {
        Venda venda = new Venda();

        venda.setId(1);
        venda.setData(new Date());
        venda.setTotal(200.0f);
        venda.setDesconto(20.0f);
        venda.setProcoTotal(180.0f);

        assertEquals(Integer.valueOf(1), venda.getId());
        assertNotNull(venda.getData());
        assertEquals(Float.valueOf(200.0f), venda.getTotal());
        assertEquals(Float.valueOf(20.0f), venda.getDesconto());
        assertEquals(Float.valueOf(180.0f), venda.getProcoTotal());

        assertTrue("Cadastro Valido",validarCadastroCompletoVenda(venda));
    }

    private boolean validarCadastroCompletoVenda(Venda venda) {
        return venda.getId() != null &&
                venda.getData() != null &&
                venda.getTotal() != null &&
                venda.getDesconto() != null &&
                venda.getProcoTotal() != null &&
                venda.getId() > 0 &&
                venda.getTotal() > 0 &&
                venda.getDesconto() >= 0 &&
                venda.getProcoTotal() >= 0;
    }
}
