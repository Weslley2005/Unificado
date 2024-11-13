package br.unigran.tcc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.unigran.tcc.ViewModel.CadastroEquipAlug;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CadastroEquipAlugTest {

    private FirebaseFirestore firestore;

    @Before
    public void setUp() {
        firestore = FirebaseFirestore.getInstance();
    }

    @Test
    public void testSalvarEquipamento() {
        ActivityScenario.launch(CadastroEquipAlug.class);

        onView(withId(R.id.idNomeEqui)).perform(typeText("Equipamento Teste"));
        onView(withId(R.id.idQtdEqui)).perform(typeText("10"));
        onView(withId(R.id.idPrecoEquiM)).perform(typeText("50,00"));
        onView(withId(R.id.idPrecoEquiM)).perform(closeSoftKeyboard());
        onView(withId(R.id.idPrecoEquiI)).perform(click(), typeText("100"));
        onView(withId(R.id.idPrecoEquiM)).perform(closeSoftKeyboard());


        onView(withId(R.id.nomeError)).check(matches(withText("")));
        onView(withId(R.id.quantidadeError)).check(matches(withText("")));
        onView(withId(R.id.precoAlugMError)).check(matches(withText("")));
        onView(withId(R.id.precoAlugIError)).check(matches(withText("")));

        onView(withId(R.id.idCadastrarEqui)).perform(click());


        firestore.collection("EquipamentoAluguel")
                .whereEqualTo("nome", "Equipamento Teste")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    assert queryDocumentSnapshots.size() > 0;
                });
    }

    @Test
    public void testCamposObrigatorios() {
        ActivityScenario.launch(CadastroEquipAlug.class);

        onView(withId(R.id.idCadastrarEqui)).perform(click());

        onView(withId(R.id.nomeError)).check(matches(withText("Campo obrigatório!")));
        onView(withId(R.id.quantidadeError)).check(matches(withText("Campo obrigatório!")));
        onView(withId(R.id.precoAlugMError)).check(matches(withText("Campo obrigatório!")));
        onView(withId(R.id.precoAlugIError)).check(matches(withText("Campo obrigatório!")));
    }


}
