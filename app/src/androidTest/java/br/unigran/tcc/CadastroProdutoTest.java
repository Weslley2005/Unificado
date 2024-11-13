package br.unigran.tcc;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.unigran.tcc.ViewModel.CadastroProduto;

@RunWith(AndroidJUnit4.class)
public class CadastroProdutoTest {

    @Rule
    public ActivityScenarioRule<CadastroProduto> activityScenarioRule =
            new ActivityScenarioRule<>(CadastroProduto.class);

    @Test
    public void testPreencherCampos() {
        onView(withId(R.id.idNomeProduto)).perform(typeText("Produto Teste"), closeSoftKeyboard());
        onView(withId(R.id.idQtdProduto)).perform(typeText("10"), closeSoftKeyboard());
        onView(withId(R.id.idPrecoCompra)).perform(typeText("5.00"), closeSoftKeyboard());
        onView(withId(R.id.idPrecoVenda)).perform(typeText("10.00"), closeSoftKeyboard());

        onView(withId(R.id.idNomeProduto)).check(matches(withText("Produto Teste")));
        onView(withId(R.id.idQtdProduto)).check(matches(withText("10")));
        onView(withId(R.id.idPrecoCompra)).check(matches(withText("5.00")));
        onView(withId(R.id.idPrecoVenda)).check(matches(withText("10.00")));

        onView(withId(R.id.idTipoProduto)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Alimento"))).perform(click());

        onView(withId(R.id.idCadastrarProduto)).perform(click());
    }

    @Test
    public void testSalvarProduto() {
        onView(withId(R.id.idNomeProduto)).perform(typeText("Produto Teste"), closeSoftKeyboard());
        onView(withId(R.id.idQtdProduto)).perform(typeText("10"), closeSoftKeyboard());
        onView(withId(R.id.idPrecoCompra)).perform(typeText("5.00"), closeSoftKeyboard());
        onView(withId(R.id.idPrecoVenda)).perform(typeText("10.00"), closeSoftKeyboard());
        onView(withId(R.id.idTipoProduto)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Alimento"))).perform(click());

        onView(withId(R.id.idCadastrarProduto)).perform(click());

    }
}
