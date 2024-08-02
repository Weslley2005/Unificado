package br.unigran.tcc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.unigran.tcc.ViewModel.Login;
import br.unigran.tcc.ViewModel.MainActivity;

//@RunWith(AndroidJUnit4.class)
public class LoginTest {

    private Login loginActivity;
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText senha;

    @Before
    public void setUp() {
        loginActivity = Mockito.spy(new Login());
        mAuth = Mockito.mock(FirebaseAuth.class);
        loginActivity.mAuth = mAuth;

        // Mock Views
        email = Mockito.mock(EditText.class);
        senha = Mockito.mock(EditText.class);
        loginActivity.email = email;
        loginActivity.senha = senha;
    }

    @Test
    public void testEmptyFields() {
        // Set up mocks
        when(email.getText().toString()).thenReturn("");
        when(senha.getText().toString()).thenReturn("");

        // Simulate button click
        loginActivity.login.performClick();

        // Verify Toast message
        verify(loginActivity).showToast("Obrigatório preencher todos os campos!");
    }

    @Test
    public void testSuccessfulLogin() {
        // Set up mocks
        when(email.getText().toString()).thenReturn("user@example.com");
        when(senha.getText().toString()).thenReturn("password");

        Task<AuthResult> task = Mockito.mock(Task.class);
        when(task.isSuccessful()).thenReturn(true);
        when(mAuth.signInWithEmailAndPassword("user@example.com", "password")).thenReturn(task);

        // Simulate button click
        loginActivity.login.performClick();

        // Verify that the main activity is started
        verify(loginActivity).startActivity(new Intent(loginActivity, MainActivity.class));
    }

    @Test
    public void testFailedLogin() {
        // Set up mocks
        when(email.getText().toString()).thenReturn("user@example.com");
        when(senha.getText().toString()).thenReturn("wrongpassword");

        Task<AuthResult> task = Mockito.mock(Task.class);
        when(task.isSuccessful()).thenReturn(false);
        when(mAuth.signInWithEmailAndPassword("user@example.com", "wrongpassword")).thenReturn(task);

        // Simulate button click
        loginActivity.login.performClick();

        // Verify Toast message
        verify(loginActivity).showToast("ERRO ao efetuar o Login!");
    }

    @Test
    public void testPasswordReset() {
        // Set up mocks
        when(email.getText().toString()).thenReturn("user@example.com");

        Task<Void> task = Mockito.mock(Task.class);
        when(task.isSuccessful()).thenReturn(true);
        when(mAuth.sendPasswordResetEmail("user@example.com")).thenReturn(task);

        // Simulate button click
        loginActivity.esqueceuSenha.performClick();

        // Verify Toast message
        verify(loginActivity).showToast("Email de recuperação de senha enviado!");
    }
}