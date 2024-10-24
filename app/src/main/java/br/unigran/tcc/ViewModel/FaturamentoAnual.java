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

public class FaturamentoAnual extends AppCompatActivity {
    private TextView textFaturamentoTotal, textDesconto, textLucroTotal;
    private TextView textFaturamentoTotalA, textDescontoA, textLucroTotalA;
    private Button btnCalcularFaturamento;
    private RecyclerView recyclerViewProdutos, recyclerViewAlugueis;
    private FaturamentoAnualAdapter produtosAdapter;
    private FaturamentoAnualAlugueisAdapter alugueisAdapter;
    private FaturamentoAnualImpl faturamentoAnual;
    private FaturamentoAnualAlugueisImpl faturamentoAnualAlugueis;
    private TextView textViewData;
    private ImageView imageViewCalendario;
    private Calendar calendar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faturamento_anual);

        textFaturamentoTotal = findViewById(R.id.text_faturamento_total);
        textDesconto = findViewById(R.id.text_desconto);
        textLucroTotal = findViewById(R.id.text_lucro_total);
        textFaturamentoTotalA = findViewById(R.id.text_faturamento_totalA);
        textDescontoA = findViewById(R.id.text_descontoA);
        textLucroTotalA = findViewById(R.id.text_lucro_totalA);
        btnCalcularFaturamento = findViewById(R.id.btn_calcular_faturamento);
        recyclerViewProdutos = findViewById(R.id.recycler_view_produtos);
        recyclerViewAlugueis = findViewById(R.id.recycler_view_alugueis);  // Corrigido aqui

        faturamentoAnual = new FaturamentoAnualImpl();
        faturamentoAnualAlugueis = new FaturamentoAnualAlugueisImpl();

        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));
        produtosAdapter = new FaturamentoAnualAdapter(new ArrayList<>());
        recyclerViewProdutos.setAdapter(produtosAdapter);

        recyclerViewAlugueis.setLayoutManager(new LinearLayoutManager(this));
        alugueisAdapter = new FaturamentoAnualAlugueisAdapter(new ArrayList<>());
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
                mostrarSeletorDeAno();
            }
        });

        Window janela = getWindow();
        janela.setStatusBarColor(getResources().getColor(android.R.color.black));
        janela.setNavigationBarColor(getResources().getColor(android.R.color.black));
    }

    private void mostrarSeletorDeAno() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, Calendar.JANUARY);
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        atualizarAnoSelecionado();
                    }
                },
                calendar.get(Calendar.YEAR), 0, 1);
        datePickerDialog.show();
    }

    private void atualizarAnoSelecionado() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        textViewData.setText(sdf.format(calendar.getTime()));
    }

    private void calcularFaturamento() {
        int anoSelecionado = calendar.get(Calendar.YEAR);

        Calendar dataInicio = Calendar.getInstance();
        dataInicio.set(anoSelecionado, Calendar.JANUARY, 1, 0, 0, 0);

        Calendar dataFim = Calendar.getInstance();
        dataFim.set(anoSelecionado, Calendar.DECEMBER, 31, 23, 59, 59);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dataInicioStr = sdf.format(dataInicio.getTime());
        String dataFimStr = sdf.format(dataFim.getTime());

        Log.d("FaturamentoAnual", "Calculando faturamento de " + dataInicioStr + " até " + dataFimStr);

        faturamentoAnual.calcularFaturamentoAnual(dataInicioStr, dataFimStr, new FaturamentoAnualImpl.FaturamentoListener() {
            @Override
            public void onFaturamentoCalculado(Map<String, FaturamentoAnualImpl.ProdutoFaturamento> produtosMap, double totalDescontos, double totalLucro) {
                if (produtosMap == null || produtosMap.isEmpty()) {
                    Toast.makeText(FaturamentoAnual.this, "Nenhum produto encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double totalFaturamento = 0;
                List<FaturamentoAnualImpl.ProdutoFaturamento> produtos = new ArrayList<>(produtosMap.values());
                for (FaturamentoAnualImpl.ProdutoFaturamento produto : produtos) {
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
                Toast.makeText(FaturamentoAnual.this, "Erro ao calcular faturamento.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calcularFaturamentoAlugueis() {
        int anoSelecionado = calendar.get(Calendar.YEAR);

        Calendar dataInicio = Calendar.getInstance();
        dataInicio.set(anoSelecionado, Calendar.JANUARY, 1, 0, 0, 0);

        Calendar dataFim = Calendar.getInstance();
        dataFim.set(anoSelecionado, Calendar.DECEMBER, 31, 23, 59, 59);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dataInicioStr = sdf.format(dataInicio.getTime());
        String dataFimStr = sdf.format(dataFim.getTime());

        Log.d("FaturamentoAnual", "Calculando faturamento de " + dataInicioStr + " até " + dataFimStr);

        faturamentoAnualAlugueis.calcularFaturamentoAnualAlugueis(dataInicioStr, dataFimStr, new FaturamentoAnualAlugueisImpl.FaturamentoListener() {
            @Override
            public void onFaturamentoCalculado(Map<String, FaturamentoAnualAlugueisImpl.ProdutoFaturamento> produtosMap, double totalDescontos, double totalLucro) {
                if (produtosMap == null || produtosMap.isEmpty()) {
                    Toast.makeText(FaturamentoAnual.this, "Nenhum produto encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double totalFaturamento = 0;
                List<FaturamentoAnualAlugueisImpl.ProdutoFaturamento> produtos = new ArrayList<>(produtosMap.values());
                for (FaturamentoAnualAlugueisImpl.ProdutoFaturamento produto : produtos) {
                    totalFaturamento += produto.getTotalPrecoVenda();
                }

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                textFaturamentoTotalA.setText("Faturamento Total: " + currencyFormat.format(totalFaturamento));
                textDescontoA.setText("Desconto Total: " + currencyFormat.format(totalDescontos));
                textLucroTotalA.setText("Lucro Total: " + currencyFormat.format(totalLucro));
                alugueisAdapter.updateProdutos(produtos);
            }

            @Override
            public void onErro(Exception e) {
                Log.e("FaturamentoMensal", "Erro ao calcular faturamento", e);
                Toast.makeText(FaturamentoAnual.this, "Erro ao calcular faturamento.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
