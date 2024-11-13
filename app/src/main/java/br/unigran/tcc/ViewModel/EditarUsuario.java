package br.unigran.tcc.ViewModel;

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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import br.unigran.tcc.R;

public class EditarUsuario extends AppCompatActivity {

    private EditText telefone;
    private EditText estado;
    private EditText cidade;
    private EditText bairro;
    private EditText rua;
    private EditText numero;
    private Button salvar;
    private TextView errorTelefone;
    private TextView estadoErro;
    private TextView cidadeErro;
    private TextView bairroErro;
    private TextView ruaErro;
    private TextView numeroErro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        telefone = findViewById(R.id.idEditarTelefone);
        estado = findViewById(R.id.idEditarEstado);
        cidade = findViewById(R.id.idEditarCidade);
        bairro = findViewById(R.id.idEditarBairro);
        rua = findViewById(R.id.idEditarRua);
        numero = findViewById(R.id.idEditarNumero);
        salvar = findViewById(R.id.idSalvar);
        errorTelefone = findViewById(R.id.idErroTelefone);
        estadoErro = findViewById(R.id.estadoError);
        cidadeErro = findViewById(R.id.cidadeError);
        bairroErro = findViewById(R.id.bairroError);
        ruaErro = findViewById(R.id.ruaError);
        numeroErro = findViewById(R.id.numeroError);

        carregarDados();
        adicionarTextWatchers();

        salvar.setOnClickListener(v -> salvarDados());

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));
        window.setNavigationBarColor(getResources().getColor(android.R.color.black));
    }

    private void carregarDados() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            Log.d("EditarUsuario", "Email do usuário logado: " + email);

            FirebaseFirestore.getInstance().collection("Usuario")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                Log.d("EditarUsuario", "Número de documentos encontrados: " + querySnapshot.size());
                                QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                                telefone.setText(document.getString("telefone"));
                                estado.setText(document.getString("estado"));
                                cidade.setText(document.getString("cidade"));
                                bairro.setText(document.getString("bairro"));
                                rua.setText(document.getString("rua"));
                                numero.setText(document.getString("numero"));
                            } else {
                                Toast.makeText(EditarUsuario.this, "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show();
                                Log.d("EditarUsuario", "Nenhum documento encontrado para o email: " + email);
                            }
                        } else {
                            Log.e("EditarUsuario", "Erro ao buscar dados", task.getException());
                            Toast.makeText(EditarUsuario.this, "Erro ao carregar dados: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Erro: Usuário não autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarDados() {
        if (camposVazios()) {
            return;
        }

        String telefoneText = telefone.getText().toString();
        String estadoText = estado.getText().toString();
        String cidadeText = cidade.getText().toString();
        String bairroText = bairro.getText().toString();
        String ruaText = rua.getText().toString();
        String numeroText = numero.getText().toString();

        if (!telefoneText.matches("\\(\\d{2}\\) \\d{5}-\\d{4}")) {
            telefone.setBackgroundResource(R.drawable.borda_vermelha);
            errorTelefone.setText("Formato de telefone inválido!");
            errorTelefone.setVisibility(View.VISIBLE);
            return;
        }


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            FirebaseFirestore.getInstance().collection("Usuario")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                String docId = querySnapshot.getDocuments().get(0).getId();
                                FirebaseFirestore.getInstance().collection("Usuario")
                                        .document(docId)
                                        .update("telefone", telefoneText,
                                                "estado", estadoText,
                                                "cidade", cidadeText,
                                                "bairro", bairroText,
                                                "rua", ruaText,
                                                "numero", numeroText)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(EditarUsuario.this, "Dados salvos com sucesso", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("EditarUsuario", "Erro ao salvar dados", e);
                                            Toast.makeText(EditarUsuario.this, "Erro ao salvar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    });
        }
    }

    private boolean camposVazios() {
        boolean vazio = false;

        if (estado.getText().toString().isEmpty()) {
            estado.setBackgroundResource(R.drawable.borda_vermelha);
            estadoErro.setText("Campo obrigatório!");
            estadoErro.setVisibility(View.VISIBLE);
            vazio = true;
        }

        if (cidade.getText().toString().isEmpty()) {
            cidade.setBackgroundResource(R.drawable.borda_vermelha);
            cidadeErro.setText("Campo obrigatório!");
            cidadeErro.setVisibility(View.VISIBLE);
            vazio = true;
        }

        if (bairro.getText().toString().isEmpty()) {
            bairro.setBackgroundResource(R.drawable.borda_vermelha);
            bairroErro.setText("Campo obrigatório!");
            bairroErro.setVisibility(View.VISIBLE);
            vazio = true;
        }

        if (rua.getText().toString().isEmpty()) {
            rua.setBackgroundResource(R.drawable.borda_vermelha);
            ruaErro.setText("Campo obrigatório!");
            ruaErro.setVisibility(View.VISIBLE);
            vazio = true;
        }

        if (numero.getText().toString().isEmpty()) {
            numero.setBackgroundResource(R.drawable.borda_vermelha);
            numeroErro.setText("Campo obrigatório!");
            numeroErro.setVisibility(View.VISIBLE);
            vazio = true;
        }

        return vazio;
    }

    private void adicionarTextWatchers() {
        telefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    telefone.setBackgroundResource(android.R.drawable.edit_text);
                    errorTelefone.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        estado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    estado.setBackgroundResource(android.R.drawable.edit_text);
                    estadoErro.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cidade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    cidade.setBackgroundResource(android.R.drawable.edit_text);
                    cidadeErro.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        bairro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    bairro.setBackgroundResource(android.R.drawable.edit_text);
                    bairroErro.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        rua.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    rua.setBackgroundResource(android.R.drawable.edit_text);
                    ruaErro.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        numero.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    numero.setBackgroundResource(android.R.drawable.edit_text);
                    numeroErro.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


    }
    public void voltar() {
        Intent intent = new Intent(this, DadosUsuario.class);
        startActivity(intent);
    }

}
