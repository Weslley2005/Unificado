package br.unigran.tcc.Unit;

import org.junit.Test;
import static org.junit.Assert.*;

import br.unigran.tcc.Model.Venda;
import java.util.Date;

public class VendaUnitTest {
    @Test
    public void testCamposObrigatorios() {
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

        assertTrue("Campos Valido",validarCamposObrigatorios(venda));

        venda.setId(null);
        venda.setData(null);
        venda.setTotal(null);
        venda.setDesconto(null);
        venda.setProcoTotal(null);

        assertEquals(null, venda.getId());
        assertEquals(null, venda.getData());
        assertEquals(null, venda.getTotal());
        assertEquals(null, venda.getDesconto());
        assertEquals(null, venda.getProcoTotal());

        assertFalse("Campos Invalido",validarCamposObrigatorios(venda));
    }

    private boolean validarCamposObrigatorios(Venda venda) {
        return venda.getId() != null && venda.getData() != null &&
                venda.getTotal() != null && venda.getProcoTotal() != null;
    }

    @Test
    public void testValidacaoPrecosCompraVenda() {
        Venda venda = new Venda();

        venda.setTotal(200.0f);
        venda.setProcoTotal(180.0f);
        assertTrue("Preço Compra Valido",validarPrecosCompraVenda(venda));

        venda.setTotal(0.0f);
        venda.setProcoTotal(180.0f);
        assertFalse("Preço Compra Invalido",validarPrecosCompraVenda(venda));

        venda.setTotal(200.0f);
        venda.setProcoTotal(0.0f);
        assertFalse("Preço Compra Valido",validarPrecosCompraVenda(venda));

        venda.setTotal(-200.0f);
        venda.setProcoTotal(180.0f);
        assertFalse("Preço Compra Valido",validarPrecosCompraVenda(venda));

        venda.setTotal(200.0f);
        venda.setProcoTotal(-180.0f);
        assertFalse("Preço Compra Valido",validarPrecosCompraVenda(venda));
    }

    private boolean validarPrecosCompraVenda(Venda venda) {
        return venda.getTotal() != null && venda.getProcoTotal() != null &&
                venda.getTotal() > 0 && venda.getProcoTotal() > 0;
    }

}
