package br.unigran.tcc.ViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import br.unigran.tcc.R;
import br.unigran.tcc.ViewModel.FinalizarAluguel;
import br.unigran.tcc.ViewModel.FinalizarVendas;

public class TipoComandas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_comandas);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));


        window.setNavigationBarColor(getResources().getColor(android.R.color.black));
    }

    public void vendasComanda(View view) {
        Intent intent = new Intent(this, FinalizarVendas.class);
        startActivity(intent);
    }
    public void aluguelComanda(View view) {
        Intent intent = new Intent(this, FinalizarAluguel.class);
        startActivity(intent);
    }
}