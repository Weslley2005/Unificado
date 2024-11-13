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

import br.unigran.tcc.R;

public class ACPPVAlimentos extends AppCompatActivity {

    public TextView nomeProduto;
    public TextView precoVenda;
    public EditText qtdParaVenda;
    public TextView precoTotal;
    private Button btnAdicionarCarrinho;

    public double precoUnitario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acppvalimentos);

        nomeProduto = findViewById(R.id.textNomeProduto);
        precoVenda = findViewById(R.id.textPrecoAluguelM);
        qtdParaVenda = findViewById(R.id.editQtdAluguel);
        precoTotal = findViewById(R.id.textPrecoTotal);
        btnAdicionarCarrinho = findViewById(R.id.btnAdicionarCarrinho);

        Intent intent = getIntent();
        String nome = intent.getStringExtra("nome");
        precoUnitario = intent.getFloatExtra("precoVenda", 0);

        nomeProduto.setText(nome);
        precoVenda.setText(String.format("Preço de Venda: R$%.2f", precoUnitario));
        precoTotal.setText(String.format("Preço Total: R$%.2f", precoUnitario * 0));

        Window janela = getWindow();
        janela.setStatusBarColor(getResources().getColor(android.R.color.black));
        janela.setNavigationBarColor(getResources().getColor(android.R.color.black));

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

        btnAdicionarCarrinho.setOnClickListener(v -> adicionarAoCarrinho());
    }

    public void atualizarPrecoTotal() {
        String textoQtd = qtdParaVenda.getText().toString();
        int quantidade = 0;

        if (!textoQtd.isEmpty()) {
            try {
                quantidade = Integer.parseInt(textoQtd);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Quantidade inválida.", Toast.LENGTH_SHORT).show();
            }
        }

        double precoTotalCalculado = precoUnitario * quantidade;
        precoTotal.setText(String.format("Preço Total: R$%.2f", precoTotalCalculado));
    }

    private void adicionarAoCarrinho() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();
            String nome = nomeProduto.getText().toString();
            String textoQuantidade = qtdParaVenda.getText().toString();

            if (textoQuantidade.isEmpty()) {
                Toast.makeText(this, "Digite uma quantidade válida.", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantidadeDesejada;
            try {
                quantidadeDesejada = Integer.parseInt(textoQuantidade);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Quantidade inválida.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance().collection("ProdutosPP")
                    .whereEqualTo("nome", nome)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            QueryDocumentSnapshot documento = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);

                            double precoTotalCalculado = precoUnitario * quantidadeDesejada;

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
                                        Toast.makeText(ACPPVAlimentos.this, "Produto adicionado ao carrinho", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ACPPVAlimentos.this, "Erro ao adicionar produto ao carrinho", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(ACPPVAlimentos.this, "Produto não encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ACPPVAlimentos.this, "Erro ao verificar estoque", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ACPPVAlimentos.this, "Você precisa estar logado para adicionar itens ao carrinho.", Toast.LENGTH_SHORT).show();
        }
    }
}
