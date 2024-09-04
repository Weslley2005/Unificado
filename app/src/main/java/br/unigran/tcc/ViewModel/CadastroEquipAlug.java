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

import br.unigran.tcc.Model.EquipamentoAluguel;
import br.unigran.tcc.R;

public class CadastroEquipAlug extends AppCompatActivity {

    private EditText nomeEquip;
    private EditText qtdEquip;
    private EditText precoAluguel;
    private Spinner tipoEquip;
    private Button botaoSalvar;
    private TextView erroNome, erroQuantidade, erroPrecoCompra, erroPrecoVenda, erroTipo, tituloEdicao;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_equip_alug);

        nomeEquip = findViewById(R.id.idNomeEqui);
        qtdEquip = findViewById(R.id.idQtdEqui);
        precoAluguel = findViewById(R.id.idPrecoEqui);
        tipoEquip = findViewById(R.id.idTipoEqui);
        botaoSalvar = findViewById(R.id.idCadastrarEqui);
        erroNome = findViewById(R.id.nomeError);
        erroQuantidade = findViewById(R.id.quantidadeError);
        erroPrecoVenda = findViewById(R.id.precoVendaError);
        erroTipo = findViewById(R.id.tipoError);
        tituloEdicao = findViewById(R.id.textView6);

        botaoSalvar.setOnClickListener(view -> salvarEquip());

        ArrayList<String> tiposProduto = new ArrayList<>();
        tiposProduto.add("Selecione um tipo");
        tiposProduto.add("Meia");
        tiposProduto.add("Inteira");

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposProduto);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoEquip.setAdapter(adapterSpinner);

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

        precoAluguel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    precoAluguel.setBackgroundResource(android.R.drawable.edit_text);
                    erroPrecoVenda.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        tipoEquip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    tipoEquip.setBackgroundResource(android.R.drawable.edit_text);
                    erroTipo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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
        if (precoAluguel.getText().toString().isEmpty()) {
            precoAluguel.setBackgroundResource(R.drawable.borda_vermelha);
            erroPrecoVenda.setText("Campo obrigatório!");
            erroPrecoVenda.setVisibility(View.VISIBLE);
            camposVazios = true;
        }
        if (tipoEquip.getSelectedItemPosition() == 0) {
            tipoEquip.setBackgroundResource(R.drawable.borda_vermelha);
            erroTipo.setText("Campo obrigatório!");
            erroTipo.setVisibility(View.VISIBLE);
            camposVazios = true;
        }
        return camposVazios;
    }

    public void limparCampos() {
        nomeEquip.setText("");
        qtdEquip.setText("");
        precoAluguel.setText("");
        tipoEquip.setSelection(0);
    }

    private void preencherCampos(EquipamentoAluguel equipAlug) {
        nomeEquip.setText(equipAlug.getNome());
        qtdEquip.setText(String.valueOf(equipAlug.getQtdAluguel()));
        precoAluguel.setText(String.valueOf(equipAlug.getPrecoAluguel()));

        ArrayAdapter<String> adapterSpinner = (ArrayAdapter<String>) tipoEquip.getAdapter();
        int posicaoSpinner = adapterSpinner.getPosition(equipAlug.getTipoAluguel());
        tipoEquip.setSelection(posicaoSpinner);
    }

    private void salvarEquip() {
        if (verificarCampos()) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        EquipamentoAluguel equipAlug = new EquipamentoAluguel();
        equipAlug.setNome(nomeEquip.getText().toString());
        equipAlug.setQtdAluguel(Integer.parseInt(qtdEquip.getText().toString()));
        equipAlug.setPrecoAluguel(Float.parseFloat(precoAluguel.getText().toString().replace(",", ".")));
        equipAlug.setTipoAluguel(tipoEquip.getSelectedItem().toString());

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