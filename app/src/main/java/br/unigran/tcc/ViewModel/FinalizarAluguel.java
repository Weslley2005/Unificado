package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import br.unigran.tcc.Model.FinalizarAlugueis; // Assegure-se de ter essa classe criada
import br.unigran.tcc.R;

public class FinalizarAluguel extends AppCompatActivity {

    private RecyclerView recyclerViewAlugueis;
    private FinalizarAluguelAdapter aluguelAdapter;
    private List<FinalizarAlugueis> listaAlugueis;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_aluguel);

        recyclerViewAlugueis = findViewById(R.id.recycleViewAlugueis);
        listaAlugueis = new ArrayList<>();
        aluguelAdapter = new FinalizarAluguelAdapter(listaAlugueis, this);

        recyclerViewAlugueis.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAlugueis.setAdapter(aluguelAdapter);

        carregarAlugueis();
    }

    private void carregarAlugueis() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();
            FirebaseFirestore.getInstance().collection("AluguelFinalizadas")
                    .whereEqualTo("usuarioId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            listaAlugueis.clear();
                            for (QueryDocumentSnapshot documento : task.getResult()) {
                                FinalizarAlugueis aluguel = documento.toObject(FinalizarAlugueis.class); // Assegure-se de que esta classe está correta
                                aluguel.setId(documento.getId()); // Certifique-se de que a classe Aluguel tem um método setId
                                listaAlugueis.add(aluguel);
                            }
                            aluguelAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("FinalizarAluguelActivity", "Erro ao carregar aluguéis", task.getException());
                        }
                    });
        } else {
            Toast.makeText(this, "Você precisa estar logado para visualizar seus aluguéis.", Toast.LENGTH_SHORT).show();
        }
    }
}
