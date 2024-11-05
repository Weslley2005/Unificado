package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import br.unigran.tcc.Model.Usuario;
import br.unigran.tcc.R;

public class CadastroUsuario extends AppCompatActivity {

    public EditText nome;
    public EditText cpf;
    public EditText telefone;
    public EditText estado;
    public EditText cidade;
    public EditText bairro;
    public EditText rua;
    public EditText numero;
    public EditText email;
    public EditText senha;
    private TextView senhaError;
    public EditText confirmarSenha;
    private Button cadastrar;
    private TextView cpfError;
    private TextView confSenhaError;
    private TextView telefoneError;
    private FirebaseAuth mAuth;
    private Usuario u;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        mAuth = FirebaseAuth.getInstance();
        nome = findViewById(R.id.idNome);
        cpf = findViewById(R.id.idCpf);
        telefone = findViewById(R.id.idTelefone);
        estado = findViewById(R.id.idEstado);
        cidade = findViewById(R.id.idCidade);
        bairro = findViewById(R.id.idBairro);
        rua = findViewById(R.id.idRua);
        numero = findViewById(R.id.idNumero);
        email = findViewById(R.id.idCadEmail);
        senha = findViewById(R.id.idcadSenha);
        confirmarSenha = findViewById(R.id.idConfSenha);
        cadastrar = findViewById(R.id.idCadastrar);
        cpfError = findViewById(R.id.cpfError);
        confSenhaError = findViewById(R.id.confSenhaError);
        senhaError = findViewById(R.id.senhaError);
        telefoneError = findViewById(R.id.telefoneError);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        window.setNavigationBarColor(getResources().getColor(android.R.color.black));


        telefone.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFormatting) return;

                isFormatting = true;

                String digits = s.toString().replaceAll("\\D", "");
                StringBuilder formatted = new StringBuilder();
                if (digits.length() >= 2) {
                    formatted.append("(").append(digits.substring(0, 2)).append(") ");
                    if (digits.length() >= 7) {
                        formatted.append(digits.substring(2, 7)).append("-").append(digits.substring(7, Math.min(11, digits.length())));
                    } else if (digits.length() > 2) {
                        formatted.append(digits.substring(2));
                    }
                } else if (digits.length() > 0) {
                    formatted.append("(").append(digits);
                }

                telefone.setText(formatted.toString());
                telefone.setSelection(formatted.length());

                isFormatting = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camposVazios()) {
                    Toast.makeText(getApplicationContext(), "Obrigatório preencher todos os campos!", Toast.LENGTH_SHORT).show();
                } else if (senha.getText().toString().length() < 8) {
                    senha.setBackgroundResource(R.drawable.borda_vermelha);
                    senhaError.setText("A senha deve ter no mínimo 8 caracteres!");
                    senhaError.setVisibility(View.VISIBLE);
                } else if (!telefone.getText().toString().matches("\\(\\d{2}\\) \\d{5}-\\d{4}")) {
                    telefone.setBackgroundResource(R.drawable.borda_vermelha);
                    telefoneError.setText("Formato de telefone inválido!");
                    telefoneError.setVisibility(View.VISIBLE);
                } else if (!senha.getText().toString().equals(confirmarSenha.getText().toString())) {
                    confirmarSenha.setBackgroundResource(R.drawable.borda_vermelha);
                    confSenhaError.setText("Senhas não conferem!");
                    confSenhaError.setVisibility(View.VISIBLE);
                } else if (!CPFUtils.isValidCPF(cpf.getText().toString())) {
                    cpf.setBackgroundResource(R.drawable.borda_vermelha);
                    cpfError.setText("CPF inválido!");
                    cpfError.setVisibility(View.VISIBLE);
                } else {
                    senha.setBackgroundResource(android.R.drawable.edit_text);
                    senhaError.setVisibility(View.GONE);
                    telefone.setBackgroundResource(android.R.drawable.edit_text);
                    telefoneError.setVisibility(View.GONE);
                    confirmarSenha.setBackgroundResource(android.R.drawable.edit_text);
                    confSenhaError.setVisibility(View.GONE);
                    cpf.setBackgroundResource(android.R.drawable.edit_text);
                    cpfError.setVisibility(View.GONE);
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), senha.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Cadastro efetuado com Sucesso!", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            salvarUsuario();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Erro ao obter usuário!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        new AlertDialog.Builder(CadastroUsuario.this)
                                                .setTitle("Email já cadastrado")
                                                .setMessage("Este email já está registrado. Por favor, use outro email ou faça login.")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }
                                }
                            });
                }
            }
        });

        senha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 8) {
                    senha.setBackgroundResource(android.R.drawable.edit_text);
                    senhaError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        telefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().matches("\\(\\d{2}\\) \\d{5}-\\d{4}")) {
                    telefone.setBackgroundResource(android.R.drawable.edit_text);
                    telefoneError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        confirmarSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(senha.getText().toString())) {
                    confirmarSenha.setBackgroundResource(android.R.drawable.edit_text);
                    confSenhaError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cpf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (CPFUtils.isValidCPF(s.toString())) {
                    cpf.setBackgroundResource(android.R.drawable.edit_text);
                    cpfError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    public boolean camposVazios() {
        return nome.getText().toString().isEmpty() || cpf.getText().toString().isEmpty() || estado.getText().toString().isEmpty() || telefone.getText().toString().isEmpty() || cidade.getText().toString().isEmpty() || bairro.getText().toString().isEmpty() || rua.getText().toString().isEmpty() || numero.getText().toString().isEmpty() || email.getText().toString().isEmpty() || senha.getText().toString().isEmpty() || confirmarSenha.getText().toString().isEmpty();
    }

    private void salvarUsuario() {
        u = new Usuario();
        u.setNome(nome.getText().toString());
        u.setCpf(cpf.getText().toString());
        u.setTelefone(telefone.getText().toString());
        u.setEstado(estado.getText().toString());
        u.setCidade(cidade.getText().toString());
        u.setBairro(bairro.getText().toString());
        u.setRua(rua.getText().toString());
        u.setNumero(numero.getText().toString());
        u.setEmail(email.getText().toString());
        u.setSenha(senha.getText().toString());
        u.setConfSenha(confirmarSenha.getText().toString());

        db.collection("Usuario").add(u)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Usuário salvo no Firestore!", Toast.LENGTH_SHORT).show();
                        limpaCampos();
                        mudarTela();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Erro ao salvar usuário no Firestore!", Toast.LENGTH_SHORT).show();
                        Log.e("CadastroUsuario", "Erro: ", e);
                    }
                });
    }

    private void mudarTela() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void limpaCampos() {
        nome.setText("");
        cpf.setText("");
        telefone.setText("");
        estado.setText("");
        cidade.setText("");
        bairro.setText("");
        rua.setText("");
        numero.setText("");
        email.setText("");
        senha.setText("");
        confirmarSenha.setText("");
    }
}
