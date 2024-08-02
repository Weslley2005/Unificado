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

import br.unigran.tcc.Model.EquipamentoAluguel;
import br.unigran.tcc.Model.Produtos;
import br.unigran.tcc.Model.TipoProdutos;
import br.unigran.tcc.R;

public class CadastroEquipAlug extends AppCompatActivity {

    private EditText nome;
    private EditText quantidade;
    private EditText precoAluguel;
    private Spinner tipo;
    private Button cadastrarProduto;
    private EquipamentoAluguel ea;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_equip_alug);

        nome = findViewById(R.id.idNomeEqui);
        quantidade = findViewById(R.id.idQtdEqui);
        precoAluguel = findViewById(R.id.idPrecoEqui);
        tipo = findViewById(R.id.idTipoEqui);
        cadastrarProduto = findViewById(R.id.idCadastrarEqui);

        cadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarProduto();
            }
        });
    }

    private boolean camposVazios() {
        return nome.getText().toString().isEmpty() || quantidade.getText().toString().isEmpty() || precoAluguel.getText().toString().isEmpty()  || tipo.getSelectedItem().toString().isEmpty() ;
    }

    private void salvarProduto() {
        Produtos p = new Produtos();
        p.setNome(nome.getText().toString());
        p.setQtdProduto(Integer.valueOf(quantidade.getText().toString()));
        p.setProcoCompra(Double.valueOf(precoAluguel.getText().toString()));
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
        precoAluguel.setText("");
        tipo.setSelection(0);
    }
}