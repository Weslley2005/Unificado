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

import br.unigran.tcc.Model.Produtos;
import br.unigran.tcc.Model.TipoProdutos;
import br.unigran.tcc.R;

public class CadastroProduto extends AppCompatActivity {

    private EditText nome;
    private EditText quantidade;
    private EditText precoCompra;
    private EditText precoVenda;
    private Spinner tipo;
    private Button cadastrarProduto;
    private Produtos p;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto);

        nome = findViewById(R.id.idNomeProduto);
        quantidade = findViewById(R.id.idQtdProduto);
        precoCompra = findViewById(R.id.idPrecoCompra);
        precoVenda = findViewById(R.id.idPrecoVenda);
        tipo = findViewById(R.id.idTipoProduto);
        cadastrarProduto = findViewById(R.id.idCadastrarProduto);

        cadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarProduto();
            }
        });
    }

    private boolean camposVazios() {
        return nome.getText().toString().isEmpty() || quantidade.getText().toString().isEmpty() || precoCompra.getText().toString().isEmpty() || precoVenda.getText().toString().isEmpty() || tipo.getSelectedItem().toString().isEmpty() ;
    }

    private void salvarProduto() {
        Produtos p = new Produtos();
        p.setNome(nome.getText().toString());
        p.setQtdProduto(Integer.valueOf(quantidade.getText().toString()));
        p.setProcoCompra(Double.valueOf(precoCompra.getText().toString()));
        p.setPrecoVenda(Double.valueOf(precoVenda.getText().toString()));
        p.setTipo(TipoProdutos.valueOf(tipo.getSelectedItem().toString()));

        db.collection("Produtos").add(p)
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
        quantidade.setText("");
        precoCompra.setText("");
        precoVenda.setText("");
        tipo.setSelection(0);
    }
}
