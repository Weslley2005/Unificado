package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.unigran.tcc.Model.Usuario;
import br.unigran.tcc.R;

public class DadosUsuario extends AppCompatActivity {

    private UsuarioAdapter usuarioAdapter;
    private List<Usuario> usuarioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_usuario);

        RecyclerView recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(this));

        usuarioList = new ArrayList<>();
        usuarioAdapter = new UsuarioAdapter(usuarioList);
        recyclerViewUsuarios.setAdapter(usuarioAdapter);

        buscarDados();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void buscarDados() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            Log.d("DadosUsuarioActivity", "Email do usuário logado: " + email);

            FirebaseFirestore.getInstance().collection("Usuario")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                Log.d("DadosUsuarioActivity", "Número de documentos encontrados: " + querySnapshot.size());
                                boolean userFound = false;
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    Usuario usuario = document.toObject(Usuario.class);
                                    Log.d("DadosUsuarioActivity", "Usuário encontrado: " + usuario.getNome());
                                    userFound = true;
                                    usuarioList.add(usuario);
                                }
                                if (!userFound) {
                                    Log.d("DadosUsuarioActivity", "Nenhum usuário encontrado com o email fornecido.");
                                }
                                usuarioAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("DadosUsuarioActivity", "Consulta retornou nulo.");
                            }
                        } else {
                            Log.e("DadosUsuarioActivity", "Erro ao buscar dados", task.getException());
                        }
                    });
        } else {
            Log.e("DadosUsuarioActivity", "Nenhum usuário logado");
        }
    }

    public void editarUsuario(View view) {
        Intent intent = new Intent(this, EditarUsuario.class);
        startActivity(intent);
    }
}
