package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import br.unigran.tcc.Model.ProdutoPP;
import br.unigran.tcc.Model.Produtos;
import br.unigran.tcc.Model.TipoProdutos;
import br.unigran.tcc.R;

public class CadastroProdutoPP extends AppCompatActivity {

    private EditText nome;
    private EditText precoVenda;
    private Spinner tipo;
    private Button cadastrarProduto;
    private ProdutoPP pp;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto_pp);

        nome = findViewById(R.id.idNomeProdutoPP);
        precoVenda = findViewById(R.id.idPrecoVendaPP);
        tipo = findViewById(R.id.idTipoProdutoPP);
        cadastrarProduto = findViewById(R.id.idCadastrarProdutoPP);

        cadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarProdutoPP();
            }
        });
    }

    private boolean camposVazios() {
        return nome.getText().toString().isEmpty() ||  precoVenda.getText().toString().isEmpty() || tipo.getSelectedItem().toString().isEmpty() ;
    }

    private void salvarProdutoPP() {
        Produtos p = new Produtos();
        p.setNome(nome.getText().toString());
        p.setPrecoVenda(Double.valueOf(precoVenda.getText().toString()));
        p.setTipo(TipoProdutos.valueOf(tipo.getSelectedItem().toString()));

        db.collection("ProdutoPP").add(p)
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