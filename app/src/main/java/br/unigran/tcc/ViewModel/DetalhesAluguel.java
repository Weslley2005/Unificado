package br.unigran.tcc.ViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unigran.tcc.Model.AluguelFinalizado;
import br.unigran.tcc.Model.ItemAluguel;
import br.unigran.tcc.R;

public class DetalhesAluguel extends AppCompatActivity {
    private RecyclerView recyclerViewItensAlugados;
    private ItemAluguelAdapter itemAluguelAdapter;
    private List<ItemAluguel> listaItensAlugados;
    private Button buttonFinalizarAluguel;
    private FirebaseFirestore firestore;
    private ImageButton deletar, editar;
    private String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_aluguel);

        recyclerViewItensAlugados = findViewById(R.id.recycleViewItensAlugados);
        buttonFinalizarAluguel = findViewById(R.id.buttonFinalizarAluguel);
        deletar = findViewById(R.id.idDeletarAluguel);
        editar = findViewById(R.id.idEditarAluguel);
        listaItensAlugados = new ArrayList<>();
        itemAluguelAdapter = new ItemAluguelAdapter(listaItensAlugados);

        recyclerViewItensAlugados.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItensAlugados.setAdapter(itemAluguelAdapter);

        firestore = FirebaseFirestore.getInstance();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioId = usuarioAtual != null ? usuarioAtual.getUid() : null;

        String aluguelId = getIntent().getStringExtra("aluguelId");
        if (aluguelId != null && !aluguelId.isEmpty()) {
            carregarItensAlugados(aluguelId);
            configurarBotaoFinalizar(aluguelId);
        } else {
            Toast.makeText(this, "ID do aluguel inválido!", Toast.LENGTH_SHORT).show();
            finish();
        }

        deletar.setOnClickListener(view -> {
            mostrarDialogoDeConfirmacao(aluguelId);
        });

        editar.setOnClickListener(view -> {
            if (usuarioId != null) {
                editarAluguel(aluguelId, usuarioId);
            } else {
                Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            }
        });

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black));

        window.setNavigationBarColor(getResources().getColor(android.R.color.black));
    }

    private void carregarItensAlugados(String aluguelId) {
        CollectionReference itensRef = firestore.collection("AluguelFinalizadas")
                .document(aluguelId)
                .collection("ItensAluguel");

        itensRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                listaItensAlugados.clear();
                for (QueryDocumentSnapshot documento : task.getResult()) {
                    ItemAluguel itemAluguel = documento.toObject(ItemAluguel.class);
                    itemAluguel.setId(documento.getId());
                    listaItensAlugados.add(itemAluguel);
                }
                itemAluguelAdapter.notifyDataSetChanged();
            } else {
                Log.e("DetalhesAluguel", "Erro ao carregar itens alugados", task.getException());
                Toast.makeText(DetalhesAluguel.this, "Erro ao carregar itens!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarBotaoFinalizar(String aluguelId) {
        buttonFinalizarAluguel.setOnClickListener(v -> finalizarAluguel(aluguelId));
    }

    private void finalizarAluguel(String aluguelId) {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = usuarioAtual.getUid();

        int totalItens = listaItensAlugados.size();
        if (totalItens == 0) {
            Toast.makeText(this, "Nenhum item para devolver!", Toast.LENGTH_SHORT).show();
            return;
        }

        final int[] itensDevolvidos = {0};

        for (ItemAluguel item : listaItensAlugados) {
            devolverItem(item, () -> {
                itensDevolvidos[0]++;
                if (itensDevolvidos[0] == totalItens) {
                    firestore.collection("CarrinhoAluguel").document(userId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                salvarAluguelFinalizado(aluguelId, usuarioId);
                                Toast.makeText(this, "Aluguel finalizado com sucesso!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Erro ao finalizar aluguel", Toast.LENGTH_SHORT).show();
                                Log.e("DetalhesAluguel", "Erro ao finalizar aluguel", e);
                            });
                }
            });
        }
    }


    private void salvarAluguelFinalizado(String aluguelId, String usuarioId) {
        firestore.collection("AluguelFinalizadas").document(aluguelId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        AluguelFinalizado aluguelFinalizado = new AluguelFinalizado();

                        if (task.getResult().getDouble("subTotal") != null) {
                            aluguelFinalizado.setSubtotal(task.getResult().getDouble("subTotal"));
                        } else {
                            Log.e("DetalhesAluguel", "subTotal é nulo");
                            aluguelFinalizado.setSubtotal(0);
                        }

                        if (task.getResult().getDouble("desconto") != null) {
                            aluguelFinalizado.setDesconto(task.getResult().getDouble("desconto"));
                        } else {
                            Log.e("DetalhesAluguel", "desconto é nulo");
                            aluguelFinalizado.setDesconto(0.0);
                        }

                        if (task.getResult().getDouble("total") != null) {
                            aluguelFinalizado.setTotal(task.getResult().getDouble("total"));
                        } else {
                            Log.e("DetalhesAluguel", "total é nulo");
                            aluguelFinalizado.setTotal(0);
                        }

                        aluguelFinalizado.setData(task.getResult().getString("data"));
                        aluguelFinalizado.setHora(task.getResult().getString("hora"));
                        aluguelFinalizado.setIdNomenAluguel(task.getResult().getString("idNomenAluguel"));
                        aluguelFinalizado.setIdTelefoneAluguel(task.getResult().getString("idTelefoneAluguel"));

                        aluguelFinalizado.setUsuarioId(usuarioId);

                        firestore.collection("AlugFinaliz").document(aluguelId)
                                .set(aluguelFinalizado)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("DetalhesAluguel", "Aluguel finalizado salvo com sucesso!");

                                    salvarItensAluguel(aluguelId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("DetalhesAluguel", "Erro ao salvar aluguel finalizado", e);
                                });
                    } else {
                        Log.e("DetalhesAluguel", "Erro ao carregar dados do aluguel", task.getException());
                    }
                });
    }


    private void salvarItensAluguel(String aluguelId) {
        CollectionReference itensRef = firestore.collection("AluguelFinalizadas")
                .document(aluguelId)
                .collection("ItensAluguel");

        itensRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot documento : task.getResult()) {
                    ItemAluguel itemAluguel = documento.toObject(ItemAluguel.class);
                    itemAluguel.setId(documento.getId());

                    firestore.collection("AlugFinaliz").document(aluguelId)
                            .collection("ItensAluguel")
                            .document(itemAluguel.getId())
                            .set(itemAluguel)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("DetalhesAluguel", "Item alugado salvo com sucesso: " + itemAluguel.getId());
                                deletarColecaoAluguelFinalizadas(aluguelId);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("DetalhesAluguel", "Erro ao salvar item alugado", e);
                            });
                }
            } else {
                Log.e("DetalhesAluguel", "Erro ao carregar itens alugados", task.getException());
            }
        });
    }

    private void deletarColecaoAluguelFinalizadas(String aluguelId) {
        CollectionReference collectionRef = firestore.collection("AluguelFinalizadas").document(aluguelId).collection("ItensAluguel");

        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d("DetalhesAluguel", "Documento deletado com sucesso: " + document.getId());
                            })
                            .addOnFailureListener(e -> {
                                Log.e("DetalhesAluguel", "Erro ao deletar documento: " + document.getId(), e);
                            });
                }
                firestore.collection("AluguelFinalizadas").document(aluguelId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("DetalhesAluguel", "Coleção AluguelFinalizadas deletada com sucesso!");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("DetalhesAluguel", "Erro ao deletar a coleção AluguelFinalizadas", e);
                        });
            } else {
                Log.e("DetalhesAluguel", "Erro ao carregar documentos da coleção AluguelFinalizadas", task.getException());
            }
        });
    }

    private void devolverItem(ItemAluguel itemAluguel, Runnable onComplete) {
        firestore.collection("EquipamentoAluguel")
                .whereEqualTo("nome", itemAluguel.getNome())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        Long estoqueAtual = document.getLong("qtdAluguel");
                        if (estoqueAtual != null) {
                            int novaQuantidadeEstoque = estoqueAtual.intValue() + itemAluguel.getQuantidade();

                            Log.d("CarrinhoActivity", "Nova quantidade de estoque após adição: " + novaQuantidadeEstoque);

                            document.getReference().update("qtdAluguel", novaQuantidadeEstoque)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("CarrinhoActivity", "Estoque atualizado com sucesso!");
                                        onComplete.run();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("CarrinhoActivity", "Erro ao atualizar estoque", e);
                                        onComplete.run();
                                    });
                        } else {
                            Log.e("CarrinhoActivity", "Estoque atual é nulo!");
                            onComplete.run();
                        }
                    } else {
                        Log.e("CarrinhoActivity", "Produto não encontrado para atualizar estoque");
                        onComplete.run();
                    }
                });
    }
    private void mostrarDialogoDeConfirmacao(String aluguelId) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Você realmente deseja deletar este aluguel?")
                .setPositiveButton("Sim", (dialog, which) -> deletarAluguel(aluguelId))
                .setNegativeButton("Não", null)
                .show();
    }

    private void deletarAluguel(String aluguelId) {
        if (listaItensAlugados.isEmpty()) {
            Toast.makeText(this, "Nenhum item para devolver!", Toast.LENGTH_SHORT).show();
            return;
        }

        final int[] itensDevolvidos = {0};
        int totalItens = listaItensAlugados.size();

        for (ItemAluguel item : listaItensAlugados) {
            devolverItem(item, () -> {
                itensDevolvidos[0]++;
                if (itensDevolvidos[0] == totalItens) {
                    firestore.collection("AluguelFinalizadas").document(aluguelId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Aluguel deletado com sucesso!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Erro ao deletar aluguel", Toast.LENGTH_SHORT).show();
                                Log.e("DetalhesAluguel", "Erro ao deletar aluguel", e);
                            });
                }
            });
        }
    }
    private void editarAluguel(String aluguelId, String usuarioId) {
        CollectionReference itensRef = firestore.collection("AluguelFinalizadas")
                .document(aluguelId)
                .collection("ItensAluguel");

        firestore.collection("AluguelFinalizadas").document(aluguelId).get()
                .addOnCompleteListener(taskAluguel -> {
                    if (taskAluguel.isSuccessful() && taskAluguel.getResult() != null) {
                        String idNomeAluguel = taskAluguel.getResult().getString("idNomenAluguel");
                        String idTelefoneAluguel = taskAluguel.getResult().getString("idTelefoneAluguel");

                        itensRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                final int[] itensDevolvidos = {0};
                                int totalItens = task.getResult().size();

                                for (QueryDocumentSnapshot documento : task.getResult()) {
                                    ItemAluguel itemAluguel = documento.toObject(ItemAluguel.class);
                                    itemAluguel.setId(documento.getId());

                                    devolverItem(itemAluguel, () -> {
                                        itensDevolvidos[0]++;
                                        if (itensDevolvidos[0] == totalItens) {
                                            firestore.collection("CarrinhoAluguel").document(usuarioId)
                                                    .collection("ItensAluguel")
                                                    .document(itemAluguel.getId())
                                                    .set(itemAluguel)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Map<String, Object> dadosExtras = new HashMap<>();
                                                        dadosExtras.put("idNomenAluguel", idNomeAluguel);
                                                        dadosExtras.put("idTelefoneAluguel", idTelefoneAluguel);

                                                        firestore.collection("CarrinhoAluguel").document(usuarioId)
                                                                .set(dadosExtras, SetOptions.merge())
                                                                .addOnSuccessListener(aVoid2 -> {
                                                                    Log.d("DetalhesAluguel", "Campos adicionais salvos com sucesso!");
                                                                    deletarColecaoAluguelFinalizadas(aluguelId);
                                                                    ComandaAlug();
                                                                    CarrinhoAlug();
                                                                    finish();
                                                                })
                                                                .addOnFailureListener(e -> Log.e("DetalhesAluguel", "Erro ao salvar campos adicionais", e));
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e("DetalhesAluguel", "Erro ao salvar item alugado", e);
                                                    });
                                        }
                                    });
                                }
                            } else {
                                Log.e("DetalhesAluguel", "Erro ao carregar itens alugados", task.getException());
                            }
                        });
                    } else {
                        Log.e("DetalhesAluguel", "Erro ao carregar documento AluguelFinalizadas", taskAluguel.getException());
                    }
                });
    }

    public void CarrinhoAlug() {
        Intent intent = new Intent(this, CarrinhoAluguel.class);
        startActivity(intent);
    }
    public void ComandaAlug() {
        Intent intent = new Intent(this, FinalizarAluguel.class);
        startActivity(intent);
    }
}
