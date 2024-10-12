package br.unigran.tcc.ViewModel;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.unigran.tcc.Model.Produtos;
import br.unigran.tcc.R;

public class CarrinhoVendasAdapter extends RecyclerView.Adapter<CarrinhoVendasAdapter.ViewHolder> {
    private final List<Produtos> listaCarrinho;
    private final CarrinhoVendas listarCarrinho;

    public CarrinhoVendasAdapter(List<Produtos> listaCarrinho, CarrinhoVendas listarCarrinho) {
        this.listaCarrinho = listaCarrinho;
        this.listarCarrinho = listarCarrinho;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_carrinho_vendas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produtos produto = listaCarrinho.get(position);
        holder.textNome.setText(produto.getNome());
        holder.textPreco.setText(String.format("R$%.2f", produto.getPrecoVenda()));
        holder.textQtd.setText(String.format("Qtd: %d", produto.getQtdProduto()));

        holder.btnExcluir.setOnClickListener(v -> {
            listarCarrinho.mostrarDialogoDeConfirmacao(position, produto);
        });

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(listarCarrinho, ACPVAlimento.class);
            intent.putExtra("id", produto.getId());
            listarCarrinho.startActivity(intent);
            //voltar();
        });
    }

    @Override
    public int getItemCount() {
        return listaCarrinho.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textPreco, textQtd;
        ImageButton btnEditar, btnExcluir;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textPreco = itemView.findViewById(R.id.textPreco);
            textQtd = itemView.findViewById(R.id.textQtdAluguel);
            btnEditar = itemView.findViewById(R.id.btnEditarProdutos);
            btnExcluir = itemView.findViewById(R.id.btnDeletarProdutos);
        }
    }
}
