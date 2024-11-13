package br.unigran.tcc.ViewModel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

        recyclerViewCarrinho = findViewById(R.id.recyclerViewCarrinhoAluguel);
        textSubtotal = findViewById(R.id.textSubtotal);
        textTotal = findViewById(R.id.textTotal);
        editDesconto = findViewById(R.id.editDesconto);
        buttonFinalizar = findViewById(R.id.buttonFinalizar);

        listaCarrinho = new ArrayList<>();
        carrinhoAdapter = new CarrinhoVendasAdapter(listaCarrinho, this);
        recyclerViewCarrinho.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCarrinho.setAdapter(carrinhoAdapter);

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();
            FirebaseFirestore.getInstance().collection("Carrinho").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nomeAluguel = documentSnapshot.getString("idNomenAluguel");

                            if (nomeAluguel == null) {
                                carregarCarrinho();
                            } else {
                                carregarCarrinhoEditar();
                            }
                        } else {
                            Log.e("CarrinhoActivity", "Documento não encontrado.");
                            carregarCarrinho();
                        }
                    })
                    .addOnFailureListener(e -> Log.e("CarrinhoActivity", "Erro ao carregar documento", e));
        } else {
            Toast.makeText(CarrinhoVendas.this, "Você precisa estar logado para visualizar o carrinho.", Toast.LENGTH_SHORT).show();
        }

        buttonFinalizar.setOnClickListener(v -> finalizarCompra());

        editDesconto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarSubtotal();
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
                                produto.setId(documento.getId());

                                Object precoUnitarioObj = item.get("precoUnitario");
                                if (precoUnitarioObj != null) {
                                    produto.setPrecoVenda(((Double) precoUnitarioObj).floatValue());
                                } else {
                                    produto.setPrecoVenda(0f);
                                }

                                Object quantidadeObj = item.get("quantidade");
                                if (quantidadeObj != null) {
                                    produto.setQtdProduto(((Long) quantidadeObj).intValue());
                                } else {
                                    produto.setQtdProduto(0);
                                }

                                Object precoCompraObj = item.get("precoCompra");
                                if (precoCompraObj != null) {
                                    produto.setPrecoCompra(((Double) precoCompraObj).floatValue());
                                } else {
                                    produto.setPrecoCompra(0f);
                                }

                                listaCarrinho.add(produto);
                                subtotal += produto.getPrecoVenda() * produto.getQtdProduto();
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

    private void carregarCarrinhoEditar() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("Carrinho").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nomeAluguel = documentSnapshot.getString("idNomenAluguel");


                            EditText editNomeAluguel = findViewById(R.id.idNomeAluguel);


                            editNomeAluguel.setText(nomeAluguel);
                        } else {
                            Log.e("CarrinhoActivity", "Documento não encontrado para carregar nome e telefone.");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("CarrinhoActivity", "Erro ao carregar nome e telefone", e));

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
                                produto.setId(documento.getId());

                                Object precoUnitarioObj = item.get("precoUnitario");
                                if (precoUnitarioObj != null) {
                                    produto.setPrecoVenda(((Double) precoUnitarioObj).floatValue());
                                } else {
                                    produto.setPrecoVenda(0f);
                                }

                                Object quantidadeObj = item.get("quantidade");
                                if (quantidadeObj != null) {
                                    produto.setQtdProduto(((Long) quantidadeObj).intValue());
                                } else {
                                    produto.setQtdProduto(0);
                                }

                                Object precoCompraObj = item.get("precoCompra");
                                if (precoCompraObj != null) {
                                    produto.setPrecoCompra(((Double) precoCompraObj).floatValue());
                                } else {
                                    produto.setPrecoCompra(0f);
                                }

                                listaCarrinho.add(produto);
                                subtotal += produto.getPrecoVenda() * produto.getQtdProduto();
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

        String descontoStr = editDesconto.getText().toString().replace(",", ".");
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
        EditText editNomeAluguel = findViewById(R.id.idNomeAluguel);
        TextView erroNomeAluguel = findViewById(R.id.erroNomeAluguel);

        String nomeAluguel = editNomeAluguel.getText().toString().trim();


        boolean camposVazios = false;

        if (nomeAluguel.isEmpty()) {
            editNomeAluguel.setBackgroundResource(R.drawable.borda_vermelha);
            erroNomeAluguel.setText("Campo obrigatório!");
            erroNomeAluguel.setVisibility(View.VISIBLE);
            camposVazios = true;
        } else {
            editNomeAluguel.setBackgroundResource(0);
            erroNomeAluguel.setVisibility(View.GONE);
        }

        if (camposVazios) {
            return;
        }

        String descontoStr = editDesconto.getText().toString().replace(",", ".");
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

            Date dataAtual = new Date();
            SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat formatoHora = new SimpleDateFormat("mm:ss", Locale.getDefault());

            String dataFormatada = formatoData.format(dataAtual);
            String horaFormatada = formatoHora.format(dataAtual);

            Map<String, Object> compra = new HashMap<>();
            compra.put("usuarioId", userId);
            compra.put("subtotal", subtotal);
            compra.put("desconto", desconto);
            compra.put("total", total);
            compra.put("precoCompra", total); // Incluindo precoCompra
            compra.put("data", dataFormatada);
            compra.put("hora", horaFormatada);
            compra.put("idNomenAluguel", nomeAluguel);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference comprasRef = firestore.collection("Compras");

            comprasRef.add(compra)
                    .addOnSuccessListener(documentReference -> {
                        String compraId = documentReference.getId();

                        CollectionReference itensRef = firestore.collection("Compras").document(compraId).collection("Itens");
                        List<Map<String, Object>> itens = new ArrayList<>();
                        for (Produtos produtos : listaCarrinho) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("nome", produtos.getNome());
                            item.put("precoUnitario", produtos.getPrecoVenda());
                            item.put("quantidade", produtos.getQtdProduto());
                            item.put("precoTotal", produtos.getPrecoVenda() * produtos.getQtdProduto());
                            item.put("precoCompra", produtos.getPrecoCompra());
                            itens.add(item);
                        }

                        for (Map<String, Object> item : itens) {
                            itensRef.add(item)
                                    .addOnFailureListener(e -> {
                                        Log.e("CarrinhoActivity", "Erro ao adicionar item à compra", e);
                                        Toast.makeText(CarrinhoVendas.this, "Erro ao finalizar compra", Toast.LENGTH_SHORT).show();
                                    });
                        }

                        for (Produtos produtos : listaCarrinho) {
                            atualizarEstoque(produtos);
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

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Carrinho").document(userId)
                .delete()  // Directly delete the document
                .addOnSuccessListener(aVoid -> {
                    listaCarrinho.clear(); // Clear the local list
                    carrinhoAdapter.notifyDataSetChanged(); // Notify adapter to refresh the view
                    subtotal = 0.0; // Reset the subtotal
                    atualizarSubtotal(); // Update the displayed subtotal
                    Log.i("CarrinhoActivity", "Carrinho limpo com sucesso");
                })
                .addOnFailureListener(e -> {
                    Log.e("CarrinhoActivity", "Erro ao limpar carrinho", e);
                });
    }

    public void mostrarDialogoDeConfirmacao(int posicao, Produtos produtos) {
        AlertDialog.Builder construtor = new AlertDialog.Builder(this);
        construtor.setTitle("Confirmação");
        construtor.setMessage("Você tem certeza que deseja deletar este item?");

        construtor.setPositiveButton("Sim", (dialog, which) -> deletarItem(posicao, produtos));

        construtor.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());

        AlertDialog alertaDialogo = construtor.create();
        alertaDialogo.show();
    }

    private void deletarItem(int posicao, Produtos produtos) {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            FirebaseFirestore.getInstance().collection("Carrinho")
                    .document(userId)
                    .collection("Itens")
                    .document(produtos.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        listaCarrinho.remove(posicao);
                        carrinhoAdapter.notifyItemRemoved(posicao);
                        atualizarSubtotal();
                    })
                    .addOnFailureListener(e -> Log.e("CarrinhoAluguel", "Erro ao deletar item", e));
        } else {
            Log.e("CarrinhoAluguel", "Usuário não está logado.");
        }
    }
}
