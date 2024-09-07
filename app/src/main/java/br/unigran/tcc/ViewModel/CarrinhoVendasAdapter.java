package br.unigran.tcc.ViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.unigran.tcc.Model.Produtos;
import br.unigran.tcc.R;

public class CarrinhoVendasAdapter extends RecyclerView.Adapter<CarrinhoVendasAdapter.ViewHolder>{
    private final List<Produtos> listaCarrinho;

    public CarrinhoVendasAdapter(List<Produtos> listaCarrinho) {
        this.listaCarrinho = listaCarrinho;
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
    }

    @Override
    public int getItemCount() {
        return listaCarrinho.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textPreco, textQtd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textPreco = itemView.findViewById(R.id.textPreco);
            textQtd = itemView.findViewById(R.id.textQtd);
        }
    }
}
