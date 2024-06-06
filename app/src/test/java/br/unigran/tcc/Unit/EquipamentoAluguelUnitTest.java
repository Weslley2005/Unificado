package br.unigran.tcc.Unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import br.unigran.tcc.Model.EquipamentoAluguel;
import br.unigran.tcc.Model.TipoAluguel; // Certifique-se de importar a classe TipoAluguel

public class EquipamentoAluguelUnitTest {
    @Test
    public void testCamposObrigatorios() {
        EquipamentoAluguel equipamento = new EquipamentoAluguel();

        equipamento.setId(1);
        equipamento.setNome("Equipamento A");
        equipamento.setQtdAluguel(5);
        equipamento.setPrecoAluguel(100.0f);
        equipamento.setTipoAluguem(TipoAluguel.Meia);

        assertEquals(Integer.valueOf(1), equipamento.getId());
        assertEquals("Equipamento A", equipamento.getNome());
        assertEquals(Integer.valueOf(5), equipamento.getQtdAluguel());
        assertEquals(Float.valueOf(100.0f), equipamento.getPrecoAluguel());
        assertEquals(TipoAluguel.Meia, equipamento.getTipoAluguel());

        assertTrue("Caompos preenchidos com sucesso",validarCamposObrigatorios(equipamento));

        equipamento.setId(null);
        equipamento.setNome(null);
        equipamento.setQtdAluguel(null);
        equipamento.setPrecoAluguel(null);
        equipamento.setTipoAluguem(null);

        assertEquals(null, equipamento.getId());
        assertEquals(null, equipamento.getNome());
        assertEquals(null, equipamento.getQtdAluguel());
        assertEquals(null, equipamento.getPrecoAluguel());
        assertEquals(null, equipamento.getTipoAluguel());

        assertFalse("Algum campo esta vazio",validarCamposObrigatorios(equipamento));
    }

    private boolean validarCamposObrigatorios(EquipamentoAluguel equipamento) {
        return equipamento.getId() != null && equipamento.getNome() != null &&
                equipamento.getQtdAluguel() != null && equipamento.getPrecoAluguel() != null &&
                equipamento.getTipoAluguel() != null && !equipamento.getNome().isEmpty();
    }

    @Test
    public void testValidacaoQuantidadeAluguel() {
        EquipamentoAluguel equipamento = new EquipamentoAluguel();

        equipamento.setQtdAluguel(10);
        assertEquals(Integer.valueOf(10), equipamento.getQtdAluguel());
        assertTrue("Quantidade Valida",validarQuantidadeAluguel(equipamento));

        equipamento.setQtdAluguel(0);
        assertEquals(Integer.valueOf(0), equipamento.getQtdAluguel());
        assertFalse("Quantidade Invalida",validarQuantidadeAluguel(equipamento));

        equipamento.setQtdAluguel(-5);
        assertEquals(Integer.valueOf(-5), equipamento.getQtdAluguel());
        assertFalse("Quantidade Invalida",validarQuantidadeAluguel(equipamento));
    }

    private boolean validarQuantidadeAluguel(EquipamentoAluguel equipamento) {
        return equipamento.getQtdAluguel() != null && equipamento.getQtdAluguel() > 0;
    }

    @Test
    public void testValidacaoPrecosAluguel() {
        EquipamentoAluguel equipamento = new EquipamentoAluguel();

        equipamento.setPrecoAluguel(50.0f);
        assertEquals(Float.valueOf(50.0f), equipamento.getPrecoAluguel());
        assertTrue("Preço Valido",validarPrecosAluguel(equipamento));

        equipamento.setPrecoAluguel(0.0f);
        assertEquals(Float.valueOf(0.0f), equipamento.getPrecoAluguel());
        assertFalse("Proço Invalido",validarPrecosAluguel(equipamento));

        equipamento.setPrecoAluguel(-50.0f);
        assertEquals(Float.valueOf(-50.0f), equipamento.getPrecoAluguel());
        assertFalse("Preço Invalido",validarPrecosAluguel(equipamento));
    }

    private boolean validarPrecosAluguel(EquipamentoAluguel equipamento) {
        return equipamento.getPrecoAluguel() != null && equipamento.getPrecoAluguel() > 0;
    }

}
