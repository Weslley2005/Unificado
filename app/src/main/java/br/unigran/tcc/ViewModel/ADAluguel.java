package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import br.unigran.tcc.Model.EquipamentoAluguel;
import br.unigran.tcc.R;

public class ADAluguel extends AppCompatActivity {

    private TextView nomeProduto;
    private TextView precoAluguelM;
    private TextView precoAluguelI;
    public EditText qtdParaAluguel;
    private TextView precoTotal;
    private Button btnAdicionarCarrinho;
    public Switch switchTipoAluguel;
    private double precoUnitarioM;
    private double precoUnitarioI;

    private FirebaseFirestore db;
    private String itemId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adaluguel);

        nomeProduto = findViewById(R.id.textNomeProduto);
        precoAluguelM = findViewById(R.id.textPrecoAluguelM);
        precoAluguelI = findViewById(R.id.textPrecoAluguelI);
        qtdParaAluguel = findViewById(R.id.editQtdAluguel);
        precoTotal = findViewById(R.id.textPrecoTotal);
        btnAdicionarCarrinho = findViewById(R.id.btnAdicionarCarrinho);
        switchTipoAluguel = findViewById(R.id.switchTipoAluguel);

        db = FirebaseFirestore.getInstance();

        itemId = getIntent().getStringExtra("id");

        if (itemId != null) {
            buscarDadosDoBanco();
        }

        Intent intent = getIntent();
        String nome = intent.getStringExtra("nome");
        precoUnitarioM = intent.getFloatExtra("precoAluguelM", 0);
        precoUnitarioI = intent.getFloatExtra("precoAluguelI", 0);
        int qtdProduto = intent.getIntExtra("qtdAluguel", 1);

        nomeProduto.setText(nome);
        precoAluguelM.setText(String.format("Preço de Aluguel M: R$%.2f", precoUnitarioM));
        precoAluguelI.setText(String.format("Preço de Aluguel I: R$%.2f", precoUnitarioI));
        precoTotal.setText(String.format("Preço Total: R$%.2f", precoUnitarioM * 0));

        Window janela = getWindow();
        janela.setStatusBarColor(getResources().getColor(android.R.color.black));
        janela.setNavigationBarColor(getResources().getColor(android.R.color.black));

        qtdParaAluguel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarPrecoTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        switchTipoAluguel.setOnCheckedChangeListener((buttonView, isChecked) -> atualizarPrecoTotal());

        btnAdicionarCarrinho.setOnClickListener(v -> adicionarAoCarrinho());
    }

    private void atualizarPrecoTotal() {
        String textoQtd = qtdParaAluguel.getText().toString();
        int quantidade = 0;

        if (!textoQtd.isEmpty()) {
            quantidade = Integer.parseInt(textoQtd);
        }

        double precoTotalCalculado;
        if (switchTipoAluguel.isChecked()) {
            precoTotalCalculado = precoUnitarioI * quantidade;
        } else {
            precoTotalCalculado = precoUnitarioM * quantidade;
        }

        precoTotal.setText(String.format("Preço Total: R$%.2f", precoTotalCalculado));
    }

    private void adicionarAoCarrinho() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();
            String nome = nomeProduto.getText().toString();
            int quantidadeDesejada = Integer.parseInt(qtdParaAluguel.getText().toString());

            if (itemId != null) {
                FirebaseFirestore.getInstance().collection("EquipamentoAluguel")
                        .whereEqualTo("nome", nome)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                QueryDocumentSnapshot documento = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                                EquipamentoAluguel equipAlugEstoque = documento.toObject(EquipamentoAluguel.class);
                                int qtdDisponivel = equipAlugEstoque.getQtdAluguel();

                                if (quantidadeDesejada <= qtdDisponivel) {
                                    FirebaseFirestore.getInstance().collection("CarrinhoAluguel").document(userId)
                                            .collection("ItensAluguel").document(itemId)
                                            .update("quantidade", quantidadeDesejada,
                                                    "tipoAluguel", switchTipoAluguel.isChecked(),
                                                    "precoTotal", calcularPrecoTotal(quantidadeDesejada))
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(ADAluguel.this, "Produto atualizado no carrinho", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ADAluguel.this, CarrinhoAluguel.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(ADAluguel.this, "Erro ao atualizar produto no carrinho", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(ADAluguel.this, "Estoque insuficiente para a quantidade desejada.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ADAluguel.this, "Erro ao verificar estoque", Toast.LENGTH_SHORT).show();
                        });
            } else {
                FirebaseFirestore.getInstance().collection("EquipamentoAluguel")
                        .whereEqualTo("nome", nome)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                QueryDocumentSnapshot documento = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                                EquipamentoAluguel equipAlugEstoque = documento.toObject(EquipamentoAluguel.class);
                                int qtdDisponivel = equipAlugEstoque.getQtdAluguel();

                                if (quantidadeDesejada <= qtdDisponivel) {
                                    double precoTotalCalculado = calcularPrecoTotal(quantidadeDesejada);

                                    Map<String, Object> produto = new HashMap<>();
                                    produto.put("id", documento.getId());
                                    produto.put("nome", nome);
                                    produto.put("precoAluguelM", precoUnitarioM);
                                    produto.put("precoAluguelI", precoUnitarioI);
                                    produto.put("quantidade", quantidadeDesejada);
                                    produto.put("precoTotal", precoTotalCalculado);
                                    produto.put("tipoAluguel", switchTipoAluguel.isChecked());

                                    FirebaseFirestore.getInstance().collection("CarrinhoAluguel").document(userId)
                                            .collection("ItensAluguel")
                                            .add(produto)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(ADAluguel.this, "Produto adicionado ao carrinho", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(ADAluguel.this, "Erro ao adicionar produto ao carrinho", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(ADAluguel.this, "Estoque insuficiente para a quantidade desejada.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ADAluguel.this, "Erro ao verificar estoque", Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Toast.makeText(ADAluguel.this, "Você precisa estar logado para adicionar itens ao carrinho.", Toast.LENGTH_SHORT).show();
        }
    }


    private double calcularPrecoTotal(int quantidade) {
        return switchTipoAluguel.isChecked() ? precoUnitarioI * quantidade : precoUnitarioM * quantidade;
    }


    private void buscarDadosDoBanco() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            FirebaseFirestore.getInstance().collection("CarrinhoAluguel").document(userId)
                    .collection("ItensAluguel")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(ADAluguel.this, "Nenhum item encontrado.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                if (documentSnapshot.exists()) {
                                    String nome = documentSnapshot.getString("nome");
                                    precoUnitarioM = documentSnapshot.getDouble("precoAluguelM");
                                    precoUnitarioI = documentSnapshot.getDouble("precoAluguelI");
                                    int qtdAluguel = documentSnapshot.getLong("quantidade").intValue();
                                    boolean tipoAluguel = documentSnapshot.getBoolean("tipoAluguel");

                                    nomeProduto.setText(nome);
                                    qtdParaAluguel.setText(String.valueOf(qtdAluguel));
                                    precoAluguelM.setText(String.format("Preço de Aluguel M: R$%.2f", precoUnitarioM));
                                    precoAluguelI.setText(String.format("Preço de Aluguel I: R$%.2f", precoUnitarioI));
                                    precoTotal.setText(String.format("Preço Total: R$%.2f", calcularPrecoTotal(qtdAluguel)));
                                    switchTipoAluguel.setChecked(tipoAluguel);

                                    Toast.makeText(ADAluguel.this, "Dados carregados com sucesso: " + nome, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ADAluguel.this, "Documento não existe: " + documentSnapshot.getId(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ADAluguel.this, "Erro ao buscar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ADAluguel.this, "Você precisa estar logado para buscar itens no carrinho.", Toast.LENGTH_SHORT).show();
        }
    }


}
