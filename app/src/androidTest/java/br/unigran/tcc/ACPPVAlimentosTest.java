package br.unigran.tcc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import br.unigran.tcc.ViewModel.ACPPVAlimentos;

@RunWith(AndroidJUnit4.class)
public class ACPPVAlimentosTest {

    @Rule
    public ActivityScenarioRule<ACPPVAlimentos> activityRule =
            new ActivityScenarioRule<>(ACPPVAlimentos.class);
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Test
    public void testAdicionarAoCarrinho() {
        Map<String, Object> produto = new HashMap<>();
        produto.put("nome", "Produto Teste");
        produto.put("precoVenda", 10.0);
        produto.put("quantidade", 10);

        db.collection("ProdutosPP")
                .document("produtoTest")
                .set(produto)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activityRule.getScenario().onActivity(activity -> {
                            activity.qtdParaVenda.setText("3");
                        });

                        onView(withId(R.id.btnAdicionarCarrinho))
                                .perform(click());

                    }
                });
    }
}

