package br.unigran.tcc.ViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import br.unigran.tcc.Model.Produtos;
import br.unigran.tcc.R;

public class ACPVAlimento extends AppCompatActivity {

    private TextView nomeProduto;
    private TextView precoVenda;
    private EditText qtdParaVenda;
    private TextView precoTotal;
    private Button btnAdicionarCarrinho;
    private double precoUnitario;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acpvalimento);

        // Inicializando as views
        nomeProduto = findViewById(R.id.textNomeProduto);
        precoVenda = findViewById(R.id.textPrecoAluguelM);
        qtdParaVenda = findViewById(R.id.editQtdAluguel);
        precoTotal = findViewById(R.id.textPrecoTotal);
        btnAdicionarCarrinho = findViewById(R.id.btnAdicionarCarrinho);

        // Obtendo dados passados via Intent
        Intent intent = getIntent();
        String nome = intent.getStringExtra("nome");
        precoUnitario = intent.getFloatExtra("precoVenda", 0);
        itemId = intent.getStringExtra("id");

        nomeProduto.setText(nome);
        precoVenda.setText(String.format("Preço de Venda: R$%.2f", precoUnitario));
        precoTotal.setText(String.format("Preço Total: R$%.2f", 0.0));

        // Personalizando a barra de status e de navegação
        Window janela = getWindow();
        janela.setStatusBarColor(getResources().getColor(android.R.color.black));
        janela.setNavigationBarColor(getResources().getColor(android.R.color.black));

        // Atualizando preço total com base na quantidade
        qtdParaVenda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarPrecoTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Adicionando produto ao carrinho
        btnAdicionarCarrinho.setOnClickListener(v -> adicionarAoCarrinho());

        // Se um ID foi passado, buscar os dados do banco
        if (itemId != null) {
            buscarDadosDoBanco();
        }
    }

    // Atualiza o preço total com base na quantidade inserida
    private void atualizarPrecoTotal() {
        String textoQtd = qtdParaVenda.getText().toString();
        int quantidade = 0;

        if (!textoQtd.isEmpty()) {
            quantidade = Integer.parseInt(textoQtd);
        }

        double precoTotalCalculado = precoUnitario * quantidade;
        precoTotal.setText(String.format("Preço Total: R$%.2f", precoTotalCalculado));
    }

    // Adiciona ou atualiza o produto no carrinho
    private void adicionarAoCarrinho() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();
            String nome = nomeProduto.getText().toString();
            int quantidadeDesejada = Integer.parseInt(qtdParaVenda.getText().toString());

            // Verifica se o item já está no carrinho e atualiza
            if (itemId != null) {
                FirebaseFirestore.getInstance().collection("Carrinho").document(userId)
                        .collection("Itens").document(itemId)
                        .update("quantidade", quantidadeDesejada,
                                "precoTotal", calcularPrecoTotalC(quantidadeDesejada))
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ACPVAlimento.this, "Produto atualizado no carrinho", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ACPVAlimento.this, CarrinhoVendas.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // Limpa a pilha de atividades para evitar duplicatas
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ACPVAlimento.this, "Erro ao atualizar produto no carrinho", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Busca o item pelo nome no estoque antes de adicionar
                FirebaseFirestore.getInstance().collection("Produtos")
                        .whereEqualTo("nome", nome)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                QueryDocumentSnapshot documento = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                                Produtos produtosEstoque = documento.toObject(Produtos.class);
                                int qtdDisponivel = produtosEstoque.getQtdProduto();

                                if (quantidadeDesejada <= qtdDisponivel) {
                                    double precoTotalCalculado = precoUnitario * quantidadeDesejada;

                                    // Cria um mapa com as informações do produto
                                    Map<String, Object> produto = new HashMap<>();
                                    produto.put("id", documento.getId());
                                    produto.put("nome", nome);
                                    produto.put("precoUnitario", precoUnitario);
                                    produto.put("quantidade", quantidadeDesejada);
                                    produto.put("precoTotal", precoTotalCalculado);

                                    FirebaseFirestore.getInstance().collection("Carrinho").document(userId)
                                            .collection("Itens")
                                            .add(produto)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(ACPVAlimento.this, "Produto adicionado ao carrinho", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(ACPVAlimento.this, "Erro ao adicionar produto ao carrinho", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(ACPVAlimento.this, "Estoque insuficiente para a quantidade desejada.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ACPVAlimento.this, "Erro ao verificar estoque", Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Toast.makeText(ACPVAlimento.this, "Você precisa estar logado para adicionar itens ao carrinho.", Toast.LENGTH_SHORT).show();
        }
    }

    // Busca dados no banco para preencher os campos
    private void buscarDadosDoBanco() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            FirebaseFirestore.getInstance().collection("Carrinho").document(userId)
                    .collection("Itens")
                    .document(itemId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Preenche os campos com os dados recuperados
                            String nome = documentSnapshot.getString("nome");
                            precoUnitario = documentSnapshot.getDouble("precoUnitario");
                            int qtdAluguel = documentSnapshot.getLong("quantidade").intValue();

                            nomeProduto.setText(nome);
                            qtdParaVenda.setText(String.valueOf(qtdAluguel));
                            precoVenda.setText(String.format("Preço Unitário: R$%.2f", precoUnitario));
                            precoTotal.setText(String.format("Preço Total: R$%.2f", calcularPrecoTotalC(qtdAluguel)));
                        } else {
                            Toast.makeText(ACPVAlimento.this, "Nenhum item encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ACPVAlimento.this, "Erro ao buscar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ACPVAlimento.this, "Você precisa estar logado para buscar itens no carrinho.", Toast.LENGTH_SHORT).show();
        }
    }

    // Função auxiliar para calcular o preço total
    private double calcularPrecoTotalC(int quantidade) {
        return precoUnitario * quantidade;
    }
}
