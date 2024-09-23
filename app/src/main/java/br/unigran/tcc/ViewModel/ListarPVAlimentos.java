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

import br.unigran.tcc.Model.ProdutoPP;
import br.unigran.tcc.Model.Produtos;
import br.unigran.tcc.R;

public class ListarPVAlimentos extends AppCompatActivity {

    private PVAlimentoAdapter adapterPVAli;
    private PPVAlimentosAdapter adapterPPAli;
    private List<Produtos> listaDeProdutos;
    private List<Produtos> listaFiltradaDeProdutos;
    private List<ProdutoPP> listaDeProdutosPP;
    private List<ProdutoPP> listaFiltradaDeProdutosPP;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_pvalimentos);

        RecyclerView recyclerViewProduto = findViewById(R.id.recyclerViewAluguel);
        RecyclerView recyclerViewProdutoPP = findViewById(R.id.recyclerViewPPVAlimentos);
        recyclerViewProduto.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProdutoPP.setLayoutManager(new LinearLayoutManager(this));

        listaDeProdutos = new ArrayList<>();
        listaFiltradaDeProdutos = new ArrayList<>();
        listaDeProdutosPP = new ArrayList<>();
        listaFiltradaDeProdutosPP = new ArrayList<>();

        adapterPVAli = new PVAlimentoAdapter(listaFiltradaDeProdutos, this);
        adapterPPAli = new PPVAlimentosAdapter(listaFiltradaDeProdutosPP, this);

        recyclerViewProduto.setAdapter(adapterPVAli);
        recyclerViewProdutoPP.setAdapter(adapterPPAli);

        buscarDados();
        buscarDadosPP(); // Nova função para buscar produtos PP

        setSupportActionBar(findViewById(R.id.toolbar));

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String novoTexto) {
                filtrarProdutos(novoTexto);
                filtrarProdutosPP(novoTexto); // Filtrar também produtos PP
                return true;
            }
        });

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));
        window.setNavigationBarColor(getResources().getColor(android.R.color.black));

        int corDoTexto = Color.WHITE;
        int corDoHint = Color.WHITE;

        EditText campoDeBusca = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        campoDeBusca.setTextColor(corDoTexto);
        campoDeBusca.setHintTextColor(corDoHint);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filtrarProdutos(String consulta) {
        listaFiltradaDeProdutos.clear();
        for (Produtos produto : listaDeProdutos) {
            if (produto.getNome().toLowerCase().contains(consulta.toLowerCase())) {
                listaFiltradaDeProdutos.add(produto);
            }
        }
        adapterPVAli.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filtrarProdutosPP(String consulta) {
        listaFiltradaDeProdutosPP.clear();
        for (ProdutoPP produtoPP : listaDeProdutosPP) {
            if (produtoPP.getNome().toLowerCase().contains(consulta.toLowerCase())) {
                listaFiltradaDeProdutosPP.add(produtoPP);
            }
        }
        adapterPPAli.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void buscarDados() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String email = usuarioAtual.getEmail();
            Log.d("ListarProdutos", "Email do usuário logado: " + email);

            FirebaseFirestore.getInstance().collection("Produtos")
                    .get()
                    .addOnCompleteListener(tarefa -> {
                        if (tarefa.isSuccessful()) {
                            QuerySnapshot querySnapshot = tarefa.getResult();
                            if (querySnapshot != null) {
                                listaDeProdutos.clear();
                                for (QueryDocumentSnapshot documento : querySnapshot) {
                                    Produtos produto = documento.toObject(Produtos.class);
                                    produto.setId(documento.getId());
                                    listaDeProdutos.add(produto);
                                }
                                listaFiltradaDeProdutos.addAll(listaDeProdutos);
                                adapterPVAli.notifyDataSetChanged();
                            } else {
                                Log.d("ListarProdutos", "Consulta retornou nulo.");
                            }
                        } else {
                            Log.e("ListarProdutos", "Erro ao buscar dados", tarefa.getException());
                        }
                    });
        } else {
            Log.e("ListarProdutos", "Nenhum usuário logado");
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void buscarDadosPP() {
        FirebaseFirestore.getInstance().collection("ProdutosPP")
                .get()
                .addOnCompleteListener(tarefa -> {
                    if (tarefa.isSuccessful()) {
                        QuerySnapshot querySnapshot = tarefa.getResult();
                        if (querySnapshot != null) {
                            listaDeProdutosPP.clear();
                            for (QueryDocumentSnapshot documento : querySnapshot) {
                                ProdutoPP produtoPP = documento.toObject(ProdutoPP.class);
                                produtoPP.setId(documento.getId());
                                listaDeProdutosPP.add(produtoPP);
                            }
                            listaFiltradaDeProdutosPP.addAll(listaDeProdutosPP);
                            adapterPPAli.notifyDataSetChanged();
                        } else {
                            Log.d("ListarProdutosPP", "Consulta retornou nulo.");
                        }
                    } else {
                        Log.e("ListarProdutosPP", "Erro ao buscar dados", tarefa.getException());
                    }
                });
    }
}
