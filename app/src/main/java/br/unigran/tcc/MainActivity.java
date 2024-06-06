package br.unigran.tcc;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import br.unigran.tcc.Model.Usuario;

public class MainActivity extends AppCompatActivity {
    // Inicializa o Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Usuario usuario = new Usuario();
        usuario.setNome("Weslley");
        usuario.setEmail("exemplo@gmail");
        db.collection("Usuario").add(usuario);
    }
}
