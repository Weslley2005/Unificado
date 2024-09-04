package br.unigran.tcc.Inte;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.unigran.tcc.Model.EquipamentoAluguel;

public class EquipamentoAluguelTest {
    @Test
    public void testCadastroCompletoEquipamento() {
        EquipamentoAluguel equipamento = new EquipamentoAluguel();

        equipamento.setId(1);
        equipamento.setNome("Equipamento A");
        equipamento.setQtdAluguel(10);
        equipamento.setPrecoAluguel(50.0f);
        equipamento.setTipoAluguem(TipoAluguel.Meia);

        assertEquals(Integer.valueOf(1), equipamento.getId());
        assertEquals("Equipamento A", equipamento.getNome());
        assertEquals(Integer.valueOf(10), equipamento.getQtdAluguel());
        assertEquals(Float.valueOf(50.0f), equipamento.getPrecoAluguel());
        assertEquals(TipoAluguel.Meia, equipamento.getTipoAluguel());

        assertTrue("Cadastro Valido",validarCadastroCompletoEquipamento(equipamento));
    }

    private boolean validarCadastroCompletoEquipamento(EquipamentoAluguel equipamento) {
        return equipamento.getId() != null && equipamento.getNome() != null &&
                equipamento.getQtdAluguel() != null && equipamento.getPrecoAluguel() != null &&
                equipamento.getTipoAluguel() != null && !equipamento.getNome().isEmpty() &&
                equipamento.getQtdAluguel() > 0 && equipamento.getPrecoAluguel() > 0;
    }
}
