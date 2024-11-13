package br.unigran.tcc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.unigran.tcc.ViewModel.FaturamentoDiario;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FaturamentoDiarioTest {

    @Rule
    public ActivityScenarioRule<FaturamentoDiario> activityScenarioRule =
            new ActivityScenarioRule<>(FaturamentoDiario.class);

    @Test
    public void testCalculoFaturamento() {
        onView(withId(R.id.btn_calcular_faturamento)).perform(ViewActions.click());

        onView(withId(R.id.text_faturamento_total)).check(matches(withText(containsString("Faturamento Total"))));
        onView(withId(R.id.text_desconto)).check(matches(withText(containsString("Desconto Total"))));
        onView(withId(R.id.text_lucro_total)).check(matches(withText(containsString("Lucro Total"))));

        onView(withId(R.id.text_faturamento_total)).check(matches(withText(containsString("R$ 0.00"))));
        onView(withId(R.id.text_desconto)).check(matches(withText(containsString("R$ 0.00"))));
        onView(withId(R.id.text_lucro_total)).check(matches(withText(containsString("R$ 0.00"))));
    }

}
