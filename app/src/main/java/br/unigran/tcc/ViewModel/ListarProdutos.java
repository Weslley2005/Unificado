package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.unigran.tcc.Model.Produtos;
import br.unigran.tcc.R;

public class ListarProdutos extends AppCompatActivity {

    private AlimentoAdapter alimentoAdapter;
    private List<Produtos> alimentoList;
    private List<Produtos> filteredAlimentoList;
    private BebidaAdapter bebidaAdapter;
    private List<Produtos> bebidaList;
    private List<Produtos> filteredBebidaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_produtos);

        RecyclerView recyclerViewAlimentos = findViewById(R.id.recyclerViewAlimentos);
        recyclerViewAlimentos.setLayoutManager(new LinearLayoutManager(this));

        alimentoList = new ArrayList<>();
        filteredAlimentoList = new ArrayList<>();
        alimentoAdapter = new AlimentoAdapter(filteredAlimentoList);
        recyclerViewAlimentos.setAdapter(alimentoAdapter);

        RecyclerView recyclerViewBebidas = findViewById(R.id.recyclerViewBebidas);
        recyclerViewBebidas.setLayoutManager(new LinearLayoutManager(this));

        bebidaList = new ArrayList<>();
        filteredBebidaList = new ArrayList<>();
        bebidaAdapter = new BebidaAdapter(filteredBebidaList);
        recyclerViewBebidas.setAdapter(bebidaAdapter);

        buscarDados();

        setSupportActionBar(findViewById(R.id.toolbar));

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarProdutos(newText);
                return true;
            }
        });

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        window.setNavigationBarColor(getResources().getColor(android.R.color.black));

        int textColor = Color.WHITE;
        int hintColor = Color.WHITE;

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(textColor); // Cor do texto
        searchEditText.setHintTextColor(hintColor); // Cor do hint
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filtrarProdutos(String query) {
        filteredAlimentoList.clear();
        filteredBebidaList.clear();

        for (Produtos produto : alimentoList) {
            if (produto.getNome().toLowerCase().contains(query.toLowerCase())) {
                filteredAlimentoList.add(produto);
            }
        }

        for (Produtos produto : bebidaList) {
            if (produto.getNome().toLowerCase().contains(query.toLowerCase())) {
                filteredBebidaList.add(produto);
            }
        }

        alimentoAdapter.notifyDataSetChanged();
        bebidaAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void buscarDados() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            Log.d("ListarProdutos", "Email do usuário logado: " + email);

            FirebaseFirestore.getInstance().collection("Produtos")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                alimentoList.clear();
                                bebidaList.clear();
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    Produtos produto = document.toObject(Produtos.class);
                                    if ("Alimento".equals(produto.getTipo())) {
                                        alimentoList.add(produto);
                                    } else if ("Bebida".equals(produto.getTipo())) {
                                        bebidaList.add(produto);
                                    }
                                }
                                filteredAlimentoList.addAll(alimentoList);
                                filteredBebidaList.addAll(bebidaList);
                                alimentoAdapter.notifyDataSetChanged();
                                bebidaAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("ListarProdutos", "Consulta retornou nulo.");
                            }
                        } else {
                            Log.e("ListarProdutos", "Erro ao buscar dados", task.getException());
                        }
                    });
        } else {
            Log.e("ListarProdutos", "Nenhum usuário logado");
        }
    }

}