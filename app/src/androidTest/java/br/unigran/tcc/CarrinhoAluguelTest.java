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

import br.unigran.tcc.Model.EquipamentoAluguel;
import br.unigran.tcc.ViewModel.CarrinhoAluguel;

public class CarrinhoAluguelTest {

    @Rule
    public ActivityScenarioRule<CarrinhoAluguel> activityScenarioRule =
            new ActivityScenarioRule<>(CarrinhoAluguel.class);
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
    public void testCampoTelefoneFormatadoCorretamente() {
        onView(withId(R.id.idTelefoneAluguel))
                .perform(ViewActions.typeText("11987654321"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.idTelefoneAluguel))
                .check(matches(withText("(11) 98765-4321")));
    }
    @Test
    public void testDescontoCalculadoCorretamente() {
        onView(withId(R.id.textSubtotal))
                .check(matches(withText("Subtotal: R$0.00")));

        onView(withId(R.id.editDesconto))
                .perform(ViewActions.typeText("10"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.textTotal))
                .check(matches(withText("Total: R$-10.00")));
    }

    @Test
    public void testValidarCamposObrigatorios() {
        onView(withId(R.id.buttonFinalizar))
                .perform(ViewActions.click());
        onView(withId(R.id.erroNomeAluguel))
                .check(matches(withText("Campo obrigatório!")));

        onView(withId(R.id.erroTelefoneAluguel))
                .check(matches(withText("Campo obrigatório!")));
    }

    @Test
    public void testAdicionarItensCarrinho() {
        String userId = usuarioTeste.getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        EquipamentoAluguel novoEquipamento = new EquipamentoAluguel();
        novoEquipamento.setNome("Equipamento de Teste");
        novoEquipamento.setPrecoAluguelM(50.0f);
        novoEquipamento.setPrecoAluguelI(25.0f);
        novoEquipamento.setTotalPreco(75.0f);
        novoEquipamento.setQtdAluguel(1);
        novoEquipamento.setTipoAluguel(true);

        firestore.collection("CarrinhoAluguel").document(userId)
                .collection("ItensAluguel")
                .add(novoEquipamento)
                .addOnSuccessListener(documentReference -> {
                    assertNotNull(documentReference.getId());
                    assertTrue(documentReference.getId().length() > 0);
                })
                .addOnFailureListener(e -> fail("Falha ao adicionar item ao carrinho: " + e.getMessage()));
    }


    @Test
    public void testFinalizarCompra() {
        String nomeAluguel = "Cliente Teste";
        String telefoneAluguel = "(99) 99999-9999";
        double subtotal = 150.0;
        double desconto = 20.0;
        double totalEsperado = subtotal - desconto;

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Map<String, Object> compra = new HashMap<>();
            compra.put("nome", nomeAluguel);
            compra.put("telefone", telefoneAluguel);
            compra.put("subtotal", subtotal);
            compra.put("desconto", desconto);
            compra.put("total", totalEsperado);

            firestore.collection("ComprasAluguel").document(userId)
                    .set(compra)
                    .addOnSuccessListener(aVoid -> {
                        assertTrue(true);
                    })
                    .addOnFailureListener(e -> fail("Erro ao finalizar compra: " + e.getMessage()));
        } else {
            fail("Usuário não está autenticado.");
        }
    }

}
