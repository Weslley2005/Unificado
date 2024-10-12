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

import br.unigran.tcc.Model.EquipamentoAluguel;
import br.unigran.tcc.R;

public class ListarEquipAlug extends AppCompatActivity {

    private EquipAlugAdapter equipAlugAdapter;
    private List<EquipamentoAluguel> equipAlugList;
    private List<EquipamentoAluguel> filteredEquipAlugList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_equip_alug);

        RecyclerView recyclerViewEquipAlug = findViewById(R.id.recyclerViewEquipAlug);
        recyclerViewEquipAlug.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEquipAlug.setHasFixedSize(true);

        equipAlugList = new ArrayList<>();
        filteredEquipAlugList = new ArrayList<>();
        equipAlugAdapter = new EquipAlugAdapter(filteredEquipAlugList, this);
        recyclerViewEquipAlug.setAdapter(equipAlugAdapter);

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
                filtrarEquipAlug(newText);
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
    private void filtrarEquipAlug(String query) {
        filteredEquipAlugList.clear();

        for (EquipamentoAluguel equipAlug : equipAlugList) {
            if (equipAlug.getNome().toLowerCase().contains(query.toLowerCase())) {
                filteredEquipAlugList.add(equipAlug);
            }
        }
        equipAlugAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void buscarDados() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            Log.d("ListarEquipAlug", "Email do usuário logado: " + email);

            FirebaseFirestore.getInstance().collection("EquipamentoAluguel")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                equipAlugList.clear();
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    EquipamentoAluguel equipAlug = document.toObject(EquipamentoAluguel.class);
                                    equipAlug.setId(document.getId());
                                    equipAlugList.add(equipAlug);
                                }
                                filteredEquipAlugList.addAll(equipAlugList);
                                equipAlugAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("ListarEquipAlug", "Consulta retornou nulo.");
                            }
                        } else {
                            Log.e("ListarEquipAlug", "Erro ao buscar dados", task.getException());
                        }
                    });
        } else {
            Log.e("ListarEquipAlug", "Nenhum usuário logado");
        }
    }

    public void showConfirmationDialog(int position, EquipamentoAluguel equiAlug) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmação");
        builder.setMessage("Você tem certeza que deseja deletar este item?");

        builder.setPositiveButton("Sim", (dialog, which) -> deleteItem(position, equiAlug));

        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem(int position, EquipamentoAluguel equipAlug) {
        FirebaseFirestore.getInstance().collection("EquipamentoAluguel")
                .document(equipAlug.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    equipAlugList.remove(position);
                    filteredEquipAlugList.remove(position);
                    equipAlugAdapter.notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> Log.e("ListarEquipAlug", "Erro ao deletar item", e));
    }

}