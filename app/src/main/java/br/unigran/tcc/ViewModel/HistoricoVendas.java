package br.unigran.tcc.ViewModel;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import br.unigran.tcc.Model.FinalizaVendas;
import br.unigran.tcc.R;

public class HistoricoVendas extends AppCompatActivity {

    private RecyclerView recyclerViewHistoricoVendas;
    private HistoricoVendasAdapter historicoVendasAdapter;
    private List<FinalizaVendas> listaVendas;
    private TextView textViewData;
    private ImageView imageViewCalendario;
    private List<FinalizaVendas> listaItensVendasFiltrados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_vendas);

        setSupportActionBar(findViewById(R.id.toolbar));

        SearchView searchView = findViewById(R.id.searchView);
        recyclerViewHistoricoVendas = findViewById(R.id.recycleViewHistoricoVendas);
        listaVendas = new ArrayList<>();
        listaItensVendasFiltrados = new ArrayList<>();
        historicoVendasAdapter = new HistoricoVendasAdapter(listaVendas, this);

        recyclerViewHistoricoVendas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistoricoVendas.setAdapter(historicoVendasAdapter);

        textViewData = findViewById(R.id.textViewData);
        imageViewCalendario = findViewById(R.id.imageViewCalendario);

        imageViewCalendario.setOnClickListener(v -> showDatePickerDialog());
        textViewData.setOnClickListener(v -> showDatePickerDialog());

        carregarVendas();

        // Set colors for status bar and navigation bar
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));

        // Customize SearchView text colors
        customizeSearchView(searchView);
    }

    private void customizeSearchView(SearchView searchView) {
        int textColor = Color.WHITE;
        int hintColor = Color.WHITE;

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(textColor);
            searchEditText.setHintTextColor(hintColor);
        }

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
    }

    private void filtrarItens(String texto) {
        listaVendas.clear();
        if (texto.isEmpty()) {
            listaVendas.addAll(listaItensVendasFiltrados);
        } else {
            String filtro = texto.toLowerCase();
            for (FinalizaVendas item : listaItensVendasFiltrados) {
                if (item.getidNomenAluguel().toLowerCase().contains(filtro)) {
                    listaVendas.add(item);
                }
            }
        }
        historicoVendasAdapter.notifyDataSetChanged();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
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
        try {
            Date dataFiltro = sdf.parse(dataStr);
            carregarVendasPorData(dataFiltro);
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de data inválido!", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarVendasPorData(Date dataFiltro) {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();
            FirebaseFirestore.getInstance().collection("VendasFinaliz")
                    .whereEqualTo("usuarioId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            listaVendas.clear();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            for (QueryDocumentSnapshot documento : task.getResult()) {
                                FinalizaVendas vendas = documento.toObject(FinalizaVendas.class);
                                vendas.setId(documento.getId());

                                try {
                                    Date dataVenda = sdf.parse(vendas.getData());
                                    if (dataVenda != null && dataVenda.equals(dataFiltro)) {
                                        listaVendas.add(vendas);
                                    }
                                } catch (ParseException e) {
                                    Log.e("HistoricoVendas", "Erro ao comparar datas", e);
                                }
                            }
                            historicoVendasAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("HistoricoVendas", "Erro ao carregar vendas", task.getException());
                        }
                    });
        } else {
            Toast.makeText(this, "Você precisa estar logado para visualizar suas vendas.", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarVendas() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();
            FirebaseFirestore.getInstance().collection("VendasFinaliz")
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
                            listaItensVendasFiltrados.addAll(listaVendas); // Keep a copy for filtering
                            historicoVendasAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("HistoricoVendas", "Erro ao carregar vendas", task.getException());
                        }
                    });
        } else {
            Toast.makeText(this, "Você precisa estar logado para visualizar suas vendas.", Toast.LENGTH_SHORT).show();
        }
    }
}
