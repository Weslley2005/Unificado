package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import br.unigran.tcc.Model.ItemAluguel;
import br.unigran.tcc.R;

public class DetalhesHistoricoAluguel extends AppCompatActivity {
    private RecyclerView recyclerViewItensHistoricoAlugados;
    private DetalhesHistoricoAluguelAdapter DetalhesHistoricoAluguelAdapter;
    private List<ItemAluguel> listaItensAlugados;
    private FirebaseFirestore firestore;
    private ImageButton deletar, editar;
    private String usuarioId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_historico_aluguel);

        recyclerViewItensHistoricoAlugados = findViewById(R.id.recycleViewItensHistoricoAlugados);
        deletar = findViewById(R.id.idDeletarAluguel);
        editar = findViewById(R.id.idEditarAluguel);
        listaItensAlugados = new ArrayList<>();
        DetalhesHistoricoAluguelAdapter = new DetalhesHistoricoAluguelAdapter(listaItensAlugados);

        recyclerViewItensHistoricoAlugados.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItensHistoricoAlugados.setAdapter(DetalhesHistoricoAluguelAdapter);

        firestore = FirebaseFirestore.getInstance();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioId = usuarioAtual != null ? usuarioAtual.getUid() : null;

        String aluguelId = getIntent().getStringExtra("aluguelId");
        if (aluguelId != null && !aluguelId.isEmpty()) {
            carregarItensAlugados(aluguelId);
        } else {
            Toast.makeText(this, "ID do aluguel inválido!", Toast.LENGTH_SHORT).show();
            finish();
        }

        deletar.setOnClickListener(view -> {
            mostrarDialogoDeConfirmacao(aluguelId);
        });

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        window.setNavigationBarColor(getResources().getColor(android.R.color.black));
    }

    private void carregarItensAlugados(String aluguelId) {
        CollectionReference itensRef = firestore.collection("AlugFinaliz")
                .document(aluguelId)
                .collection("ItensAluguel");

        itensRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                listaItensAlugados.clear();
                for (QueryDocumentSnapshot documento : task.getResult()) {
                    ItemAluguel itemAluguel = documento.toObject(ItemAluguel.class);
                    itemAluguel.setId(documento.getId());
                    listaItensAlugados.add(itemAluguel);
                }
                DetalhesHistoricoAluguelAdapter.notifyDataSetChanged();
            } else {
                Log.e("DetalhesAluguel", "Erro ao carregar itens alugados", task.getException());
                Toast.makeText(DetalhesHistoricoAluguel.this, "Erro ao carregar itens!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void mostrarDialogoDeConfirmacao(String aluguelId) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Você realmente deseja deletar este aluguel?")
                .setPositiveButton("Sim", (dialog, which) -> deletarAluguel(aluguelId))
                .setNegativeButton("Não", null)
                .show();
    }

    private void deletarAluguel(String aluguelId) {
        if (listaItensAlugados.isEmpty()) {
            Toast.makeText(this, "Nenhum item para devolver!", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("AlugFinaliz").document(aluguelId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Aluguel deletado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao deletar aluguel", Toast.LENGTH_SHORT).show();
                    Log.e("DetalhesAluguel", "Erro ao deletar aluguel", e);
                });
    }
}



