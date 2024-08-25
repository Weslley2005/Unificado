package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import br.unigran.tcc.Model.Produtos;
import br.unigran.tcc.R;

public class CadastroProduto extends AppCompatActivity {

    private EditText nomeProduto;
    private EditText quantidadeProduto;
    private EditText precoCompraProduto;
    private EditText precoVendaProduto;
    private Spinner tipoProduto;
    private Button botaoCadastrar;
    private TextView erroNome, erroQuantidade, erroPrecoCompra, erroPrecoVenda, erroTipo, tituloEdicao;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @SuppressLint({"WrongViewCast", "MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto);

        nomeProduto = findViewById(R.id.idNomeProduto);
        quantidadeProduto = findViewById(R.id.idQtdProduto);
        precoCompraProduto = findViewById(R.id.idPrecoCompra);
        precoVendaProduto = findViewById(R.id.idPrecoVenda);
        tipoProduto = findViewById(R.id.idTipoProduto);
        botaoCadastrar = findViewById(R.id.idCadastrarProduto);
        erroNome = findViewById(R.id.nomeError);
        erroQuantidade = findViewById(R.id.quantidadeError);
        erroPrecoCompra = findViewById(R.id.precoCompraError);
        erroPrecoVenda = findViewById(R.id.precoVendaError);
        erroTipo = findViewById(R.id.tipoError);
        tituloEdicao = findViewById(R.id.textView2);

        botaoCadastrar.setOnClickListener(view -> salvarProduto());

        ArrayList<String> tiposProduto = new ArrayList<>();
        tiposProduto.add("Selecione um tipo");
        tiposProduto.add("Alimento");
        tiposProduto.add("Bebida");

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposProduto);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoProduto.setAdapter(adapterSpinner);

        adicionarObservadoresDeTexto();

        Window janela = getWindow();
        janela.setStatusBarColor(getResources().getColor(android.R.color.black));
        janela.setNavigationBarColor(getResources().getColor(android.R.color.black));

        Intent intentRecebido = getIntent();
        if (intentRecebido.hasExtra("produto")) {
            Produtos produtoEditado = (Produtos) intentRecebido.getSerializableExtra("produto");
            preencherCampos(produtoEditado);
            tituloEdicao.setText("Editar Produto");
            botaoCadastrar.setText("Atualizar Produto");
        }
    }

    private void adicionarObservadoresDeTexto() {
        nomeProduto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    nomeProduto.setBackgroundResource(android.R.drawable.edit_text);
                    erroNome.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        quantidadeProduto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    quantidadeProduto.setBackgroundResource(android.R.drawable.edit_text);
                    erroQuantidade.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        precoCompraProduto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    precoCompraProduto.setBackgroundResource(android.R.drawable.edit_text);
                    erroPrecoCompra.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        precoVendaProduto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    precoVendaProduto.setBackgroundResource(android.R.drawable.edit_text);
                    erroPrecoVenda.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        tipoProduto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    tipoProduto.setBackgroundResource(android.R.drawable.edit_text);
                    erroTipo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private boolean verificarCampos() {
        boolean camposVazios = false;
        if (nomeProduto.getText().toString().isEmpty()) {
            nomeProduto.setBackgroundResource(R.drawable.borda_vermelha);
            erroNome.setText("Campo obrigatório!");
            erroNome.setVisibility(View.VISIBLE);
            camposVazios = true;
        }
        if (quantidadeProduto.getText().toString().isEmpty()) {
            quantidadeProduto.setBackgroundResource(R.drawable.borda_vermelha);
            erroQuantidade.setText("Campo obrigatório!");
            erroQuantidade.setVisibility(View.VISIBLE);
            camposVazios = true;
        }
        if (precoCompraProduto.getText().toString().isEmpty()) {
            precoCompraProduto.setBackgroundResource(R.drawable.borda_vermelha);
            erroPrecoCompra.setText("Campo obrigatório!");
            erroPrecoCompra.setVisibility(View.VISIBLE);
            camposVazios = true;
        }
        if (precoVendaProduto.getText().toString().isEmpty()) {
            precoVendaProduto.setBackgroundResource(R.drawable.borda_vermelha);
            erroPrecoVenda.setText("Campo obrigatório!");
            erroPrecoVenda.setVisibility(View.VISIBLE);
            camposVazios = true;
        }
        if (tipoProduto.getSelectedItemPosition() == 0) {
            tipoProduto.setBackgroundResource(R.drawable.borda_vermelha);
            erroTipo.setText("Campo obrigatório!");
            erroTipo.setVisibility(View.VISIBLE);
            camposVazios = true;
        }
        return camposVazios;
    }

    public void limparCampos() {
        nomeProduto.setText("");
        quantidadeProduto.setText("");
        precoCompraProduto.setText("");
        precoVendaProduto.setText("");
        tipoProduto.setSelection(0);
    }

    private void preencherCampos(Produtos produto) {
        nomeProduto.setText(produto.getNome());
        quantidadeProduto.setText(String.valueOf(produto.getQtdProduto()));
        precoCompraProduto.setText(String.valueOf(produto.getPrecoCompra()));
        precoVendaProduto.setText(String.valueOf(produto.getPrecoVenda()));

        ArrayAdapter<String> adapterSpinner = (ArrayAdapter<String>) tipoProduto.getAdapter();
        int posicaoSpinner = adapterSpinner.getPosition(produto.getTipo());
        tipoProduto.setSelection(posicaoSpinner);
    }

    private void salvarProduto() {
        if (verificarCampos()) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        Produtos produto = new Produtos();
        produto.setNome(nomeProduto.getText().toString());
        produto.setQtdProduto(Integer.parseInt(quantidadeProduto.getText().toString()));
        produto.setPrecoCompra(Float.parseFloat(precoCompraProduto.getText().toString().replace(",", ".")));
        produto.setPrecoVenda(Float.parseFloat(precoVendaProduto.getText().toString().replace(",", ".")));
        produto.setTipo(tipoProduto.getSelectedItem().toString());

        Intent intent = getIntent();
        if (intent.hasExtra("produto")) {
            Produtos produtoExistente = (Produtos) intent.getSerializableExtra("produto");
            firestore.collection("Produtos").document(produtoExistente.getId()).set(produto)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), "Produto atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                        voltarParaLista();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Erro ao atualizar produto.", Toast.LENGTH_SHORT).show();
                        Log.e("CadastroProduto", "Erro: ", e);
                    });
        } else {
            firestore.collection("Produtos").add(produto)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Produto salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        limparCampos();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Erro ao salvar produto.", Toast.LENGTH_SHORT).show();
                        Log.e("CadastroProduto", "Erro: ", e);
                    });
        }
    }

    public void voltarParaLista() {
        Intent intent = new Intent(this, ListarProdutos.class);
        startActivity(intent);
        finish();
    }
}
