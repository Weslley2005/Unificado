package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import br.unigran.tcc.Model.ProdutoPP;
import br.unigran.tcc.R;

public class CadastroProdutoPP extends AppCompatActivity {

    private EditText nome;
    private EditText precoVenda;
    private Spinner tipo;
    private Button cadastrarProdutoPP;
    private ProdutoPP pp;
    private TextView nomeError, precoVendaError, tipoError;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto_pp);

        nome = findViewById(R.id.idNomeProdutoPP);
        precoVenda = findViewById(R.id.idPrecoVendaPP);
        tipo = findViewById(R.id.idTipoProdutoPP);
        cadastrarProdutoPP = findViewById(R.id.idCadastrarProdutoPP);
        nomeError = findViewById(R.id.nomeError);
        precoVendaError = findViewById(R.id.precoVendaError);
        tipoError = findViewById(R.id.tipoError);

        cadastrarProdutoPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarProdutoPP();
            }
        });

        ArrayList<String> strings = new ArrayList<>();
        strings.add("Selecione um tipo");
        strings.add("Alimento");
        strings.add("Bebida");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(spinnerAdapter);

        adicionarTextWatchers();

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        // Deixar a barra inferior (navigation bar) preta
        window.setNavigationBarColor(getResources().getColor(android.R.color.black));
    }

    private void adicionarTextWatchers() {
        nome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    nome.setBackgroundResource(android.R.drawable.edit_text);
                    nomeError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        precoVenda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    precoVenda.setBackgroundResource(android.R.drawable.edit_text);
                    precoVendaError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    tipo.setBackgroundResource(android.R.drawable.edit_text);
                    tipoError.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private boolean camposVazios() {
        boolean vazio = false;
        if (nome.getText().toString().isEmpty()) {
            nome.setBackgroundResource(R.drawable.borda_vermelha);
            nomeError.setText("Campo obrigatório!");
            nomeError.setVisibility(View.VISIBLE);
            vazio = true;
        }
        if (precoVenda.getText().toString().isEmpty()) {
            precoVenda.setBackgroundResource(R.drawable.borda_vermelha);
            precoVendaError.setText("Campo obrigatório!");
            precoVendaError.setVisibility(View.VISIBLE);
            vazio = true;
        }
        if (tipo.getSelectedItemPosition() == 0) {
            tipo.setBackgroundResource(R.drawable.borda_vermelha);
            tipoError.setText("Campo obrigatório!");
            tipoError.setVisibility(View.VISIBLE);
            vazio = true;
        }
        return vazio;
    }

    private void salvarProdutoPP() {
        if (camposVazios()) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProdutoPP pp = new ProdutoPP();
        pp.setNome(nome.getText().toString());
        pp.setPrecoVenda(Float.valueOf(precoVenda.getText().toString().replace(",", ".")));
        pp.setTipoProdutoPP(tipo.getSelectedItem().toString());
        db.collection("ProdutosPP").add(pp)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "PRODUTO SALVO!", Toast.LENGTH_SHORT).show();
                        limpaCampos();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERRO AO SALVAR PRODUTO", Toast.LENGTH_SHORT).show();
                        Log.e("CadastroProduto", "Erro: ", e);
                    }
                });
    }

    public void limpaCampos() {
        nome.setText("");
        precoVenda.setText("");
        tipo.setSelection(0);
    }
}