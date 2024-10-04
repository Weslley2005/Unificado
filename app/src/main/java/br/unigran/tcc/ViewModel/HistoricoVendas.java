package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

import br.unigran.tcc.Model.FinalizarVendas;
import br.unigran.tcc.R;

public class HistoricoVendas extends AppCompatActivity {

    private RecyclerView recyclerViewHistoricoVendas;
    private HistoricoVendasAdapter historicoVendasAdapter; // Corrigido aqui
    private List<FinalizarVendas> listaVendas;
    private TextView textViewData; // TextView para mostrar a data selecionada
    private ImageView imageViewCalendario; // ImageView como botão para abrir o calendário

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_vendas);

        recyclerViewHistoricoVendas = findViewById(R.id.recycleViewHistoricoVendas);
        listaVendas = new ArrayList<>();
        historicoVendasAdapter = new HistoricoVendasAdapter(listaVendas, this);

        recyclerViewHistoricoVendas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistoricoVendas.setAdapter(historicoVendasAdapter);

        textViewData = findViewById(R.id.textViewData); // Inicializando TextView
        imageViewCalendario = findViewById(R.id.imageViewCalendario); // Inicializando ImageView

        imageViewCalendario.setOnClickListener(v -> showDatePickerDialog());

        textViewData.setOnClickListener(v -> showDatePickerDialog());

        carregarVendas(); // Mudado para carregarVendas
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
                    filtrarVendasPorData(selectedDate); // Chamar método para filtrar imediatamente após a seleção
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
            FirebaseFirestore.getInstance().collection("Compras")
                    .whereEqualTo("usuarioId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            listaVendas.clear();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            for (QueryDocumentSnapshot documento : task.getResult()) {
                                FinalizarVendas vendas = documento.toObject(FinalizarVendas.class);
                                vendas.setId(documento.getId());

                                try {
                                    Date dataVenda = sdf.parse(vendas.getData()); // Assegure-se de que a classe FinalizarVendas possui o método getData
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



    private void carregarVendas() { // Mudado para carregarVendas
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
                                FinalizarVendas vendas = documento.toObject(FinalizarVendas.class); // Assegure-se de que esta classe está correta
                                vendas.setId(documento.getId()); // Certifique-se de que a classe FinalizarVendas tem um método setId
                                listaVendas.add(vendas);
                            }
                            historicoVendasAdapter.notifyDataSetChanged(); // Corrigido para chamar no adapter
                        } else {
                            Log.e("HistoricoVendas", "Erro ao carregar vendas", task.getException()); // Mudada a mensagem de log
                        }
                    });
        } else {
            Toast.makeText(this, "Você precisa estar logado para visualizar suas vendas.", Toast.LENGTH_SHORT).show();
        }
    }
}
