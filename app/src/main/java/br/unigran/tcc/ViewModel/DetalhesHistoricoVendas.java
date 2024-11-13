package br.unigran.tcc.ViewModel;

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

import br.unigran.tcc.Model.ItemVendas;
import br.unigran.tcc.R;

public class DetalhesHistoricoVendas extends AppCompatActivity {

    private RecyclerView recyclerViewItensHistoricoVendas;
    private DetalhesHistoricoVendasAdapter detalhesHistoricoVendasAdapter;
    private List<ItemVendas> listaItensVendas;
    private FirebaseFirestore firestore;
    private ImageButton deletar;
    private String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_hitorico_vendas);

        recyclerViewItensHistoricoVendas = findViewById(R.id.recycleViewItensHistoricoVendas);
        deletar = findViewById(R.id.idDeletarAluguel);
        listaItensVendas = new ArrayList<>();
        detalhesHistoricoVendasAdapter = new DetalhesHistoricoVendasAdapter(listaItensVendas);

        recyclerViewItensHistoricoVendas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItensHistoricoVendas.setAdapter(detalhesHistoricoVendasAdapter);

        firestore = FirebaseFirestore.getInstance();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioId = usuarioAtual != null ? usuarioAtual.getUid() : null;

        String aluguelId = getIntent().getStringExtra("aluguelId");
        if (aluguelId != null && !aluguelId.isEmpty()) {
            carregarItensVendas(aluguelId);
        } else {
            Toast.makeText(this, "ID da venda inválido!", Toast.LENGTH_SHORT).show();
            finish();
        }

        deletar.setOnClickListener(view -> {
            mostrarDialogoDeConfirmacao(aluguelId);
        });

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        window.setNavigationBarColor(getResources().getColor(android.R.color.black));
    }

    private void carregarItensVendas(String vendaId) {
        CollectionReference itensRef = firestore.collection("VendasFinaliz")
                .document(vendaId)
                .collection("Itens");

        itensRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                listaItensVendas.clear();
                for (QueryDocumentSnapshot documento : task.getResult()) {
                    ItemVendas itemVendas = documento.toObject(ItemVendas.class);
                    itemVendas.setId(documento.getId());
                    listaItensVendas.add(itemVendas);
                }
                detalhesHistoricoVendasAdapter.notifyDataSetChanged();
            } else {
                Log.e("DetalhesHistoricoVendas", "Erro ao carregar itens vendidos", task.getException());
                Toast.makeText(DetalhesHistoricoVendas.this, "Erro ao carregar itens!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoDeConfirmacao(String vendaId) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Você realmente deseja deletar esta venda?")
                .setPositiveButton("Sim", (dialog, which) -> deletarVenda(vendaId))
                .setNegativeButton("Não", null)
                .show();
    }

    private void deletarVenda(String vendaId) {
        if (listaItensVendas.isEmpty()) {
            Toast.makeText(this, "Nenhum item para excluir!", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("Compras").document(vendaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Venda deletada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao deletar venda", Toast.LENGTH_SHORT).show();
                    Log.e("DetalhesHistoricoVendas", "Erro ao deletar venda", e);
                });
    }
}
