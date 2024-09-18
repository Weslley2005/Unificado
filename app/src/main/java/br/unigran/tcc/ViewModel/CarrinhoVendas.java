package br.unigran.tcc.ViewModel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unigran.tcc.Model.Produtos;
import br.unigran.tcc.R;

public class CarrinhoVendas extends AppCompatActivity {

    private RecyclerView recyclerViewCarrinho;
    private CarrinhoVendasAdapter carrinhoAdapter;
    private List<Produtos> listaCarrinho;
    private TextView textSubtotal;
    private TextView textTotal;
    private EditText editDesconto;
    private Button buttonFinalizar;
    private double subtotal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho_vendas);

        recyclerViewCarrinho = findViewById(R.id.recyclerViewCarrinho);
        textSubtotal = findViewById(R.id.textSubtotal);
        textTotal = findViewById(R.id.textTotal);
        editDesconto = findViewById(R.id.editDesconto);
        buttonFinalizar = findViewById(R.id.buttonFinalizar);

        listaCarrinho = new ArrayList<>();
        carrinhoAdapter = new CarrinhoVendasAdapter(listaCarrinho);
        recyclerViewCarrinho.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCarrinho.setAdapter(carrinhoAdapter);

        carregarCarrinho();

        buttonFinalizar.setOnClickListener(v -> finalizarCompra());

        // Adiciona um TextWatcher para atualizar o total quando o desconto mudar
        editDesconto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Não é necessário implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarSubtotal();  // Atualiza subtotal e total ao alterar o desconto
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Não é necessário implementar
            }
        });

        Window janela = getWindow();
        janela.setStatusBarColor(getResources().getColor(android.R.color.black));
        janela.setNavigationBarColor(getResources().getColor(android.R.color.black));
    }

    private void carregarCarrinho() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            FirebaseFirestore.getInstance().collection("Carrinho").document(userId)
                    .collection("Itens")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            listaCarrinho.clear();
                            subtotal = 0.0;
                            for (QueryDocumentSnapshot documento : task.getResult()) {
                                Map<String, Object> item = documento.getData();
                                Produtos produto = new Produtos();
                                produto.setNome((String) item.get("nome"));
                                produto.setPrecoVenda(((Double) item.get("precoUnitario")).floatValue());
                                produto.setQtdProduto(((Long) item.get("quantidade")).intValue());
                                produto.setPrecoVenda(((Double) item.get("precoTotal")).floatValue());

                                listaCarrinho.add(produto);
                                subtotal += produto.getPrecoVenda();
                            }
                            carrinhoAdapter.notifyDataSetChanged();
                            atualizarSubtotal();
                        } else {
                            Log.e("CarrinhoActivity", "Erro ao carregar carrinho", task.getException());
                        }
                    });
        } else {
            Toast.makeText(CarrinhoVendas.this, "Você precisa estar logado para visualizar o carrinho.", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarSubtotal() {
        textSubtotal.setText(String.format("Subtotal: R$%.2f", subtotal));

        String descontoStr = editDesconto.getText().toString();
        double desconto = 0.0;

        if (!descontoStr.isEmpty()) {
            try {
                desconto = Double.parseDouble(descontoStr);
            } catch (NumberFormatException e) {
                desconto = 0.0;
            }
        }

        double total = subtotal - desconto;
        textTotal.setText(String.format("Total: R$%.2f", total));
    }


    private void finalizarCompra() {
        String descontoStr = editDesconto.getText().toString();
        double desconto = 0.0;

        if (!descontoStr.isEmpty()) {
            try {
                desconto = Double.parseDouble(descontoStr);
            } catch (NumberFormatException e) {
                desconto = 0.0;
            }
        }

        double total = subtotal - desconto;

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            Map<String, Object> compra = new HashMap<>();
            compra.put("usuarioId", userId);
            compra.put("subtotal", subtotal);
            compra.put("desconto", desconto);
            compra.put("total", total);
            compra.put("dataHora", System.currentTimeMillis());

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference comprasRef = firestore.collection("Compras");

            comprasRef.add(compra)
                    .addOnSuccessListener(documentReference -> {
                        String compraId = documentReference.getId();

                        CollectionReference itensRef = firestore.collection("Compras").document(compraId).collection("Itens");
                        List<Map<String, Object>> itens = new ArrayList<>();
                        for (Produtos produto : listaCarrinho) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("nome", produto.getNome());
                            item.put("precoUnitario", produto.getPrecoVenda());
                            item.put("quantidade", produto.getQtdProduto());
                            item.put("precoTotal", produto.getPrecoVenda() * produto.getQtdProduto());
                            itens.add(item);
                        }

                        for (Map<String, Object> item : itens) {
                            itensRef.add(item)
                                    .addOnFailureListener(e -> {
                                        Log.e("CarrinhoActivity", "Erro ao adicionar item à compra", e);
                                        Toast.makeText(CarrinhoVendas.this, "Erro ao finalizar compra", Toast.LENGTH_SHORT).show();
                                    });
                        }

                        for (Produtos produto : listaCarrinho) {
                            Log.d("CarrinhoActivity", "Quantidade no carrinho: " + produto.getQtdProduto());
                            atualizarEstoque(produto);
                        }

                        limparCarrinho(userId);
                        Toast.makeText(CarrinhoVendas.this, String.format("Compra finalizada com sucesso! Total: R$%.2f", total), Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CarrinhoActivity", "Erro ao salvar compra", e);
                        Toast.makeText(CarrinhoVendas.this, "Erro ao finalizar compra", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(CarrinhoVendas.this, "Você precisa estar logado para finalizar a compra.", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarEstoque(Produtos produto) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Produtos")
                .whereEqualTo("nome", produto.getNome())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        int estoqueAtual = document.getLong("qtdProduto").intValue();

                        Log.d("CarrinhoActivity", "Estoque atual do produto '" + produto.getNome() + "': " + estoqueAtual);

                        int novaQuantidadeEstoque = estoqueAtual - produto.getQtdProduto();

                        Log.d("CarrinhoActivity", "Nova quantidade de estoque após subtração: " + novaQuantidadeEstoque);

                        document.getReference().update("qtdProduto", novaQuantidadeEstoque)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("CarrinhoActivity", "Estoque atualizado com sucesso!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("CarrinhoActivity", "Erro ao atualizar estoque", e);
                                });
                    } else {
                        Log.e("CarrinhoActivity", "Produto não encontrado para atualizar estoque");
                    }
                });
    }




    private void limparCarrinho(String userId) {
        FirebaseFirestore.getInstance().collection("Carrinho").document(userId)
                .collection("Itens")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documento : task.getResult()) {
                            documento.getReference().delete();
                        }
                        listaCarrinho.clear();
                        carrinhoAdapter.notifyDataSetChanged();
                        subtotal = 0.0;
                        atualizarSubtotal();
                    } else {
                        Log.e("CarrinhoActivity", "Erro ao limpar carrinho", task.getException());
                    }
                });
    }

}