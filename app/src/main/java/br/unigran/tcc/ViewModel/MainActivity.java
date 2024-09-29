package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import br.unigran.tcc.R;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private FirebaseAuth mAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout=findViewById(R.id.drawerLayout);
        navigationView= findViewById(R.id.navView);
        mAuth = FirebaseAuth.getInstance();
        toggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.fechar,R.string.abrir);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //:::::::::::::::::::::::::::::::::::::;;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));


        window.setNavigationBarColor(getResources().getColor(android.R.color.black));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    public void cadProduto(MenuItem item) {
        Intent intent = new Intent(this, CadastroProduto.class);
        startActivity(intent);
    }
    public void cadProdutoPP(MenuItem item) {
        Intent intent = new Intent(this, CadastroProdutoPP.class);
        startActivity(intent);
    }
    public void cadEquipAlug(MenuItem item) {
        Intent intent = new Intent(this, CadastroEquipAlug.class);
        startActivity(intent);
    }
    public void dadosUsuario(MenuItem item) {
        Intent intent = new Intent(this, DadosUsuario.class);
        startActivity(intent);
    }
    public void deslogar(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Logout")
                .setMessage("Você realmente deseja sair?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Não", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void listProdutos(MenuItem item) {
        Intent intent = new Intent(this, ListarProdutos.class);
        startActivity(intent);
    }
    public void listProdutoPP(MenuItem item) {
        Intent intent = new Intent(this, ListarProdutoPP.class);
        startActivity(intent);
    }
    public void listEquipAlug(MenuItem item) {
        Intent intent = new Intent(this, ListarEquipAlug.class);
        startActivity(intent);
    }

    public void vendas(View view) {
        Intent intent = new Intent(this, ListarPVAlimentos.class);
        startActivity(intent);
    }
    public void carrinho(View view) {
        Intent intent = new Intent(this, TipoCarrinho.class);
        startActivity(intent);
    }
    public void Aluguel(View view) {
        Intent intent = new Intent(this, Aluguel.class);
        startActivity(intent);
    }
    public void finalizarAluguel(View view) {
        Intent intent = new Intent(this, FinalizarAluguel.class);
        startActivity(intent);
    }
}
