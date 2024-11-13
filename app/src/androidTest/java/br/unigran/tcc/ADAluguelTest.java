package br.unigran.tcc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import br.unigran.tcc.ViewModel.ADAluguel;

public class ADAluguelTest {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ActivityScenario<ADAluguel> scenario;

    @Before
    public void setUp() throws Exception {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Test
    public void testCarregarDadosDoBanco() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
        }

        scenario = ActivityScenario.launch(ADAluguel.class);

        Map<String, Object> produto = new HashMap<>();
        produto.put("nome", "Produto Teste");
        produto.put("precoAluguelM", 10.0);
        produto.put("precoAluguelI", 15.0);
        produto.put("quantidade", 10);

        db.collection("EquipamentoAluguel")
                .document("produtoTest")
                .set(produto)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onView(withId(R.id.textNomeProduto))
                                .check(matches(withText("Produto Teste")));
                        onView(withId(R.id.textPrecoAluguelM))
                                .check(matches(withText("Preço de Aluguel M: R$10.00")));
                        onView(withId(R.id.textPrecoAluguelI))
                                .check(matches(withText("Preço de Aluguel I: R$15.00")));
                    }
                });
    }

    @Test
    public void testAdicionarAoCarrinho() {
        Map<String, Object> produto = new HashMap<>();
        produto.put("nome", "Produto Teste");
        produto.put("precoAluguelM", 10.0);
        produto.put("precoAluguelI", 15.0);
        produto.put("quantidade", 10);

        db.collection("EquipamentoAluguel")
                .document("produtoTest")
                .set(produto)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        scenario.onActivity(activity -> {
                            activity.qtdParaAluguel.setText("3");
                            activity.switchTipoAluguel.setChecked(true);
                        });

                        onView(withId(R.id.btnAdicionarCarrinho))
                                .perform(click());

                    }
                });
    }

}
