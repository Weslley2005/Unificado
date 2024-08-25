package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
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

    private ProdutoAdapter adaptadorProdutos;
    private List<Produtos> listaDeProdutos;
    private List<Produtos> listaFiltradaDeProdutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_produtos);

        RecyclerView recyclerViewProduto = findViewById(R.id.recyclerViewProduto);
        recyclerViewProduto.setLayoutManager(new LinearLayoutManager(this));

        listaDeProdutos = new ArrayList<>();
        listaFiltradaDeProdutos = new ArrayList<>();
        adaptadorProdutos = new ProdutoAdapter(listaFiltradaDeProdutos, this);
        recyclerViewProduto.setAdapter(adaptadorProdutos);

        buscarDados();

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
        adaptadorProdutos.notifyDataSetChanged();
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
                                adaptadorProdutos.notifyDataSetChanged();
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

    public void mostrarDialogoDeConfirmacao(int posicao, Produtos produto) {
        AlertDialog.Builder construtor = new AlertDialog.Builder(this);
        construtor.setTitle("Confirmação");
        construtor.setMessage("Você tem certeza que deseja deletar este item?");

        construtor.setPositiveButton("Sim", (dialog, which) -> deletarItem(posicao, produto));

        construtor.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());

        AlertDialog alertaDialogo = construtor.create();
        alertaDialogo.show();
    }

    private void deletarItem(int posicao, Produtos produto) {
        FirebaseFirestore.getInstance().collection("Produtos")
                .document(produto.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listaDeProdutos.remove(posicao);
                    listaFiltradaDeProdutos.remove(posicao);
                    adaptadorProdutos.notifyItemRemoved(posicao);
                })
                .addOnFailureListener(e -> Log.e("ListarProdutos", "Erro ao deletar item", e));
    }
}
