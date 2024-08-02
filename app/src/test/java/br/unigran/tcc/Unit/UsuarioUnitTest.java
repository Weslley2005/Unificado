package br.unigran.tcc.Unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import br.unigran.tcc.Model.Usuario;

public class UsuarioUnitTest {

    @Test
    public void testValidarFormatoCPF() {
        Usuario usuario = new Usuario();
        usuario.setCpf("123.456.789-01");
        assertTrue("CPF Valido",validarFormatoCPF(usuario.getCpf()));
    }

    @Test
    public void testValidarFormatoCPF_Invalido() {
        Usuario usuario = new Usuario();
        usuario.setCpf("12345678901");
        assertFalse("CPF Invalido",validarFormatoCPF(usuario.getCpf()));
    }

    @Test
    public void testValidarFormatoEmail() {
        Usuario usuario = new Usuario();
        usuario.setEmail("joao@example.com");
        assertTrue("Email Valido",validarFormatoEmail(usuario.getEmail()));
    }

    @Test
    public void testValidarFormatoEmail_Invalido() {
        Usuario usuario = new Usuario();
        usuario.setEmail("joaoexample.com");
        assertFalse("Email Invalido",validarFormatoEmail(usuario.getEmail()));
    }

    @Test
    public void testValidarEmailUnico() {
        Usuario usuario1 = new Usuario();
        usuario1.setEmail("joao@example.com");

        Usuario usuario2 = new Usuario();
        usuario2.setEmail("maria@example.com");

        List<Usuario> usuariosCadastrados = new ArrayList<>();
        usuariosCadastrados.add(usuario1);

        assertTrue("Email Unico",validarEmailUnico(usuario2.getEmail(), usuariosCadastrados));
    }

    @Test
    public void testValidarCPFFormatoUnico() {
        Usuario usuario1 = new Usuario();
        usuario1.setCpf("123.456.789-01");

        Usuario usuario2 = new Usuario();
        usuario2.setCpf("987.654.321-09");

        List<Usuario> usuariosCadastrados = new ArrayList<>();
        usuariosCadastrados.add(usuario1);

        assertTrue("CPF Unico",validarCPFFormatoUnico(usuario2.getCpf(), usuariosCadastrados));
    }
    private boolean validarFormatoCPF(String cpf) {

        return cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }

    private boolean validarFormatoEmail(String email) {
        return email.contains("@");
    }

    private boolean validarEmailUnico(String email, List<Usuario> usuarios) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }

    private boolean validarCPFFormatoUnico(String cpf, List<Usuario> usuarios) {
        for (Usuario u : usuarios) {
            if (u.getCpf().equals(cpf)) {
                return false;
            }
        }
        return true;
    }
}