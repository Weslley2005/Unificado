package br.unigran.tcc.ViewModel;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.unigran.tcc.R;

public class FaturamentoMensal extends AppCompatActivity {
    private static final String TAG = "FaturamentoMensal";
    private TextView textFaturamentoTotal, textDesconto, textLucroTotal;
    private Button btnCalcularFaturamento;
    private RecyclerView recyclerViewProdutos;
    private FaturamentoMensalAdapter produtosAdapter;
    private FaturamentoMensalImpl faturamentoMensal;
    private TextView textViewData;
    private ImageView imageViewCalendario;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faturamento_mensal);

        textFaturamentoTotal = findViewById(R.id.text_faturamento_total);
        textDesconto = findViewById(R.id.text_desconto);
        textLucroTotal = findViewById(R.id.text_lucro_total);
        btnCalcularFaturamento = findViewById(R.id.btn_calcular_faturamento);
        recyclerViewProdutos = findViewById(R.id.recycler_view_produtos);

        faturamentoMensal = new FaturamentoMensalImpl();

        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));
        produtosAdapter = new FaturamentoMensalAdapter(new ArrayList<>());
        recyclerViewProdutos.setAdapter(produtosAdapter);

        btnCalcularFaturamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularFaturamento();
            }
        });

        textViewData = findViewById(R.id.textViewData);
        imageViewCalendario = findViewById(R.id.imageViewCalendario);
        calendar = Calendar.getInstance();

        imageViewCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSeletorDeMes();
            }
        });
    }

    private void mostrarSeletorDeMes() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        atualizarMesSelecionado();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void atualizarMesSelecionado() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        textViewData.setText(sdf.format(calendar.getTime()));
    }

    private void calcularFaturamento() {
        int mesSelecionado = calendar.get(Calendar.MONTH);
        int anoSelecionado = calendar.get(Calendar.YEAR);

        Calendar dataInicio = Calendar.getInstance();
        dataInicio.set(anoSelecionado, mesSelecionado, 1, 0, 0, 0);

        Calendar dataFim = Calendar.getInstance();
        dataFim.set(anoSelecionado, mesSelecionado, dataFim.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59); // Último dia do mês, 23:59:59

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dataInicioStr = sdf.format(dataInicio.getTime());
        String dataFimStr = sdf.format(dataFim.getTime());

        Log.d("FaturamentoMensal", "Calculando faturamento de " + dataInicioStr + " até " + dataFimStr);

        faturamentoMensal.calcularFaturamentoMensal(dataInicioStr, dataFimStr, new FaturamentoMensalImpl.FaturamentoListener() {
            @Override
            public void onFaturamentoCalculado(Map<String, FaturamentoMensalImpl.ProdutoFaturamento> produtosMap, double totalDescontos, double totalLucro) {
                if (produtosMap == null || produtosMap.isEmpty()) {
                    Toast.makeText(FaturamentoMensal.this, "Nenhum produto encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double totalFaturamento = 0;
                List<FaturamentoMensalImpl.ProdutoFaturamento> produtos = new ArrayList<>(produtosMap.values());
                for (FaturamentoMensalImpl.ProdutoFaturamento produto : produtos) {
                    totalFaturamento += produto.getTotalPrecoVenda();
                }

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                textFaturamentoTotal.setText("Faturamento Total: " + currencyFormat.format(totalFaturamento));
                textDesconto.setText("Desconto Total: " + currencyFormat.format(totalDescontos));
                textLucroTotal.setText("Lucro Total: " + currencyFormat.format(totalLucro));

                produtosAdapter.updateProdutos(produtos);
            }

            @Override
            public void onErro(Exception e) {
                Log.e("FaturamentoMensal", "Erro ao calcular faturamento", e);
                Toast.makeText(FaturamentoMensal.this, "Erro ao calcular faturamento.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
