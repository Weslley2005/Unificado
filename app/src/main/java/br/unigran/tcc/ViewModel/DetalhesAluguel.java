package br.unigran.tcc.ViewModel;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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

public class DetalhesAluguel extends AppCompatActivity {

    private RecyclerView recyclerViewItensAlugados;
    private ItemAluguelAdapter itemAluguelAdapter;
    private List<ItemAluguel> listaItensAlugados;
    private Button buttonFinalizarAluguel;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_aluguel);

        // Inicialização das variáveis
        recyclerViewItensAlugados = findViewById(R.id.recycleViewItensAlugados);
        buttonFinalizarAluguel = findViewById(R.id.buttonFinalizarAluguel);
        listaItensAlugados = new ArrayList<>();
        itemAluguelAdapter = new ItemAluguelAdapter(listaItensAlugados);

        // Configuração do RecyclerView
        recyclerViewItensAlugados.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItensAlugados.setAdapter(itemAluguelAdapter);

        // Inicializa o Firestore
        firestore = FirebaseFirestore.getInstance();

        // Obtém o ID do aluguel da Intent
        String aluguelId = getIntent().getStringExtra("aluguelId");
        if (aluguelId != null && !aluguelId.isEmpty()) {
            carregarItensAlugados(aluguelId);
            configurarBotaoFinalizar(aluguelId);
        } else {
            Toast.makeText(this, "ID do aluguel inválido!", Toast.LENGTH_SHORT).show();
            finish(); // Fecha a Activity se o aluguelId for inválido
        }
    }

    // Método para carregar os itens alugados
    private void carregarItensAlugados(String aluguelId) {
        CollectionReference itensRef = firestore.collection("AluguelFinalizadas")
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
                itemAluguelAdapter.notifyDataSetChanged();
            } else {
                Log.e("DetalhesAluguel", "Erro ao carregar itens alugados", task.getException());
                Toast.makeText(DetalhesAluguel.this, "Erro ao carregar itens!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para configurar o botão de finalizar aluguel
    private void configurarBotaoFinalizar(String aluguelId) {
        buttonFinalizarAluguel.setOnClickListener(v -> finalizarAluguel(aluguelId));
    }

    // Método para finalizar o aluguel
    private void finalizarAluguel(String aluguelId) {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            return; // Para se o usuário não estiver autenticado
        }

        String userId = usuarioAtual.getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Devolver itens e depois deletar o aluguel
        int totalItens = listaItensAlugados.size();
        if (totalItens == 0) {
            Toast.makeText(this, "Nenhum item para devolver!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Contador para controlar o número de itens devolvidos
        final int[] itensDevolvidos = {0};

        for (ItemAluguel item : listaItensAlugados) {
            devolverItem(item, () -> {
                itensDevolvidos[0]++;
                // Após devolver todos os itens, remove o documento do aluguel
                if (itensDevolvidos[0] == totalItens) {
                    firestore.collection("CarrinhoAluguel").document(userId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Aluguel finalizado com sucesso!", Toast.LENGTH_SHORT).show();
                                finish(); // Fecha a Activity após finalizar o aluguel
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Erro ao finalizar aluguel", Toast.LENGTH_SHORT).show();
                                Log.e("DetalhesAluguel", "Erro ao finalizar aluguel", e);
                            });
                }
            });
        }
    }

    // Método para transferir itens e finalizar o aluguel


    // Método para devolver um item e atualizar seu estoque no Firestore
    private void devolverItem(ItemAluguel itemAluguel, Runnable onComplete) {
        firestore.collection("EquipamentoAluguel")
                .whereEqualTo("nome", itemAluguel.getNome())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        int estoqueAtual = document.getLong("qtdAluguel").intValue();

                        Log.d("CarrinhoActivity", "Estoque atual do produto '" + itemAluguel.getNome() + "': " + estoqueAtual);

                        int novaQuantidadeEstoque = estoqueAtual + itemAluguel.getQuantidade();

                        Log.d("CarrinhoActivity", "Nova quantidade de estoque após adição: " + novaQuantidadeEstoque);

                        document.getReference().update("qtdAluguel", novaQuantidadeEstoque)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("CarrinhoActivity", "Estoque atualizado com sucesso!");
                                    onComplete.run(); // Chama o callback quando a atualização for bem-sucedida
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("CarrinhoActivity", "Erro ao atualizar estoque", e);
                                    onComplete.run(); // Chama o callback mesmo em caso de erro para continuar o processo
                                });
                    } else {
                        Log.e("CarrinhoActivity", "Produto não encontrado para atualizar estoque");
                        onComplete.run(); // Chama o callback para continuar o processo mesmo que o produto não tenha sido encontrado
                    }
                });
    }
}
