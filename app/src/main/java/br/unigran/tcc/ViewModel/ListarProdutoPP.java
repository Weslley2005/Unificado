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

import br.unigran.tcc.Model.ProdutoPP;
import br.unigran.tcc.R;

public class ListarProdutoPP extends AppCompatActivity {

    private ProdutoPPAdapter produtoPPAdapter;
    private List<ProdutoPP> produtoPPList;
    private List<ProdutoPP> filteredProdutoPPList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_produtos_pp);

        RecyclerView recyclerViewProdutoPP = findViewById(R.id.recyclerViewProdutoPP);
        recyclerViewProdutoPP.setLayoutManager(new LinearLayoutManager(this));

        produtoPPList = new ArrayList<>();
        filteredProdutoPPList = new ArrayList<>();
        produtoPPAdapter = new ProdutoPPAdapter(filteredProdutoPPList, this);
        recyclerViewProdutoPP.setAdapter(produtoPPAdapter);

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
                filtrarProdutoPP(newText);
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
    private void filtrarProdutoPP(String query) {
        filteredProdutoPPList.clear();

        for (ProdutoPP produtoPP : produtoPPList) {
            if (produtoPP.getNome().toLowerCase().contains(query.toLowerCase())) {
                filteredProdutoPPList.add(produtoPP);
            }
        }
        produtoPPAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void buscarDados() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            Log.d("ListarProdutoPP", "Email do usuário logado: " + email);

            FirebaseFirestore.getInstance().collection("ProdutosPP")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                produtoPPList.clear();
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    ProdutoPP produtoPP = document.toObject(ProdutoPP.class);
                                    produtoPP.setId(document.getId()); //
                                    produtoPPList.add(produtoPP);
                                }
                                filteredProdutoPPList.addAll(produtoPPList);
                                produtoPPAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("ListarProdutoPP", "Consulta retornou nulo.");
                            }
                        } else {
                            Log.e("ListarProdutoPP", "Erro ao buscar dados", task.getException());
                        }
                    });
        } else {
            Log.e("ListarProdutoPP", "Nenhum usuário logado");
        }
    }

    public void showConfirmationDialog(int position, ProdutoPP produtoPP) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmação");
        builder.setMessage("Você tem certeza que deseja deletar este item?");

        builder.setPositiveButton("Sim", (dialog, which) -> deleteItem(position, produtoPP));

        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem(int position, ProdutoPP produtoPP) {
        FirebaseFirestore.getInstance().collection("ProdutosPP")
                .document(produtoPP.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    produtoPPList.remove(position);
                    filteredProdutoPPList.remove(position);
                    produtoPPAdapter.notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> Log.e("ListarProdutoPP", "Erro ao deletar item", e));
    }

}