package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import br.unigran.tcc.Model.FinalizaAlugueis;
import br.unigran.tcc.R;

public class FinalizarAluguel extends AppCompatActivity {

    private RecyclerView recyclerViewAlugueis;
    private FinalizarAluguelAdapter aluguelAdapter;
    private List<FinalizaAlugueis> listaAlugueis;
    private List<FinalizaAlugueis> listaItensAlugueisFiltrados;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_aluguel);

        setSupportActionBar(findViewById(R.id.toolbar));

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);

        recyclerViewAlugueis = findViewById(R.id.recycleViewAlugueis);
        listaAlugueis = new ArrayList<>();
        listaItensAlugueisFiltrados = new ArrayList<>();
        aluguelAdapter = new FinalizarAluguelAdapter(listaAlugueis, this);

        recyclerViewAlugueis.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAlugueis.setAdapter(aluguelAdapter);

        carregarAlugueis();

        Window janela = getWindow();
        janela.setStatusBarColor(getResources().getColor(android.R.color.black));
        janela.setNavigationBarColor(getResources().getColor(android.R.color.black));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarItens(newText);
                return true;
            }
        });

        int textColor = Color.WHITE;
        int hintColor = Color.WHITE;

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(textColor);
        searchEditText.setHintTextColor(hintColor);
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
                                FinalizaAlugueis aluguel = documento.toObject(FinalizaAlugueis.class);
                                aluguel.setId(documento.getId());
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

    private void filtrarItens(String texto) {
        listaAlugueis.clear();
        if (texto.isEmpty()) {
            listaAlugueis.addAll(listaItensAlugueisFiltrados); // Se o texto estiver vazio, exibe todos os itens
        } else {
            String filtro = texto.toLowerCase(); // Converte o texto para minúsculas para comparação
            for (FinalizaAlugueis item : listaItensAlugueisFiltrados) {
                if (item.getIdNomenAluguel().toLowerCase().contains(filtro)) { // Filtra os itens pelo nome
                    listaAlugueis.add(item); // Adiciona o item filtrado à lista de vendas
                }
            }
        }
        aluguelAdapter.notifyDataSetChanged(); // Notifica o adapter sobre a mudança
    }
}
