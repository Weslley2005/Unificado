package br.unigran.tcc.ViewModel;

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

public class FaturamentoMensal extends AppCompatActivity {
    private static final String TAG = "FaturamentoMensal";
    private TextView textFaturamentoTotal, textDesconto, textLucroTotal;
    private TextView textFaturamentoTotalA, textDescontoA, textLucroTotalA;
    private Button btnCalcularFaturamento;
    private RecyclerView recyclerViewProdutos, recyclerViewAlugueis;
    private FaturamentoMensalAdapter produtosAdapter;
    private FaturamentoMensalAlugueisAdapter alugueisAdapter;
    private FaturamentoMensalImpl faturamentoMensal;
    private FaturamentoMensalAlugueisImpl faturamentoMensalAlugueis;
    private TextView textViewData;
    private ImageView imageViewCalendario;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faturamento_mensal);

        // Inicializa as views
        textFaturamentoTotal = findViewById(R.id.text_faturamento_total);
        textDesconto = findViewById(R.id.text_desconto);
        textLucroTotal = findViewById(R.id.text_lucro_total);
        textFaturamentoTotalA = findViewById(R.id.text_faturamento_totalA);
        textDescontoA = findViewById(R.id.text_descontoA);
        textLucroTotalA = findViewById(R.id.text_lucro_totalA);
        btnCalcularFaturamento = findViewById(R.id.btn_calcular_faturamento);
        recyclerViewProdutos = findViewById(R.id.recycler_view_produtos);
        recyclerViewAlugueis = findViewById(R.id.recycler_view_alugueis);

        // Configura os adapters
        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));
        produtosAdapter = new FaturamentoMensalAdapter(new ArrayList<>());
        recyclerViewProdutos.setAdapter(produtosAdapter);

        recyclerViewAlugueis.setLayoutManager(new LinearLayoutManager(this));
        alugueisAdapter = new FaturamentoMensalAlugueisAdapter(new ArrayList<>());
        recyclerViewAlugueis.setAdapter(alugueisAdapter);

        // Instancia os cálculos de faturamento
        faturamentoMensal = new FaturamentoMensalImpl();
        faturamentoMensalAlugueis = new FaturamentoMensalAlugueisImpl();

        // Ação do botão para calcular o faturamento
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
                mostrarSeletorDeMes();
            }
        });

        Window janela = getWindow();
        janela.setStatusBarColor(getResources().getColor(android.R.color.black));
        janela.setNavigationBarColor(getResources().getColor(android.R.color.black));
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
        // Obtem o mês e ano selecionado
        int mesSelecionado = calendar.get(Calendar.MONTH);
        int anoSelecionado = calendar.get(Calendar.YEAR);

        Calendar dataInicio = Calendar.getInstance();
        dataInicio.set(anoSelecionado, mesSelecionado, 1, 0, 0, 0);

        Calendar dataFim = Calendar.getInstance();
        dataFim.set(anoSelecionado, mesSelecionado, dataFim.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dataInicioStr = sdf.format(dataInicio.getTime());
        String dataFimStr = sdf.format(dataFim.getTime());

        Log.d(TAG, "Calculando faturamento de " + dataInicioStr + " até " + dataFimStr);

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
                Log.e(TAG, "Erro ao calcular faturamento", e);
                Toast.makeText(FaturamentoMensal.this, "Erro ao calcular faturamento.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calcularFaturamentoAlugueis() {
        int mesSelecionado = calendar.get(Calendar.MONTH);
        int anoSelecionado = calendar.get(Calendar.YEAR);

        Calendar dataInicio = Calendar.getInstance();
        dataInicio.set(anoSelecionado, mesSelecionado, 1, 0, 0, 0);

        Calendar dataFim = Calendar.getInstance();
        dataFim.set(anoSelecionado, mesSelecionado, dataFim.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dataInicioStr = sdf.format(dataInicio.getTime());
        String dataFimStr = sdf.format(dataFim.getTime());

        Log.d(TAG, "Calculando faturamento de alugueis de " + dataInicioStr + " até " + dataFimStr);

        faturamentoMensalAlugueis.calcularFaturamentoMensalAlugueis(dataInicioStr, dataFimStr, new FaturamentoMensalAlugueisImpl.FaturamentoListener() {
            @Override
            public void onFaturamentoCalculado(Map<String, FaturamentoMensalAlugueisImpl.ProdutoFaturamento> produtosMap, double totalDescontos, double totalLucro) {
                if (produtosMap == null || produtosMap.isEmpty()) {
                    Toast.makeText(FaturamentoMensal.this, "Nenhum produto encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double totalFaturamento = 0;
                List<FaturamentoMensalAlugueisImpl.ProdutoFaturamento> produtos = new ArrayList<>(produtosMap.values());
                for (FaturamentoMensalAlugueisImpl.ProdutoFaturamento produto : produtos) {
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
                Log.e(TAG, "Erro ao calcular faturamento de alugueis", e);
                Toast.makeText(FaturamentoMensal.this, "Erro ao calcular faturamento de alugueis.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
