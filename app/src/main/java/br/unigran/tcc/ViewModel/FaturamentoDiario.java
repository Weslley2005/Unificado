package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

public class FaturamentoDiario extends AppCompatActivity {
    private TextView textFaturamentoTotal, textDesconto, textLucroTotal;
    private TextView textFaturamentoTotalA, textDescontoA, textLucroTotalA;
    private Button btnCalcularFaturamento;
    private RecyclerView recyclerViewProdutos;
    private FaturamentoDiarioAdapter produtosAdapter;
    private FaturamentoDiarioAluguelAdapter alugueisAdapter;
    private FaturamentoDiarioImpl faturamentoDiario;
    private FaturamentoDiarioAlugueisImpl faturamentoDiarioAlugueis;
    private TextView textViewData;
    private ImageView imageViewCalendario;
    private Calendar calendar;
    private RecyclerView recyclerViewAlugueis;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faturamento_diario);

        textFaturamentoTotal = findViewById(R.id.text_faturamento_total);
        textDesconto = findViewById(R.id.text_desconto);
        textLucroTotal = findViewById(R.id.text_lucro_total);
        textFaturamentoTotalA = findViewById(R.id.text_faturamento_totalA);
        textDescontoA = findViewById(R.id.text_descontoA);
        textLucroTotalA = findViewById(R.id.text_lucro_totalA);
        btnCalcularFaturamento = findViewById(R.id.btn_calcular_faturamento);
        recyclerViewProdutos = findViewById(R.id.recycler_view_produtos);

        faturamentoDiario = new FaturamentoDiarioImpl();
        faturamentoDiarioAlugueis = new FaturamentoDiarioAlugueisImpl();

        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));
        produtosAdapter = new FaturamentoDiarioAdapter(new ArrayList<>());
        recyclerViewProdutos.setAdapter(produtosAdapter);

        recyclerViewAlugueis = findViewById(R.id.recycler_view_alugueis);
        recyclerViewAlugueis.setLayoutManager(new LinearLayoutManager(this));
        alugueisAdapter = new FaturamentoDiarioAluguelAdapter(new ArrayList<>());
        recyclerViewAlugueis.setAdapter(alugueisAdapter);

        btnCalcularFaturamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alugueisAdapter.limparDados();
                produtosAdapter.limparDados();

                textFaturamentoTotal.setText("Faturamento Total:R$ 0.00");
                textDesconto.setText("Desconto Total:R$ 0.00");
                textLucroTotal.setText("Lucro Total:R$ 0.00");
                textFaturamentoTotalA.setText("Faturamento Total:R$ 0.00");
                textDescontoA.setText("Desconto Total:R$ 0.00");
                textLucroTotalA.setText("Lucro Total:R$ 0.00");

                calcularFaturamento();
                calcularFaturamentoAlugueis();
            }
        });


        textViewData = findViewById(R.id.textViewData);
        imageViewCalendario = findViewById(R.id.imageViewCalendario);
        calendar = Calendar.getInstance();

        imageViewCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSeletorDeData();
            }
        });

        Window janela = getWindow();
        janela.setStatusBarColor(getResources().getColor(android.R.color.black));
        janela.setNavigationBarColor(getResources().getColor(android.R.color.black));
    }

    private void mostrarSeletorDeData() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        atualizarDataNoTextView();
                    }
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void atualizarDataNoTextView() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        textViewData.setText(sdf.format(calendar.getTime()));
    }


    private void calcularFaturamento() {
        String dataSelecionada = textViewData.getText().toString();
        faturamentoDiario.calcularFaturamentoDiario(dataSelecionada, new FaturamentoDiarioImpl.FaturamentoListener() {
            @Override
            public void onFaturamentoCalculado(Map<String, FaturamentoDiarioImpl.ProdutoFaturamento> produtosMap, double totalDescontos, double totalLucro) {
                if (produtosMap == null || produtosMap.isEmpty()) {
                    Toast.makeText(FaturamentoDiario.this, "Nenhum produto encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double totalFaturamento = 0;
                List<FaturamentoDiarioImpl.ProdutoFaturamento> produtos = new ArrayList<>(produtosMap.values());
                for (FaturamentoDiarioImpl.ProdutoFaturamento produto : produtos) {
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
                Log.e("FaturamentoDiario", "Erro ao calcular faturamento", e);
                Toast.makeText(FaturamentoDiario.this, "Erro ao calcular faturamento.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calcularFaturamentoAlugueis() {
        String dataSelecionada = textViewData.getText().toString();
        faturamentoDiarioAlugueis.calcularFaturamentoAlugueis(dataSelecionada, new FaturamentoDiarioAlugueisImpl.FaturamentoListener() {
            @Override
            public void onFaturamentoCalculado(Map<String, FaturamentoDiarioAlugueisImpl.ProdutoFaturamento> produtosMap, double totalDescontos, double totalLucro) {
                if (produtosMap == null || produtosMap.isEmpty()) {
                    Toast.makeText(FaturamentoDiario.this, "Nenhum produto encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double totalFaturamento = 0;
                List<FaturamentoDiarioAlugueisImpl.ProdutoFaturamento> produtos = new ArrayList<>(produtosMap.values());
                for (FaturamentoDiarioAlugueisImpl.ProdutoFaturamento produto : produtos) {
                    totalFaturamento += produto.getTotalPrecoVenda();
                }

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                textFaturamentoTotalA.setText("Faturamento Total: " + currencyFormat.format(totalFaturamento));
                textDescontoA.setText("Desconto Total: " + currencyFormat.format(totalDescontos));
                textLucroTotalA.setText("Lucro Total: " + currencyFormat.format(totalLucro));

                alugueisAdapter.updateProdutos(produtos);
                alugueisAdapter.notifyDataSetChanged();
            }

            @Override
            public void onErro(Exception e) {
                Log.e("FaturamentoDiario", "Erro ao calcular faturamento", e);
                Toast.makeText(FaturamentoDiario.this, "Erro ao calcular faturamento.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
