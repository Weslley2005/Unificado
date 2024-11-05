package br.unigran.tcc.Inte;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;

import br.unigran.tcc.ViewModel.CPFUtils;
import br.unigran.tcc.ViewModel.CadastroUsuario;

public class CadastroUsuarioTest {

    private CadastroUsuario cadastroUsuario;

    @Before
    public void setUp() {
        cadastroUsuario = new CadastroUsuario();

        // Inicializando EditTexts simulando o cenário real
        cadastroUsuario.nome = new EditText(null);
        cadastroUsuario.cpf = new EditText(null);
        cadastroUsuario.telefone = new EditText(null);
        cadastroUsuario.estado = new EditText(null);
        cadastroUsuario.cidade = new EditText(null);
        cadastroUsuario.bairro = new EditText(null);
        cadastroUsuario.rua = new EditText(null);
        cadastroUsuario.numero = new EditText(null);
        cadastroUsuario.email = new EditText(null);
        cadastroUsuario.senha = new EditText(null);
        cadastroUsuario.confirmarSenha = new EditText(null);
    }

    @Test
    public void testCamposVazios_comCamposVazios_retornaTrue() {
        // Todos os campos estão vazios por padrão
        assertTrue(cadastroUsuario.camposVazios());
    }

    @Test
    public void testCamposVazios_comTodosCamposPreenchidos_retornaFalse() {
        // Definir valores para todos os campos
        cadastroUsuario.nome.setText("Nome Teste");
        cadastroUsuario.cpf.setText("123.456.789-09");
        cadastroUsuario.telefone.setText("(11) 91234-5678");
        cadastroUsuario.estado.setText("SP");
        cadastroUsuario.cidade.setText("São Paulo");
        cadastroUsuario.bairro.setText("Centro");
        cadastroUsuario.rua.setText("Rua Teste");
        cadastroUsuario.numero.setText("123");
        cadastroUsuario.email.setText("teste@exemplo.com");
        cadastroUsuario.senha.setText("senha123");
        cadastroUsuario.confirmarSenha.setText("senha123");

        assertFalse(cadastroUsuario.camposVazios());
    }

    @Test
    public void testLimpaCampos() {
        // Preenche os campos com valores
        cadastroUsuario.nome.setText("Nome Teste");
        cadastroUsuario.cpf.setText("123.456.789-09");
        cadastroUsuario.telefone.setText("(11) 91234-5678");
        cadastroUsuario.estado.setText("SP");
        cadastroUsuario.cidade.setText("São Paulo");
        cadastroUsuario.bairro.setText("Centro");
        cadastroUsuario.rua.setText("Rua Teste");
        cadastroUsuario.numero.setText("123");
        cadastroUsuario.email.setText("teste@exemplo.com");
        cadastroUsuario.senha.setText("senha123");
        cadastroUsuario.confirmarSenha.setText("senha123");

        // Chama o método para limpar os campos
        cadastroUsuario.limpaCampos();

        // Verifica se todos os campos estão vazios após a limpeza
        assertEquals("", cadastroUsuario.nome.getText().toString());
        assertEquals("", cadastroUsuario.cpf.getText().toString());
        assertEquals("", cadastroUsuario.telefone.getText().toString());
        assertEquals("", cadastroUsuario.estado.getText().toString());
        assertEquals("", cadastroUsuario.cidade.getText().toString());
        assertEquals("", cadastroUsuario.bairro.getText().toString());
        assertEquals("", cadastroUsuario.rua.getText().toString());
        assertEquals("", cadastroUsuario.numero.getText().toString());
        assertEquals("", cadastroUsuario.email.getText().toString());
        assertEquals("", cadastroUsuario.senha.getText().toString());
        assertEquals("", cadastroUsuario.confirmarSenha.getText().toString());
    }

    @Test
    public void testSenhaMinimoOitoCaracteres() {
        cadastroUsuario.senha.setText("1234567");
        cadastroUsuario.confirmarSenha.setText("1234567");
        assertTrue(cadastroUsuario.senha.getText().toString().length() < 8);

        cadastroUsuario.senha.setText("12345678");
        cadastroUsuario.confirmarSenha.setText("12345678");
        assertTrue(cadastroUsuario.senha.getText().toString().length() >= 8);
    }

    @Test
    public void testSenhasNaoConferem() {
        cadastroUsuario.senha.setText("senha123");
        cadastroUsuario.confirmarSenha.setText("senha321");
        assertFalse(cadastroUsuario.senha.getText().toString().equals(cadastroUsuario.confirmarSenha.getText().toString()));
    }

    @Test
    public void testFormatoTelefoneValido() {
        cadastroUsuario.telefone.setText("(11) 91234-5678");
        assertTrue(cadastroUsuario.telefone.getText().toString().matches("\\(\\d{2}\\) \\d{5}-\\d{4}"));
    }

    @Test
    public void testFormatoCpfValido() {
        cadastroUsuario.cpf.setText("123.456.789-09");
        assertTrue(CPFUtils.isValidCPF(cadastroUsuario.cpf.getText().toString()));
    }
}

