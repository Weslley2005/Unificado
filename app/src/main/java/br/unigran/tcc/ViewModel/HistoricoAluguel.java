package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.unigran.tcc.Model.FinalizaAlugueis;
import br.unigran.tcc.R;

public class HistoricoAluguel extends AppCompatActivity {
    private RecyclerView recyclerViewHistoricoAlugueis;
    private HistoricoAluguelAdapter aluguelAdapter;
    private List<FinalizaAlugueis> listaAlugueis;
    private TextView textViewData;
    private ImageView imageViewCalendario;
    private List<FinalizaAlugueis> listaItensAlugueisFiltrados;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_aluguel);

        setSupportActionBar(findViewById(R.id.toolbar));

        SearchView searchView = findViewById(R.id.searchView);
        recyclerViewHistoricoAlugueis = findViewById(R.id.recycleViewHistoricoAlugueis);
        listaAlugueis = new ArrayList<>();
        listaItensAlugueisFiltrados = new ArrayList<>();
        aluguelAdapter = new HistoricoAluguelAdapter(listaAlugueis, this);

        recyclerViewHistoricoAlugueis.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistoricoAlugueis.setAdapter(aluguelAdapter);

        textViewData = findViewById(R.id.textViewData);
        imageViewCalendario = findViewById(R.id.imageViewCalendario);

        imageViewCalendario.setOnClickListener(v -> showDatePickerDialog());
        textViewData.setOnClickListener(v -> showDatePickerDialog());

        carregarAlugueis();

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));
        window.setNavigationBarColor(getResources().getColor(android.R.color.black));

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

    private void filtrarItens(String texto) {
        listaAlugueis.clear();
        if (texto.isEmpty()) {
            listaAlugueis.addAll(listaItensAlugueisFiltrados);
        } else {
            String filtro = texto.toLowerCase();
            for (FinalizaAlugueis item : listaItensAlugueisFiltrados) {
                if (item.getIdNomenAluguel().toLowerCase().contains(filtro)) {
                    listaAlugueis.add(item);
                }
            }
        }
        aluguelAdapter.notifyDataSetChanged();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    textViewData.setText(selectedDate);
                    filtrarVendasPorData(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void filtrarVendasPorData(String dataStr) {
        if (dataStr.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione uma data.", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dataFiltro;

        try {
            dataFiltro = sdf.parse(dataStr);
            carregarVendasPorData(dataFiltro);
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de data inválido!", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarVendasPorData(Date dataFiltro) {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();
            FirebaseFirestore.getInstance().collection("AlugFinaliz")
                    .whereEqualTo("usuarioId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            listaAlugueis.clear();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            for (QueryDocumentSnapshot documento : task.getResult()) {
                                FinalizaAlugueis aluguel = documento.toObject(FinalizaAlugueis.class);
                                aluguel.setId(documento.getId());

                                try {
                                    Date dataVenda = sdf.parse(aluguel.getData());
                                    if (dataVenda != null && dataVenda.equals(dataFiltro)) {
                                        listaAlugueis.add(aluguel);
                                    }
                                } catch (ParseException e) {
                                    Log.e("HistoricoVendas", "Erro ao comparar datas", e);
                                }
                            }
                            aluguelAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("HistoricoVendas", "Erro ao carregar vendas", task.getException());
                        }
                    });
        } else {
            Toast.makeText(this, "Você precisa estar logado para visualizar suas vendas.", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarAlugueis() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();
            FirebaseFirestore.getInstance().collection("AlugFinaliz")
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
                            listaItensAlugueisFiltrados.addAll(listaAlugueis); // Adiciona todos os aluguéis filtrados
                            aluguelAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("FinalizarAluguelActivity", "Erro ao carregar aluguéis", task.getException());
                        }
                    });
        } else {
            Toast.makeText(this, "Você precisa estar logado para visualizar seus aluguéis.", Toast.LENGTH_SHORT).show();
        }
    }
}
