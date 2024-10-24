package br.unigran.tcc.ViewModel;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FaturamentoDiarioAlugueisImpl {
    private FirebaseFirestore db;
    private CollectionReference comprasRef;

    public FaturamentoDiarioAlugueisImpl() {
        db = FirebaseFirestore.getInstance();
        comprasRef = db.collection("Compras");
    }


    public void calcularFaturamentoAlugueis(String data, final FaturamentoListener listener) {
        CollectionReference alugueisRef = db.collection("AlugFinaliz");
        Query query = alugueisRef.whereEqualTo("data", data);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, ProdutoFaturamento> alugueisMap = new HashMap<>();
                    final double[] totalDescontos = {0};
                    final double[] totalLucro = {0};

                    for (DocumentSnapshot document : task.getResult()) {
                        CollectionReference itensRef = document.getReference().collection("ItensAluguel");
                        itensRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot itemDoc : task.getResult()) {
                                        ItemFaturamento item = itemDoc.toObject(ItemFaturamento.class);
                                        if (item != null) {
                                            String nomeProduto = item.getNome();
                                            double precoCompra = item.getPrecoCompra();
                                            double precoVenda = item.getPrecoTotal();
                                            int quantidade = item.getQuantidade();

                                            Log.d("FaturamentoAlugueis", "Produto: " + nomeProduto + ", Preço de Venda: " + precoVenda + ", Quantidade: " + quantidade);

                                            if (!alugueisMap.containsKey(nomeProduto)) {
                                                alugueisMap.put(nomeProduto, new ProdutoFaturamento(nomeProduto));
                                            }

                                            ProdutoFaturamento produto = alugueisMap.get(nomeProduto);
                                            produto.adicionarQuantidade(quantidade);
                                            produto.adicionarPrecoCompra(precoCompra * quantidade);
                                            produto.adicionarPrecoVenda(precoVenda);
                                            Double desconto = document.getDouble("desconto");
                                            if (desconto != null) totalDescontos[0] += desconto;
                                            totalLucro[0] += (precoVenda-desconto);
                                        }
                                    }



                                    listener.onFaturamentoCalculado(alugueisMap, totalDescontos[0], totalLucro[0]);
                                } else {
                                    listener.onErro(task.getException());
                                }
                            }
                        });
                    }
                } else {
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

        public void adicionarPrecoVenda(double precoTotal) {
            this.totalPrecoVenda += precoTotal;
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
        private double precoTotal;
        private int quantidade;

        public String getNome() {
            return nome;
        }

        public double getPrecoCompra() {
            return precoCompra;
        }

        public double getPrecoTotal() {
            return precoTotal;
        }

        public int getQuantidade() {
            return quantidade;
        }
    }
}
