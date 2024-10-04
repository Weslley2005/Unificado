package br.unigran.tcc.ViewModel;

import android.os.Bundle;
import android.util.Log;
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

public class DetalhesHistoricoVendas extends AppCompatActivity { // Corrigido o nome da classe

    private RecyclerView recyclerViewItensHistoricoVendas;
    private DetalhesHistoricoVendasAdapter detalhesHistoricoVendasAdapter; // Corrigido nome do adapter
    private List<ItemVendas> listaItensVendas;
    private FirebaseFirestore firestore;
    private ImageButton deletar;
    private String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_hitorico_vendas);

        recyclerViewItensHistoricoVendas = findViewById(R.id.recycleViewItensHistoricoVendas);
        deletar = findViewById(R.id.idDeletarAluguel); // Verificar se o ID do botão está correto
        listaItensVendas = new ArrayList<>();
        detalhesHistoricoVendasAdapter = new DetalhesHistoricoVendasAdapter(listaItensVendas); // Corrigido o nome do adapter

        // Configuração do RecyclerView
        recyclerViewItensHistoricoVendas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItensHistoricoVendas.setAdapter(detalhesHistoricoVendasAdapter);

        // Inicializa o Firestore
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioId = usuarioAtual != null ? usuarioAtual.getUid() : null; // Definindo usuarioId aqui

        // Obtém o ID da venda da Intent
        String aluguelId = getIntent().getStringExtra("aluguelId"); // Corrigir para venda se necessário
        if (aluguelId != null && !aluguelId.isEmpty()) {
            carregarItensVendas(aluguelId); // Nome adequado ao contexto de vendas
        } else {
            Toast.makeText(this, "ID da venda inválido!", Toast.LENGTH_SHORT).show(); // Corrigido para venda
            finish(); // Fecha a Activity se o aluguelId (ou vendaId) for inválido
        }

        deletar.setOnClickListener(view -> {
            // Exibe diálogo de confirmação antes de deletar
            mostrarDialogoDeConfirmacao(aluguelId);
        });
    }

    // Método para carregar os itens vendidos
    private void carregarItensVendas(String vendaId) { // Nome corrigido
        CollectionReference itensRef = firestore.collection("Compras")
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
                detalhesHistoricoVendasAdapter.notifyDataSetChanged(); // Corrigido para chamar no adapter
            } else {
                Log.e("DetalhesHistoricoVendas", "Erro ao carregar itens vendidos", task.getException()); // Mensagem de log corrigida
                Toast.makeText(DetalhesHistoricoVendas.this, "Erro ao carregar itens!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoDeConfirmacao(String vendaId) { // Nome corrigido
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Você realmente deseja deletar esta venda?") // Mensagem corrigida
                .setPositiveButton("Sim", (dialog, which) -> deletarVenda(vendaId)) // Nome corrigido
                .setNegativeButton("Não", null)
                .show();
    }

    private void deletarVenda(String vendaId) { // Nome corrigido
        // Verifica se há itens vendidos
        if (listaItensVendas.isEmpty()) {
            Toast.makeText(this, "Nenhum item para excluir!", Toast.LENGTH_SHORT).show(); // Mensagem corrigida
            return;
        }

        // Remove o documento da venda diretamente
        firestore.collection("Compras").document(vendaId) // Verifique o nome da coleção no Firestore
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Venda deletada com sucesso!", Toast.LENGTH_SHORT).show(); // Mensagem corrigida
                    finish(); // Fecha a Activity após deletar
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao deletar venda", Toast.LENGTH_SHORT).show(); // Mensagem corrigida
                    Log.e("DetalhesHistoricoVendas", "Erro ao deletar venda", e); // Mensagem de log corrigida
                });
    }
}
