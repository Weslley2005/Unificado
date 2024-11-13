package br.unigran.tcc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.unigran.tcc.ViewModel.FaturamentoMensal;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FaturamentoMensalTest {

    @Test
    public void testCalcularFaturamento() {
        try (ActivityScenario<FaturamentoMensal> scenario = ActivityScenario.launch(FaturamentoMensal.class)) {


            onView(withId(R.id.btn_calcular_faturamento)).perform(click());

            onView(withId(R.id.text_faturamento_total)).check(matches(withText("Faturamento Total:R$ 0.00")));
            onView(withId(R.id.text_desconto)).check(matches(withText("Desconto Total:R$ 0.00")));
            onView(withId(R.id.text_lucro_total)).check(matches(withText("Lucro Total:R$ 0.00")));
            onView(withId(R.id.text_faturamento_totalA)).check(matches(withText("Faturamento Total:R$ 0.00")));
            onView(withId(R.id.text_descontoA)).check(matches(withText("Desconto Total:R$ 0.00")));
            onView(withId(R.id.text_lucro_totalA)).check(matches(withText("Lucro Total:R$ 0.00")));
        }
    }

}
