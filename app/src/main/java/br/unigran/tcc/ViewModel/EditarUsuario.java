package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import br.unigran.tcc.R;

public class EditarUsuario extends AppCompatActivity {

    private EditText telefone;
    private EditText estado;
    private EditText cidade;
    private EditText bairro;
    private EditText rua;
    private EditText numero;
    private Button salvar;

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

        carregarDados();

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarDados();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
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
                            if (querySnapshot != null) {
                                Log.d("EditarUsuario", "Número de documentos encontrados: " + querySnapshot.size());
                                if (!querySnapshot.isEmpty()) {
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
                                Log.d("EditarUsuario", "Consulta retornou nulo.");
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
        String telefoneText = telefone.getText().toString();
        String estadoText = estado.getText().toString();
        String cidadeText = cidade.getText().toString();
        String bairroText = bairro.getText().toString();
        String ruaText = rua.getText().toString();
        String numeroText = numero.getText().toString();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Usuario")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                                DocumentReference docRef = db.collection("Usuario").document(document.getId());

                                Map<String, Object> user = new HashMap<>();
                                user.put("telefone", telefoneText);
                                user.put("estado", estadoText);
                                user.put("cidade", cidadeText);
                                user.put("bairro", bairroText);
                                user.put("rua", ruaText);
                                user.put("numero", numeroText);

                                docRef.update(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(EditarUsuario.this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(EditarUsuario.this, "Erro ao atualizar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.e("EditarUsuario", "Erro ao atualizar dados: " + e.getMessage());
                                        });
                            } else {
                                Toast.makeText(EditarUsuario.this, "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show();
                                Log.d("EditarUsuario", "Nenhum documento encontrado para o email: " + email);
                            }
                        } else {
                            Log.e("EditarUsuario", "Erro ao buscar dados", task.getException());
                            Toast.makeText(EditarUsuario.this, "Erro ao buscar dados: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Erro: Usuário não autenticado", Toast.LENGTH_SHORT).show();
        }
    }



}
