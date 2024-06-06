package br.unigran.tcc.Inte;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Objects;

import br.unigran.tcc.Model.Usuario;

public class UsuarioTest {
    @Test
    public void testCadastroCompletoUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setCpf("123.456.789-00");
        usuario.setTelefone("(00) 12345-6789");
        usuario.setCidade("São Paulo");
        usuario.setBairro("Centro");
        usuario.setRua("Rua A");
        usuario.setNumero(100);
        usuario.setEmail("joao.silva@example.com");
        usuario.setSenha("senha123");
        usuario.setConfSenha("senha123");

        boolean cadastroValido = validarCadastro(usuario);
        assertTrue("Cadastro Valido",cadastroValido);
    }

    @Test
    public void testLoginValido() {
        Usuario usuario = new Usuario();
        usuario.setEmail("joao.silva@example.com");
        usuario.setSenha("senha123");

        boolean loginValido = autenticar(usuario.getEmail(), usuario.getSenha());
        assertTrue("Login Valido",loginValido);
    }

    @Test
    public void testLoginInvalido() {
        Usuario usuario = new Usuario();
        usuario.setEmail("joao.silva@example.com");
        usuario.setSenha("senha123");

        boolean loginValido = autenticar(usuario.getEmail(), "senha_incorreta");
        assertFalse("Login Invalido",loginValido);
    }
    private boolean validarCadastro(Usuario usuario) {
        boolean dadosValidos = true;

        if (usuario.getNome() == null || usuario.getCpf() == null || usuario.getTelefone() == null ||
                usuario.getCidade() == null || usuario.getBairro() == null || usuario.getRua() == null ||
                usuario.getNumero() == null || usuario.getEmail() == null || usuario.getSenha() == null ||
                usuario.getConfSenha() == null) {
            dadosValidos = false;
        }

        if (!Objects.equals(usuario.getSenha(), usuario.getConfSenha())) {
            dadosValidos = false;
        }

        return dadosValidos;
    }
    private boolean autenticar(String email, String senha) {
        Usuario usuario = buscarUsuarioPorEmail(email);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            return true;
        } else {
            return false;
        }
    }
    private Usuario buscarUsuarioPorEmail(String email) {
        if ("joao.silva@example.com".equals(email)) {
            Usuario usuario = new Usuario();
            usuario.setEmail("joao.silva@example.com");
            usuario.setSenha("senha123");
            return usuario;
        } else {
            return null;
        }
    }
}
