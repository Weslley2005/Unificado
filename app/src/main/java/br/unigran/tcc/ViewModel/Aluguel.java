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

import br.unigran.tcc.Model.EquipamentoAluguel;
import br.unigran.tcc.R;

public class Aluguel extends AppCompatActivity {

    private AluguelAdapter equipAlugAdapter;
    private List<EquipamentoAluguel> equipAlugList;
    private List<EquipamentoAluguel> filteredEquipAlugList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluguel);

        RecyclerView recyclerViewEquipAlug = findViewById(R.id.recyclerViewAluguel);
        recyclerViewEquipAlug.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEquipAlug.setHasFixedSize(true);

        equipAlugList = new ArrayList<>();
        filteredEquipAlugList = new ArrayList<>();
        equipAlugAdapter = new AluguelAdapter(filteredEquipAlugList, this);
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
                                    equipAlug.setId(document.getId()); //
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
}