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

import br.unigran.tcc.Model.ProdutoPP;
import br.unigran.tcc.R;

public class CadastroProdutoPP extends AppCompatActivity {

    private EditText etNome;
    private EditText etPrecoVenda;
    private Spinner spTipo;
    private Button btnCadastrar;
    private TextView tvNomeError, tvPrecoVendaError, tvTipoError, tvTitulo;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto_pp);

        etNome = findViewById(R.id.idNomeProdutoPP);
        etPrecoVenda = findViewById(R.id.idPrecoVendaPP);
        spTipo = findViewById(R.id.idTipoProdutoPP);
        btnCadastrar = findViewById(R.id.idCadastrarProdutoPP);
        tvNomeError = findViewById(R.id.nomeError);
        tvPrecoVendaError = findViewById(R.id.precoVendaError);
        tvTipoError = findViewById(R.id.tipoError);
        tvTitulo = findViewById(R.id.textView4);

        btnCadastrar.setOnClickListener(v -> salvarProduto());

        ArrayList<String> tipoList = new ArrayList<>();
        tipoList.add("Escolha um tipo");
        tipoList.add("Alimento");
        tipoList.add("Bebida");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipoList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        adicionarObservadoresDeTexto();

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));
        window.setNavigationBarColor(getResources().getColor(android.R.color.black));

        Intent intent = getIntent();
        if (intent.hasExtra("produtoPP")) {
            ProdutoPP produto = (ProdutoPP) intent.getSerializableExtra("produtoPP");
            preencherCampos(produto);
            tvTitulo.setText("Editar Produto");
            btnCadastrar.setText("Atualizar Produto");
        }
    }

    private void adicionarObservadoresDeTexto() {
        etNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    etNome.setBackgroundResource(android.R.drawable.edit_text);
                    tvNomeError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etPrecoVenda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    etPrecoVenda.setBackgroundResource(android.R.drawable.edit_text);
                    tvPrecoVendaError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spTipo.setBackgroundResource(android.R.drawable.edit_text);
                    tvTipoError.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private boolean temCamposVazios() {
        boolean vazio = false;
        if (etNome.getText().toString().isEmpty()) {
            etNome.setBackgroundResource(R.drawable.borda_vermelha);
            tvNomeError.setText("Este campo é obrigatório!");
            tvNomeError.setVisibility(View.VISIBLE);
            vazio = true;
        }
        if (etPrecoVenda.getText().toString().isEmpty()) {
            etPrecoVenda.setBackgroundResource(R.drawable.borda_vermelha);
            tvPrecoVendaError.setText("Este campo é obrigatório!");
            tvPrecoVendaError.setVisibility(View.VISIBLE);
            vazio = true;
        }
        if (spTipo.getSelectedItemPosition() == 0) {
            spTipo.setBackgroundResource(R.drawable.borda_vermelha);
            tvTipoError.setText("Este campo é obrigatório!");
            tvTipoError.setVisibility(View.VISIBLE);
            vazio = true;
        }
        return vazio;
    }

    private void limparCampos() {
        etNome.setText("");
        etPrecoVenda.setText("");
        spTipo.setSelection(0);
    }

    private void preencherCampos(ProdutoPP produto) {
        etNome.setText(produto.getNome());
        etPrecoVenda.setText(String.valueOf(produto.getPrecoVenda()));

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spTipo.getAdapter();
        int posicao = adapter.getPosition(produto.getTipoProdutoPP());
        spTipo.setSelection(posicao);
    }

    private void salvarProduto() {
        if (temCamposVazios()) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProdutoPP produto = new ProdutoPP();
        produto.setNome(etNome.getText().toString());
        produto.setPrecoVenda(Float.valueOf(etPrecoVenda.getText().toString().replace(",", ".")));
        produto.setTipoProdutoPP(spTipo.getSelectedItem().toString());

        Intent intent = getIntent();
        if (intent.hasExtra("produtoPP")) {
            ProdutoPP produtoExistente = (ProdutoPP) intent.getSerializableExtra("produtoPP");
            firestore.collection("ProdutosPP").document(produtoExistente.getId()).set(produto)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), "Produto atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                        voltarParaLista();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Falha ao atualizar produto.", Toast.LENGTH_SHORT).show();
                        Log.e("CadastroProdutoPP", "Erro: ", e);
                    });
        } else {
            firestore.collection("ProdutosPP").add(produto)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Produto salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        limparCampos();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Falha ao salvar produto.", Toast.LENGTH_SHORT).show();
                        Log.e("CadastroProdutoPP", "Erro: ", e);
                    });
        }
    }

    private void voltarParaLista() {
        Intent intent = new Intent(this, ListarProdutoPP.class);
        startActivity(intent);
        finish();
    }
}
