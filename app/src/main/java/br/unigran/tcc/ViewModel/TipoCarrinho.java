package br.unigran.tcc.ViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import br.unigran.tcc.R;

public class TipoCarrinho extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_carrinho);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));


        window.setNavigationBarColor(getResources().getColor(android.R.color.black));
    }
    public void vendas(View view) {
        Intent intent = new Intent(this, CarrinhoVendas.class);
        startActivity(intent);
    }
    public void aluguel(View view) {
        Intent intent = new Intent(this, CarrinhoAluguel.class);
        startActivity(intent);
    }
}