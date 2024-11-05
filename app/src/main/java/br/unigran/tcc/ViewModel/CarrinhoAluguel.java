package br.unigran.tcc.ViewModel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.unigran.tcc.Model.EquipamentoAluguel;
import br.unigran.tcc.R;

public class CarrinhoAluguel extends AppCompatActivity {

    private RecyclerView recyclerViewCarrinho;
    private CarrinhoAluguelAdapter carrinhoAdapter;
    private List<EquipamentoAluguel> listaCarrinho;
    private TextView textSubtotal;
    private TextView textTotal;
    private EditText editDesconto;
    private Button buttonFinalizar;
    private double subtotal = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho_aluguel);

        recyclerViewCarrinho = findViewById(R.id.recyclerViewCarrinhoAluguel);
        textSubtotal = findViewById(R.id.textSubtotal);
        textTotal = findViewById(R.id.textTotal);
        editDesconto = findViewById(R.id.editDesconto);
        buttonFinalizar = findViewById(R.id.buttonFinalizar);

        listaCarrinho = new ArrayList<>();
        carrinhoAdapter = new CarrinhoAluguelAdapter(listaCarrinho, this);
        recyclerViewCarrinho.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCarrinho.setAdapter(carrinhoAdapter);

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            FirebaseFirestore.getInstance().collection("CarrinhoAluguel").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nomeAluguel = documentSnapshot.getString("idNomeAluguel");
                            String telefoneAluguel = documentSnapshot.getString("idTelefoneAluguel");

                            if (nomeAluguel == null && telefoneAluguel == null) {
                                carregarCarrinho();
                            } else {
                                carregarCarrinhoEditar();
                            }
                        } else {
                            Log.e("CarrinhoActivity", "Documento não encontrado.");
                            carregarCarrinho();
                        }
                    })
                    .addOnFailureListener(e -> Log.e("CarrinhoActivity", "Erro ao carregar documento", e));
        } else {
            Toast.makeText(CarrinhoAluguel.this, "Você precisa estar logado para visualizar o carrinho.", Toast.LENGTH_SHORT).show();
        }

        buttonFinalizar.setOnClickListener(v -> finalizarCompra());

        editDesconto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarSubtotal();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Window janela = getWindow();
        janela.setStatusBarColor(getResources().getColor(android.R.color.black));
        janela.setNavigationBarColor(getResources().getColor(android.R.color.black));

        EditText editTelefoneAluguel = findViewById(R.id.idTelefoneAluguel);
        editTelefoneAluguel.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFormatting) return;

                isFormatting = true;

                String digits = s.toString().replaceAll("\\D", "");
                StringBuilder formatted = new StringBuilder();
                if (digits.length() >= 2) {
                    formatted.append("(").append(digits.substring(0, 2)).append(") ");
                    if (digits.length() >= 7) {
                        formatted.append(digits.substring(2, 7)).append("-").append(digits.substring(7, Math.min(11, digits.length())));
                    } else if (digits.length() > 2) {
                        formatted.append(digits.substring(2));
                    }
                } else if (digits.length() > 0) {
                    formatted.append("(").append(digits);
                }

                editTelefoneAluguel.setText(formatted.toString());
                editTelefoneAluguel.setSelection(formatted.length());

                isFormatting = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }

    private void carregarCarrinho() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            FirebaseFirestore.getInstance().collection("CarrinhoAluguel").document(userId)
                    .collection("ItensAluguel")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            listaCarrinho.clear();
                            subtotal = 0.0;
                            for (QueryDocumentSnapshot documento : task.getResult()) {
                                EquipamentoAluguel equipAlug = new EquipamentoAluguel();
                                equipAlug.setId(documento.getId());
                                equipAlug.setNome(documento.getString("nome"));
                                equipAlug.setPrecoAluguelM(documento.getDouble("precoAluguelM").floatValue());
                                equipAlug.setPrecoAluguelI(documento.getDouble("precoAluguelI").floatValue());
                                equipAlug.setTotalPreco(documento.getDouble("precoTotal").floatValue());
                                equipAlug.setQtdAluguel(documento.getLong("quantidade").intValue());
                                equipAlug.setTipoAluguel(documento.getBoolean("tipoAluguel"));

                                listaCarrinho.add(equipAlug);
                                subtotal += equipAlug.getTotalPreco();
                            }
                            carrinhoAdapter.notifyDataSetChanged();
                            atualizarSubtotal();
                        } else {
                            Log.e("CarrinhoActivity", "Erro ao carregar carrinho", task.getException());
                        }
                    });
        } else {
            Toast.makeText(CarrinhoAluguel.this, "Você precisa estar logado para visualizar o carrinho.", Toast.LENGTH_SHORT).show();
        }
    }

    void carregarCarrinhoEditar() {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("CarrinhoAluguel").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nomeAluguel = documentSnapshot.getString("idNomenAluguel");
                            String telefoneAluguel = documentSnapshot.getString("idTelefoneAluguel");

                            EditText editNomeAluguel = findViewById(R.id.idNomeAluguel);
                            EditText editTelefoneAluguel = findViewById(R.id.idTelefoneAluguel);

                            editNomeAluguel.setText(nomeAluguel);
                            editTelefoneAluguel.setText(telefoneAluguel);
                        } else {
                            Log.e("CarrinhoActivity", "Documento não encontrado para carregar nome e telefone.");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("CarrinhoActivity", "Erro ao carregar nome e telefone", e));

            firestore.collection("CarrinhoAluguel").document(userId)
                    .collection("ItensAluguel")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            listaCarrinho.clear();
                            subtotal = 0.0;
                            for (QueryDocumentSnapshot documento : task.getResult()) {
                                EquipamentoAluguel equipAlug = new EquipamentoAluguel();
                                equipAlug.setId(documento.getId());
                                equipAlug.setNome(documento.getString("nome"));
                                equipAlug.setPrecoAluguelM(documento.getDouble("precoAluguelM").floatValue());
                                equipAlug.setPrecoAluguelI(documento.getDouble("precoAluguelI").floatValue());
                                equipAlug.setTotalPreco(documento.getDouble("precoTotal").floatValue());
                                equipAlug.setQtdAluguel(documento.getLong("quantidade").intValue());
                                equipAlug.setTipoAluguel(documento.getBoolean("tipoAluguel"));

                                listaCarrinho.add(equipAlug);
                                subtotal += equipAlug.getTotalPreco();
                            }
                            carrinhoAdapter.notifyDataSetChanged();
                            atualizarSubtotal();
                        } else {
                            Log.e("CarrinhoActivity", "Erro ao carregar carrinho", task.getException());
                        }
                    });
        } else {
            Toast.makeText(CarrinhoAluguel.this, "Você precisa estar logado para visualizar o carrinho.", Toast.LENGTH_SHORT).show();
        }
    }



    private void atualizarSubtotal() {
        textSubtotal.setText(String.format("Subtotal: R$%.2f", subtotal));

        String descontoStr = editDesconto.getText().toString();
        double desconto = 0.0;

        if (!descontoStr.isEmpty()) {
            try {
                desconto = Double.parseDouble(descontoStr);
            } catch (NumberFormatException e) {
                desconto = 0.0;
            }
        }

        double total = subtotal - desconto;
        textTotal.setText(String.format("Total: R$%.2f", total));
    }


    private void finalizarCompra() {
        EditText editNomeAluguel = findViewById(R.id.idNomeAluguel);
        EditText editTelefoneAluguel = findViewById(R.id.idTelefoneAluguel);
        TextView erroNomeAluguel = findViewById(R.id.erroNomeAluguel);
        TextView erroTelefoneAluguel = findViewById(R.id.erroTelefoneAluguel);

        String nomeAluguel = editNomeAluguel.getText().toString().trim();
        String telefoneAluguel = editTelefoneAluguel.getText().toString().trim();

        boolean camposVazios = false;

        if (nomeAluguel.isEmpty()) {
            editNomeAluguel.setBackgroundResource(R.drawable.borda_vermelha);
            erroNomeAluguel.setText("Campo obrigatório!");
            erroNomeAluguel.setVisibility(View.VISIBLE);
            camposVazios = true;
        } else {
            editNomeAluguel.setBackgroundResource(0);
            erroNomeAluguel.setVisibility(View.GONE);
        }

        if (telefoneAluguel.isEmpty()) {
            editTelefoneAluguel.setBackgroundResource(R.drawable.borda_vermelha);
            erroTelefoneAluguel.setText("Campo obrigatório!");
            erroTelefoneAluguel.setVisibility(View.VISIBLE);
            camposVazios = true;
        } else if (!telefoneAluguel.matches("\\(\\d{2}\\) \\d{5}-\\d{4}")) {
            editTelefoneAluguel.setBackgroundResource(R.drawable.borda_vermelha);
            erroTelefoneAluguel.setText("Formato inválido! Use (XX) XXXXX-XXXX.");
            erroTelefoneAluguel.setVisibility(View.VISIBLE);
            camposVazios = true;
        } else {
            editTelefoneAluguel.setBackgroundResource(0);
            erroTelefoneAluguel.setVisibility(View.GONE);
        }

        if (camposVazios) {
            return;
        }

        String descontoStr = editDesconto.getText().toString();
        double desconto = 0.0;

        if (!descontoStr.isEmpty()) {
            try {
                desconto = Double.parseDouble(descontoStr);
            } catch (NumberFormatException e) {
                desconto = 0.0;
            }
        }

        double total = subtotal - desconto;

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String dataFormatada = dateFormat.format(new Date());
            String horaFormatada = timeFormat.format(new Date());

            Map<String, Object> compra = new HashMap<>();
            compra.put("usuarioId", userId);
            compra.put("subtotal", subtotal);
            compra.put("desconto", desconto);
            compra.put("total", total);
            compra.put("data", dataFormatada);
            compra.put("hora", horaFormatada);
            compra.put("idNomenAluguel", nomeAluguel);
            compra.put("idTelefoneAluguel", telefoneAluguel);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference comprasRef = firestore.collection("AluguelFinalizadas");

            comprasRef.add(compra)
                    .addOnSuccessListener(documentReference -> {
                        String compraId = documentReference.getId();

                        CollectionReference itensRef = firestore.collection("AluguelFinalizadas").document(compraId).collection("ItensAluguel");
                        List<Map<String, Object>> itens = new ArrayList<>();
                        for (EquipamentoAluguel equipAlug : listaCarrinho) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("nome", equipAlug.getNome());
                            item.put("precoAluguelI", equipAlug.getPrecoAluguelI());
                            item.put("precoAluguelM", equipAlug.getPrecoAluguelM());
                            item.put("quantidade", equipAlug.getQtdAluguel());
                            item.put("precoTotal", equipAlug.getTotalPreco());
                            item.put("tipoAluguel", equipAlug.isTipoAluguel());
                            itens.add(item);
                        }

                        for (Map<String, Object> item : itens) {
                            itensRef.add(item)
                                    .addOnFailureListener(e -> {
                                        Log.e("CarrinhoActivity", "Erro ao adicionar item à compra", e);
                                        Toast.makeText(CarrinhoAluguel.this, "Erro ao finalizar compra", Toast.LENGTH_SHORT).show();
                                    });
                        }

                        for (EquipamentoAluguel equipAlug : listaCarrinho) {
                            Log.d("CarrinhoActivity", "Quantidade no carrinho: " + equipAlug.getQtdAluguel());
                            atualizarEstoque(equipAlug);
                        }

                        limparCarrinho(userId);
                        Toast.makeText(CarrinhoAluguel.this, String.format("Compra finalizada com sucesso! Total: R$%.2f", total), Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CarrinhoActivity", "Erro ao salvar compra", e);
                        Toast.makeText(CarrinhoAluguel.this, "Erro ao finalizar compra", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(CarrinhoAluguel.this, "Você precisa estar logado para finalizar a compra.", Toast.LENGTH_SHORT).show();
        }
    }



    private void atualizarEstoque(EquipamentoAluguel equipAlug) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("EquipamentoAluguel")
                .whereEqualTo("nome", equipAlug.getNome())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        int estoqueAtual = document.getLong("qtdAluguel").intValue();

                        Log.d("CarrinhoActivity", "Estoque atual do produto '" + equipAlug.getNome() + "': " + estoqueAtual);

                        int novaQuantidadeEstoque = estoqueAtual - equipAlug.getQtdAluguel();

                        Log.d("CarrinhoActivity", "Nova quantidade de estoque após subtração: " + novaQuantidadeEstoque);

                        document.getReference().update("qtdAluguel", novaQuantidadeEstoque)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("CarrinhoActivity", "Estoque atualizado com sucesso!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("CarrinhoActivity", "Erro ao atualizar estoque", e);
                                });
                    } else {
                        Log.e("CarrinhoActivity", "Produto não encontrado para atualizar estoque");
                    }
                });
    }




    private void limparCarrinho(String userId) {
        DocumentReference carrinhoRef = FirebaseFirestore.getInstance().collection("CarrinhoAluguel").document(userId);

        carrinhoRef.collection("ItensAluguel").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documento : task.getResult()) {
                            documento.getReference().delete();
                        }
                        listaCarrinho.clear();
                        carrinhoAdapter.notifyDataSetChanged();
                        subtotal = 0.0;
                        atualizarSubtotal();

                        carrinhoRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("CarrinhoActivity", "Carrinho limpo com sucesso!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("CarrinhoActivity", "Erro ao deletar o carrinho", e);
                                });
                    } else {
                        Log.e("CarrinhoActivity", "Erro ao limpar carrinho", task.getException());
                    }
                });
    }

    public void mostrarDialogoDeConfirmacao(int posicao, EquipamentoAluguel equipAlug) {
        AlertDialog.Builder construtor = new AlertDialog.Builder(this);
        construtor.setTitle("Confirmação");
        construtor.setMessage("Você tem certeza que deseja deletar este item?");

        construtor.setPositiveButton("Sim", (dialog, which) -> deletarItem(posicao, equipAlug));

        construtor.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());

        AlertDialog alertaDialogo = construtor.create();
        alertaDialogo.show();
    }

    private void deletarItem(int posicao, EquipamentoAluguel equipAlug) {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            String userId = usuarioAtual.getUid();

            FirebaseFirestore.getInstance().collection("CarrinhoAluguel")
                    .document(userId)
                    .collection("ItensAluguel")
                    .document(equipAlug.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        listaCarrinho.remove(posicao);
                        carrinhoAdapter.notifyItemRemoved(posicao);
                        atualizarSubtotal();
                    })
                    .addOnFailureListener(e -> Log.e("CarrinhoAluguel", "Erro ao deletar item", e));
        } else {
            Log.e("CarrinhoAluguel", "Usuário não está logado.");
        }
    }

}