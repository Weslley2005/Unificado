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

import br.unigran.tcc.Model.FinalizaVendas;
import br.unigran.tcc.Model.ItemVendas;
import br.unigran.tcc.Model.VendasFinalizadas;
import br.unigran.tcc.R;

public class DetalhesVendas extends AppCompatActivity {
    private RecyclerView recyclerViewItensVendas;
    private ItemVendasAdapter itemVendasAdapter;
    private FinalizarVendasAdapter finalizarVendasAdapter;
    private List<ItemVendas> listaItensVendas;
    private Button buttonFinalizarAluguel;
    private FirebaseFirestore firestore;
    private List<FinalizaVendas> listaVendas;
    private ImageButton deletar, editar;
    private String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_vendas);

        recyclerViewItensVendas = findViewById(R.id.recycleViewItensAlugados);
        buttonFinalizarAluguel = findViewById(R.id.buttonFinalizarAluguel);
        deletar = findViewById(R.id.idDeletarAluguel);
        editar = findViewById(R.id.idEditarAluguel);
        listaItensVendas = new ArrayList<>();
        listaVendas = new ArrayList<>();
        itemVendasAdapter = new ItemVendasAdapter(listaItensVendas);
        finalizarVendasAdapter = new FinalizarVendasAdapter(listaVendas, this);

        recyclerViewItensVendas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItensVendas.setAdapter(itemVendasAdapter);

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

        deletar.setOnClickListener(view -> mostrarDialogoDeConfirmacao(aluguelId));
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
        CollectionReference itensRef = firestore.collection("Compras")
                .document(aluguelId)
                .collection("Itens");

        itensRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                listaItensVendas.clear();
                for (QueryDocumentSnapshot documento : task.getResult()) {
                    ItemVendas itemVendas = documento.toObject(ItemVendas.class);
                    itemVendas.setId(documento.getId());
                    listaItensVendas.add(itemVendas);
                }
                itemVendasAdapter.notifyDataSetChanged();
            } else {
                Log.e("DetalhesAluguel", "Erro ao carregar itens alugados", task.getException());
                Toast.makeText(DetalhesVendas.this, "Erro ao carregar itens!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarBotaoFinalizar(String aluguelId) {
        buttonFinalizarAluguel.setOnClickListener(v -> finalizarAluguel(aluguelId));
    }

    private void finalizarAluguel(String aluguelId) {
        if (usuarioId == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("CarrinhoAluguel").document(usuarioId)
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

    private void salvarAluguelFinalizado(String aluguelId, String usuarioId) {
        firestore.collection("Compras").document(aluguelId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        VendasFinalizadas vendasFinalizado = new VendasFinalizadas();

                        if (task.getResult().getDouble("subTotal") != null) {
                            vendasFinalizado.setSubtotal(task.getResult().getDouble("subTotal"));
                        } else {
                            Log.e("DetalhesAluguel", "subTotal é nulo");
                            vendasFinalizado.setSubtotal(0);
                        }

                        if (task.getResult().getDouble("desconto") != null) {
                            vendasFinalizado.setDesconto(task.getResult().getDouble("desconto"));
                        } else {
                            Log.e("DetalhesAluguel", "desconto é nulo");
                            vendasFinalizado.setDesconto(0.0);
                        }

                        if (task.getResult().getDouble("total") != null) {
                            vendasFinalizado.setTotal(task.getResult().getDouble("total"));
                        } else {
                            Log.e("DetalhesAluguel", "total é nulo");
                            vendasFinalizado.setTotal(0);
                        }

                        vendasFinalizado.setData(task.getResult().getString("data"));
                        vendasFinalizado.setHora(task.getResult().getString("hora"));
                        vendasFinalizado.setIdNomenAluguel(task.getResult().getString("NomenAluguel"));


                        vendasFinalizado.setUsuarioId(usuarioId);

                        firestore.collection("VendasFinaliz").document(aluguelId)
                                .set(vendasFinalizado)
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
        CollectionReference itensRef = firestore.collection("Compras")
                .document(aluguelId)
                .collection("Itens");

        itensRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot documento : task.getResult()) {
                    ItemVendas itemVendas = documento.toObject(ItemVendas.class);
                    itemVendas.setId(documento.getId());

                    firestore.collection("VendasFinaliz").document(aluguelId)
                            .collection("Itens")
                            .document(itemVendas.getId())
                            .set(itemVendas)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("DetalhesAluguel", "Item alugado salvo com sucesso: " + itemVendas.getId());
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
        CollectionReference collectionRef = firestore.collection("Compras").document(aluguelId).collection("Itens");

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
                firestore.collection("Compras").document(aluguelId)
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

    private void mostrarDialogoDeConfirmacao(String aluguelId) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Você realmente deseja deletar este aluguel?")
                .setPositiveButton("Sim", (dialog, which) -> deletarAluguel(aluguelId))
                .setNegativeButton("Não", null)
                .show();
    }

    private void deletarAluguel(String aluguelId) {
        firestore.collection("Compras").document(aluguelId)
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

    private void editarAluguel(String aluguelId, String usuarioId) {
        CollectionReference itensRef = firestore.collection("Compras")
                .document(aluguelId)
                .collection("Itens");

        firestore.collection("Compras").document(aluguelId).get()
                .addOnCompleteListener(taskAluguel -> {
                    if (taskAluguel.isSuccessful() && taskAluguel.getResult() != null) {
                        String idNomeAluguel = taskAluguel.getResult().getString("NomenAluguel");

                        itensRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                List<ItemVendas> novosItens = new ArrayList<>();

                                for (QueryDocumentSnapshot documento : task.getResult()) {
                                    ItemVendas itemVendas = documento.toObject(ItemVendas.class);
                                    itemVendas.setId(documento.getId());

                                    firestore.collection("Carrinho").document(usuarioId)
                                            .collection("Itens")
                                            .document(itemVendas.getId())
                                            .set(itemVendas)
                                            .addOnSuccessListener(aVoid -> {
                                                Map<String, Object> dadosExtras = new HashMap<>();
                                                dadosExtras.put("idNomenAluguel", idNomeAluguel);

                                                firestore.collection("Carrinho").document(usuarioId)
                                                        .set(dadosExtras, SetOptions.merge())
                                                        .addOnSuccessListener(aVoid2 -> {
                                                            Log.d("DetalhesAluguel", "Campos adicionais salvos com sucesso!");
                                                            deletarColecaoAluguelFinalizadas(aluguelId);
                                                            ComandaVend();
                                                            CarrinhoVend();
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e -> Log.e("DetalhesAluguel", "Erro ao salvar campos adicionais", e));
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("DetalhesAluguel", "Erro ao salvar item alugado", e);
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


    public void CarrinhoVend() {
        Intent intent = new Intent(this, CarrinhoVendas.class);
        startActivity(intent);
    }
    public void ComandaVend() {
        Intent intent = new Intent(this, FinalizarVendas.class);
        startActivity(intent);
    }
}
