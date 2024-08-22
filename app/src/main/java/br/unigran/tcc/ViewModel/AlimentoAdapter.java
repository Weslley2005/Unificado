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

public class AlimentoAdapter extends RecyclerView.Adapter<AlimentoAdapter.ViewHolder> {
    private final List<Produtos> alimentoList;

    public AlimentoAdapter(List<Produtos> alimentoList) {
        this.alimentoList = alimentoList;
    }

    @NonNull
    @Override
    public AlimentoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_produtos_alimentos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlimentoAdapter.ViewHolder holder, int position) {
        Produtos produtos = alimentoList.get(position);
        holder.textNome.setText(produtos.getNome());
        holder.textQtd.setText(String.valueOf(produtos.getQtdProduto()));
        holder.textPrecoCompra.setText(String.valueOf(produtos.getPrecoCompra()));
        holder.textPrecoVenda.setText(String.valueOf(produtos.getPrecoVenda()));
        holder.textTipo.setText(produtos.getTipo());
    }

    @Override
    public int getItemCount() {
        return alimentoList.size(); // Retorna o tamanho da lista de alimentos
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textQtd, textPrecoCompra, textPrecoVenda, textTipo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textQtd = itemView.findViewById(R.id.textQtd);
            textPrecoCompra = itemView.findViewById(R.id.textPrecoCompra);
            textPrecoVenda = itemView.findViewById(R.id.textPrecoVenda);
            textTipo = itemView.findViewById(R.id.textTipo);
        }
    }
}