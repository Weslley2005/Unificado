package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import br.unigran.tcc.Model.EquipamentoAluguel;
import br.unigran.tcc.R;

public class CadastroEquipAlug extends AppCompatActivity {

    private EditText nomeEquip;
    private EditText qtdEquip;
    private EditText precoAluguelM;
    private EditText precoAluguelI;
    private Button botaoSalvar;
    private TextView erroNome;
    private TextView erroQuantidade;
    private TextView erroPrecoAlugM;
    private TextView erroPrecoAlugI;
    private TextView tituloEdicao;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_equip_alug);

        nomeEquip = findViewById(R.id.idNomeEqui);
        qtdEquip = findViewById(R.id.idQtdEqui);
        precoAluguelM = findViewById(R.id.idPrecoEquiM);
        precoAluguelI = findViewById(R.id.idPrecoEquiI);
        botaoSalvar = findViewById(R.id.idCadastrarEqui);
        erroNome = findViewById(R.id.nomeError);
        erroQuantidade = findViewById(R.id.quantidadeError);
        erroPrecoAlugM = findViewById(R.id.precoAlugMError);
        erroPrecoAlugI = findViewById(R.id.precoAlugIError);
        tituloEdicao = findViewById(R.id.textView6);

        botaoSalvar.setOnClickListener(view -> salvarEquip());


        adicionarObservadoresDeTexto();

        Window janela = getWindow();
        janela.setStatusBarColor(getResources().getColor(android.R.color.black));
        janela.setNavigationBarColor(getResources().getColor(android.R.color.black));

        Intent intentRecebido = getIntent();
        if (intentRecebido.hasExtra("equipAlug")) {
            EquipamentoAluguel EquipAlugEditado = (EquipamentoAluguel) intentRecebido.getSerializableExtra("equipAlug");
            preencherCampos(EquipAlugEditado);
            tituloEdicao.setText("Editar Equipamento");
            botaoSalvar.setText("Atualizar Equipamento");
        }
    }

    private void adicionarObservadoresDeTexto() {
        nomeEquip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    nomeEquip.setBackgroundResource(android.R.drawable.edit_text);
                    erroNome.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        qtdEquip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    qtdEquip.setBackgroundResource(android.R.drawable.edit_text);
                    erroQuantidade.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        precoAluguelM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    precoAluguelM.setBackgroundResource(android.R.drawable.edit_text);
                    erroPrecoAlugM.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        precoAluguelI.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    precoAluguelI.setBackgroundResource(android.R.drawable.edit_text);
                    erroPrecoAlugI.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    private boolean verificarCampos() {
        boolean camposVazios = false;
        if (nomeEquip.getText().toString().isEmpty()) {
            nomeEquip.setBackgroundResource(R.drawable.borda_vermelha);
            erroNome.setText("Campo obrigatório!");
            erroNome.setVisibility(View.VISIBLE);
            camposVazios = true;
        }
        if (qtdEquip.getText().toString().isEmpty()) {
            qtdEquip.setBackgroundResource(R.drawable.borda_vermelha);
            erroQuantidade.setText("Campo obrigatório!");
            erroQuantidade.setVisibility(View.VISIBLE);
            camposVazios = true;
        }
        if (precoAluguelM.getText().toString().isEmpty()) {
            precoAluguelM.setBackgroundResource(R.drawable.borda_vermelha);
            erroPrecoAlugM.setText("Campo obrigatório!");
            erroPrecoAlugM.setVisibility(View.VISIBLE);
            camposVazios = true;
        }
        if (precoAluguelI.getText().toString().isEmpty()) {
            precoAluguelI.setBackgroundResource(R.drawable.borda_vermelha);
            erroPrecoAlugI.setText("Campo obrigatório!");
            erroPrecoAlugI.setVisibility(View.VISIBLE);
            camposVazios = true;
        }
        return camposVazios;
    }

    public void limparCampos() {
        nomeEquip.setText("");
        qtdEquip.setText("");
        precoAluguelM.setText("");
        precoAluguelI.setText("");
    }

    private void preencherCampos(EquipamentoAluguel equipAlug) {
        nomeEquip.setText(equipAlug.getNome());
        qtdEquip.setText(String.valueOf(equipAlug.getQtdAluguel()));
        precoAluguelM.setText(String.valueOf(equipAlug.getPrecoAluguelM()));
        precoAluguelI.setText(String.valueOf(equipAlug.getPrecoAluguelI()));

    }

    private void salvarEquip() {
        if (verificarCampos()) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        EquipamentoAluguel equipAlug = new EquipamentoAluguel();
        equipAlug.setNome(nomeEquip.getText().toString());
        equipAlug.setQtdAluguel(Integer.parseInt(qtdEquip.getText().toString()));
        // Format the prices with 2 decimal places
        float precoAluguelMFloat = Float.parseFloat(precoAluguelM.getText().toString().replace(",", "."));
        float precoAluguelIFloat = Float.parseFloat(precoAluguelI.getText().toString().replace(",", "."));

        String precoAluguelMFormatted = String.format("%.2f", precoAluguelMFloat);
        String precoAluguelIFormatted = String.format("%.2f", precoAluguelIFloat);

        equipAlug.setPrecoAluguelM(Float.parseFloat(precoAluguelMFormatted));
        equipAlug.setPrecoAluguelI(Float.parseFloat(precoAluguelIFormatted));

        Intent intent = getIntent();
        if (intent.hasExtra("equipAlug")) {
            EquipamentoAluguel equipAlugExistente = (EquipamentoAluguel) intent.getSerializableExtra("equipAlug");
            firestore.collection("EquipamentoAluguel").document(equipAlugExistente.getId()).set(equipAlug)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), "Equipamento atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                        voltarParaLista();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Erro ao atualizar Equipamento.", Toast.LENGTH_SHORT).show();
                        Log.e("CadastroEquipAlug", "Erro: ");
                    });
        } else {
            firestore.collection("EquipamentoAluguel").add(equipAlug)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Equipamento salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        limparCampos();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Erro ao salvar Equipamento.", Toast.LENGTH_SHORT).show();
                        Log.e("CadastroProduto", "Erro: ", e);
                    });
        }
    }

    public void voltarParaLista() {
        Intent intent = new Intent(this, ListarEquipAlug.class);
        startActivity(intent);
        finish();
    }
}