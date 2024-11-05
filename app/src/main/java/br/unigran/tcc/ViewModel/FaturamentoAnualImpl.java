package br.unigran.tcc.ViewModel;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FaturamentoAnualImpl {
    private FirebaseFirestore db;
    private CollectionReference comprasRef;

    public FaturamentoAnualImpl() {
        db = FirebaseFirestore.getInstance();
        comprasRef = db.collection("VendasFinaliz");
    }
    public Date converterStringParaData(String dataString) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return formato.parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("RestrictedApi")
    public void calcularFaturamentoAnual(String dataInicio, String dataFim, final FaturamentoListener listener) {
        Log.d(TAG, "Convertendo data de inicio e fim: " + dataInicio + " até " + dataFim);

        comprasRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Consulta realizada com sucesso, documentos retornados: " + task.getResult().size());

                    Map<String, ProdutoFaturamento> produtosMap = new HashMap<>();
                    final double[] totalDescontos = {0};
                    final double[] totalLucro = {0};

                    Date dataInicioDate = converterStringParaData(dataInicio);
                    Date dataFimDate = converterStringParaData(dataFim);

                    for (DocumentSnapshot document : task.getResult()) {
                        String dataString = document.getString("data");
                        Date dataDocumento = converterStringParaData(dataString);

                        if (dataDocumento != null && (dataDocumento.compareTo(dataInicioDate) >= 0) && (dataDocumento.compareTo(dataFimDate) <= 0)) {
                            Log.d(TAG, "Documento encontrado dentro do intervalo: " + document.getId());

                            CollectionReference itensRef = document.getReference().collection("Itens");
                            itensRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Itens encontrados na subcoleção: " + task.getResult().size());

                                        for (DocumentSnapshot itemDoc : task.getResult()) {
                                            ItemFaturamento item = itemDoc.toObject(ItemFaturamento.class);
                                            if (item != null) {
                                                String nomeProduto = item.getNome();
                                                double precoCompra = item.getPrecoCompra();
                                                double precoVenda = item.getPrecoUnitario();
                                                int quantidade = item.getQuantidade();

                                                if (!produtosMap.containsKey(nomeProduto)) {
                                                    produtosMap.put(nomeProduto, new ProdutoFaturamento(nomeProduto));
                                                }

                                                ProdutoFaturamento produto = produtosMap.get(nomeProduto);
                                                produto.adicionarQuantidade(quantidade);
                                                produto.adicionarPrecoCompra(precoCompra * quantidade);
                                                produto.adicionarPrecoVenda(precoVenda * quantidade);

                                                Double desconto = document.getDouble("desconto");
                                                if (desconto != null) totalDescontos[0] += desconto;

                                                totalLucro[0] += ((precoVenda - precoCompra) * quantidade) - desconto;
                                            }
                                        }



                                        listener.onFaturamentoCalculado(produtosMap, totalDescontos[0], totalLucro[0]);
                                    } else {
                                        Log.e(TAG, "Erro ao obter itens da subcoleção 'Itens'", task.getException());
                                        listener.onErro(task.getException());
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Log.e(TAG, "Erro na consulta do Firestore", task.getException());
                    listener.onErro(task.getException());
                }
            }
        });
    }



    public interface FaturamentoListener {
        void onFaturamentoCalculado(Map<String, ProdutoFaturamento> produtosMap, double totalDescontos, double totalLucro);
        void onErro(Exception e);
    }

    public static class ProdutoFaturamento {
        private String nome;
        private int quantidade;
        private double totalPrecoCompra;
        private double totalPrecoVenda;

        public ProdutoFaturamento(String nome) {
            this.nome = nome;
            this.quantidade = 0;
            this.totalPrecoCompra = 0;
            this.totalPrecoVenda = 0;
        }

        public void adicionarQuantidade(int quantidade) {
            this.quantidade += quantidade;
        }

        public void adicionarPrecoCompra(double precoCompra) {
            this.totalPrecoCompra += precoCompra;
        }

        public void adicionarPrecoVenda(double precoUnitario) {
            this.totalPrecoVenda += precoUnitario;
        }

        public double getPrecoLiquido() {
            return totalPrecoVenda - totalPrecoCompra;
        }

        public String getNome() {
            return nome;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public double getTotalPrecoCompra() {
            return totalPrecoCompra;
        }

        public double getTotalPrecoVenda() {
            return totalPrecoVenda;
        }
    }

    public static class ItemFaturamento {
        private String nome;
        private double precoCompra;
        private double precoUnitario;
        private int quantidade;

        public String getNome() {
            return nome;
        }

        public double getPrecoCompra() {
            return precoCompra;
        }

        public double getPrecoUnitario() {
            return precoUnitario;
        }

        public int getQuantidade() {
            return quantidade;
        }
    }
}
