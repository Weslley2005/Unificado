package br.unigran.tcc;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.unigran.tcc.ViewModel.ListarProdutos;

@RunWith(AndroidJUnit4.class)
public class ListarProdutosTest {

    @Rule
    public ActivityScenarioRule<ListarProdutos> activityScenarioRule =
            new ActivityScenarioRule<>(ListarProdutos.class);


    @Test
    public void testFiltroProdutoNaBusca() {
        onView(withId(R.id.searchView))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        Log.d("Test", "SearchView visível após rolar");


        Log.d("Test", "Clicando no ícone de busca...");
        onView(withContentDescription("Search"))
                .perform(click());
        Log.d("Test", "Ícone de busca clicado");

        Log.d("Test", "Expandindo o SearchView...");
        onView(withId(R.id.searchView)).perform(scrollTo(), click());
        Log.d("Test", "SearchView expandido");

        Log.d("Test", "Digitando 'Produto Teste' no SearchView...");
        onView(allOf(withId(androidx.appcompat.R.id.search_src_text), isDisplayed()))
                .perform(typeText("Coca Cola 250ml"));
        closeSoftKeyboard();
        Log.d("Test", "Texto 'Produto Teste' digitado e teclado fechado");

        Log.d("Test", "Verificando se o RecyclerView está exibido...");
        onView(withId(R.id.recyclerViewProduto)).check(matches(isDisplayed()));
        Log.d("Test", "RecyclerView exibido com sucesso");

        Log.d("Test", "Verificando se o item 'Produto Teste' está visível...");
        onView(withText("Coca Cola 250ml")).check(matches(isDisplayed()));
        Log.d("Test", "'Produto Teste' encontrado e visível");
    }





}
