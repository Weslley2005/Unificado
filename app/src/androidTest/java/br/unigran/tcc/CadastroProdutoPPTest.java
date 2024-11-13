package br.unigran.tcc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.unigran.tcc.ViewModel.CadastroProdutoPP;

@RunWith(AndroidJUnit4.class)
public class CadastroProdutoPPTest {

    @Rule
    public ActivityScenarioRule<CadastroProdutoPP> activityScenarioRule =
            new ActivityScenarioRule<>(CadastroProdutoPP.class);

    private FirebaseFirestore firestore;

    @Before
    public void setUp() {
        firestore = FirebaseFirestore.getInstance();
    }

    @Test
    public void testCamposObrigatorios() {
        onView(withId(R.id.idCadastrarProdutoPP)).perform(ViewActions.click());

        onView(withId(R.id.nomeError)).check(ViewAssertions.matches(ViewMatchers.withText("Este campo é obrigatório!")));
        onView(withId(R.id.precoVendaError)).check(ViewAssertions.matches(ViewMatchers.withText("Este campo é obrigatório!")));
        onView(withId(R.id.tipoError)).check(ViewAssertions.matches(ViewMatchers.withText("Este campo é obrigatório!")));
    }

    @Test
    public void testSalvarProduto() {
        onView(withId(R.id.idNomeProdutoPP)).perform(ViewActions.typeText("Produto Teste"));
        onView(withId(R.id.idPrecoVendaPP)).perform(ViewActions.typeText("15.00"));
        onView(withId(R.id.idPrecoVendaPP)).perform(closeSoftKeyboard());
        onView(withId(R.id.idTipoProdutoPP)).perform(ViewActions.click());
        onView(withText("Alimento")).perform(ViewActions.click());

        onView(withId(R.id.idCadastrarProdutoPP)).perform(ViewActions.click());

        firestore.collection("ProdutosPP")
                .whereEqualTo("nome", "Produto Teste")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    assert queryDocumentSnapshots.size() > 0;
                });
    }


}
