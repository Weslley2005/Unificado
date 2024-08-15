package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        // Deixar a barra inferior (navigation bar) preta
        window.setNavigationBarColor(getResources().getColor(android.R.color.black));

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
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}
