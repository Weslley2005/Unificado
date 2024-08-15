package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.unigran.tcc.R;

public class Login extends AppCompatActivity {
    public EditText email;
    public EditText senha;
    public Button login;
    Button cadastrese;
    public Button esqueceuSenha;
    SignInButton btgo;
    public FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.idEmail);
        senha = findViewById(R.id.idSenha);
        login = findViewById(R.id.idLogin);
        cadastrese = findViewById(R.id.idCadastrese);
        esqueceuSenha = findViewById(R.id.idEsqueceuSenha);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        // Deixar a barra inferior (navigation bar) preta
        window.setNavigationBarColor(getResources().getColor(android.R.color.black));

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            telaprincipal();
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty() || senha.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Obrigatório preencher todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), senha.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Login efetuado com Sucesso!", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        telaprincipal();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "ERRO ao efetuar o Login!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        cadastrese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telacadastro();
            }
        });

        esqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = email.getText().toString();
                if (emailAddress.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Por favor, insira seu email para recuperar a senha.", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Email de recuperação de senha enviado!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Erro ao enviar email de recuperação de senha.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void telacadastro() {
        Intent intent = new Intent(this, CadastroUsuario.class);
        startActivity(intent);
    }

    public void telaprincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void showToast(String s) {
    }
}
