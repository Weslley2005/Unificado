package br.unigran.tcc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.SystemClock;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.unigran.tcc.ViewModel.CadastroUsuario;

@RunWith(AndroidJUnit4.class)
public class CadastroUsuarioTest {

    @Rule
    public ActivityScenarioRule<CadastroUsuario> activityScenarioRule =
            new ActivityScenarioRule<>(CadastroUsuario.class);

    @Before
    public void setUp() {
    }

    @Test
    public void testCadastroUsuario() {
        onView(withId(R.id.idNome)).perform(replaceText("João Silva"));
        onView(withId(R.id.idCpf)).perform(replaceText("07336443176"));
        onView(withId(R.id.idTelefone)).perform(replaceText("(12) 34567-8901"));
        onView(withId(R.id.idEstado)).perform(replaceText("SP"));
        onView(withId(R.id.idCidade)).perform(replaceText("São Paulo"));
        onView(withId(R.id.idBairro)).perform(replaceText("Centro"));
        onView(withId(R.id.idRua)).perform(scrollTo(), closeSoftKeyboard(), click(), replaceText("Rua ABC"));
        onView(withId(R.id.idNumero)).perform(scrollTo(), replaceText("123"));
        onView(withId(R.id.idCadEmail)).perform(scrollTo(), click(), replaceText("joao@gmail.com"));

        onView(withId(R.id.idCadEmail)).perform(closeSoftKeyboard());
        onView(withId(R.id.idcadSenha)).perform(replaceText("123456789"));

        onView(withId(R.id.idConfSenha)).perform(scrollTo(), replaceText("123456789"));

        onView(withId(R.id.idCadastrar)).perform(click());


    }


   @Test
    public void testCamposVazios() {
        Log.d("Test", "Rolando até o botão de cadastro...");
        onView(withId(R.id.idCadastrar))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        Log.d("Test", "Botão 'Cadastrar' visível após rolar");

        SystemClock.sleep(500);

        Log.d("Test", "Clicando no botão de cadastro...");
        onView(withId(R.id.idCadastrar)).perform(scrollTo(), click());

    }


}
