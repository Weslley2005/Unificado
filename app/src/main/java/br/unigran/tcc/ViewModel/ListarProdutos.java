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

    private ProdutoAdapter produtoAdapter;
    private List<Produtos> produtoList;
    private List<Produtos> filteredProdutoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_produtos);

        RecyclerView recyclerViewProduto = findViewById(R.id.recyclerViewProduto);
        recyclerViewProduto.setLayoutManager(new LinearLayoutManager(this));

        produtoList = new ArrayList<>();
        filteredProdutoList = new ArrayList<>();
        produtoAdapter = new ProdutoAdapter(filteredProdutoList, this); // Passa a referência da atividade
        recyclerViewProduto.setAdapter(produtoAdapter);

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
        searchEditText.setTextColor(textColor);
        searchEditText.setHintTextColor(hintColor);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filtrarProdutos(String query) {
        filteredProdutoList.clear();

        for (Produtos produto : produtoList) {
            if (produto.getNome().toLowerCase().contains(query.toLowerCase())) {
                filteredProdutoList.add(produto);
            }
        }
        produtoAdapter.notifyDataSetChanged();
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
                                produtoList.clear();
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    Produtos produto = document.toObject(Produtos.class);
                                    produto.setId(document.getId()); // Definir o ID do documento
                                    produtoList.add(produto);
                                }
                                filteredProdutoList.addAll(produtoList);
                                produtoAdapter.notifyDataSetChanged();
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

    // Método para mostrar o pop-up de confirmação
    public void showConfirmationDialog(int position, Produtos produto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmação");
        builder.setMessage("Você tem certeza que deseja deletar este item?");

        // Botão de confirmação
        builder.setPositiveButton("Sim", (dialog, which) -> deleteItem(position, produto));

        // Botão de cancelamento
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());

        // Exibir o diálogo
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Método para deletar o item
    private void deleteItem(int position, Produtos produto) {
        FirebaseFirestore.getInstance().collection("Produtos")
                .document(produto.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    produtoList.remove(position);
                    filteredProdutoList.remove(position);
                    produtoAdapter.notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> Log.e("ListarProdutos", "Erro ao deletar item", e));
    }
}
