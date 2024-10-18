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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class FaturamentoDiarioImpl {
    private FirebaseFirestore db;
    private CollectionReference comprasRef;

    public FaturamentoDiarioImpl() {
        db = FirebaseFirestore.getInstance();
        comprasRef = db.collection("Compras");
    }

    public void calcularFaturamentoDiario(String data, final FaturamentoListener listener) {
        Query query = comprasRef.whereEqualTo("data", data);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, ProdutoFaturamento> produtosMap = new HashMap<>();
                    final double[] totalDescontos = {0};
                    final double[] totalLucro = {0};

                    for (DocumentSnapshot document : task.getResult()) {
                        CollectionReference itensRef = document.getReference().collection("Itens");
                        itensRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot itemDoc : task.getResult()) {
                                        ItemFaturamento item = itemDoc.toObject(ItemFaturamento.class);
                                        if (item != null) {
                                            String nomeProduto = item.getNome();
                                            double precoCompra = item.getPrecoCompra();
                                            double precoVenda = item.getPrecoUnitario();
                                            int quantidade = item.getQuantidade();

                                            Log.d("FaturamentoDiario", "Produto: " + nomeProduto + ", Preço de Venda: " + precoVenda + ", Quantidade: " + quantidade);

                                            if (!produtosMap.containsKey(nomeProduto)) {
                                                produtosMap.put(nomeProduto, new ProdutoFaturamento(nomeProduto));
                                            }

                                            ProdutoFaturamento produto = produtosMap.get(nomeProduto);
                                            produto.adicionarQuantidade(quantidade);
                                            produto.adicionarPrecoCompra(precoCompra * quantidade);
                                            produto.adicionarPrecoVenda(precoVenda * quantidade);

                                            totalLucro[0] += (precoVenda - precoCompra) * quantidade;
                                        }
                                    }

                                    Double desconto = document.getDouble("desconto");
                                    if (desconto != null) totalDescontos[0] += desconto;

                                    listener.onFaturamentoCalculado(produtosMap, totalDescontos[0], totalLucro[0]);
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
