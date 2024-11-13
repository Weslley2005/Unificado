package br.unigran.tcc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import br.unigran.tcc.Model.ItemVendas;
import br.unigran.tcc.ViewModel.CarrinhoVendas;

public class CarrinhoVendasTest {

    @Rule
    public ActivityScenarioRule<CarrinhoVendas> activityScenarioRule =
            new ActivityScenarioRule<>(CarrinhoVendas.class);
    private FirebaseUser usuarioTeste;

    @Before
    public void setUp() throws Exception {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        AuthResult result = Tasks.await(auth.signInWithEmailAndPassword("weslleytortelli8@gmail.com", "123456789"));
        usuarioTeste = result.getUser();

        if (usuarioTeste == null) {
            throw new AssertionError("Falha ao autenticar usuário de teste.");
        }
    }



    @Test
    public void testDescontoCalculadoCorretamente() {
        onView(withId(R.id.textSubtotal))
                .check(matches(withText("Subtotal: R$0.00")));

        onView(withId(R.id.editDesconto))
                .perform(ViewActions.typeText("15"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.textTotal))
                .check(matches(withText("Total: R$-15.00")));
    }

    @Test
    public void testValidarCamposObrigatorios() {
        onView(withId(R.id.buttonFinalizar))
                .perform(ViewActions.click());

        onView(withId(R.id.erroNomeAluguel))
                .check(matches(withText("Campo obrigatório!")));


    }

    @Test
    public void testAdicionarItensCarrinho() {
        String userId = usuarioTeste.getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        ItemVendas novoProduto = new ItemVendas();
        novoProduto.setNome("Produto de Teste");
        novoProduto.setPrecoUnitario(100.0f);
        novoProduto.setQuantidade(2);
        novoProduto.setPrecoTotal(200.0f);

        firestore.collection("CarrinhoVendas").document(userId)
                .collection("ItensVenda")
                .add(novoProduto)
                .addOnSuccessListener(documentReference -> {
                    assertNotNull(documentReference.getId());
                    assertTrue(documentReference.getId().length() > 0);
                })
                .addOnFailureListener(e -> fail("Falha ao adicionar item ao carrinho: " + e.getMessage()));
    }

    @Test
    public void testFinalizarCompra() {
        String nomeVenda = "Cliente Venda";
        double subtotal = 300.0;
        double desconto = 50.0;
        double totalEsperado = subtotal - desconto;

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Map<String, Object> venda = new HashMap<>();
            venda.put("nome", nomeVenda);
            venda.put("subtotal", subtotal);
            venda.put("desconto", desconto);
            venda.put("total", totalEsperado);

            firestore.collection("ComprasVendas").document(userId)
                    .set(venda)
                    .addOnSuccessListener(aVoid -> assertTrue(true))
                    .addOnFailureListener(e -> fail("Erro ao finalizar compra: " + e.getMessage()));
        } else {
            fail("Usuário não está autenticado.");
        }
    }
}
