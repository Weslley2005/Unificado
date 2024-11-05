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

import br.unigran.tcc.Model.FinalizaVendas;
import br.unigran.tcc.R;

public class FinalizarVendas extends AppCompatActivity {
    private RecyclerView recyclerViewVendas;
    private FinalizarVendasAdapter vendasAdapter;
    private List<FinalizaVendas> listaVendas;
    private List<FinalizaVendas> listaItensVendasFiltrados; // Lista para manter os itens filtrados

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_vendas);

        setSupportActionBar(findViewById(R.id.toolbar));

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);
        recyclerViewVendas = findViewById(R.id.recycleViewAlugueis);
        listaVendas = new ArrayList<>();
        listaItensVendasFiltrados = new ArrayList<>();
        vendasAdapter = new FinalizarVendasAdapter(listaVendas, this);

        recyclerViewVendas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVendas.setAdapter(vendasAdapter);

        carregarVendas();

        // Configuração da cor da barra de status e de navegação
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

    private void carregarVendas() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();
            FirebaseFirestore.getInstance().collection("Compras")
                    .whereEqualTo("usuarioId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            listaVendas.clear();
                            for (QueryDocumentSnapshot documento : task.getResult()) {
                                FinalizaVendas vendas = documento.toObject(FinalizaVendas.class);
                                vendas.setId(documento.getId());
                                listaVendas.add(vendas);
                            }
                            // Manter uma cópia da lista original para filtrar posteriormente
                            listaItensVendasFiltrados.clear();
                            listaItensVendasFiltrados.addAll(listaVendas);
                            vendasAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("FinalizarVendas", "Erro ao carregar vendas", task.getException());
                        }
                    });
        } else {
            Toast.makeText(this, "Você precisa estar logado para visualizar suas vendas.", Toast.LENGTH_SHORT).show();
        }
    }

    private void filtrarItens(String texto) {
        listaVendas.clear();
        if (texto.isEmpty()) {
            listaVendas.addAll(listaItensVendasFiltrados); // Se o texto estiver vazio, exibe todos os itens
        } else {
            String filtro = texto.toLowerCase(); // Converte o texto para minúsculas para comparação
            for (FinalizaVendas item : listaItensVendasFiltrados) {
                if (item.getNomenAluguel().toLowerCase().contains(filtro)) { // Filtra os itens pelo nome
                    listaVendas.add(item); // Adiciona o item filtrado à lista de vendas
                }
            }
        }
        vendasAdapter.notifyDataSetChanged(); // Notifica o adapter sobre a mudança
    }
}
