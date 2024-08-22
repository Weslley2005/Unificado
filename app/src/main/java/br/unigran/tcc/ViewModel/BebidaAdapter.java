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

public class BebidaAdapter extends RecyclerView.Adapter<BebidaAdapter.ViewHolder>{
    private final List<Produtos> bebidaList;

    public BebidaAdapter(List<Produtos> bebidaList) {
        this.bebidaList = bebidaList;
    }

    @NonNull
    @Override
    public BebidaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_produtos_bebidas, parent, false);
        return new BebidaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BebidaAdapter.ViewHolder holder, int position) {
        Produtos produtos = bebidaList.get(position);
        holder.textNome.setText(produtos.getNome());
        holder.textQtd.setText(String.valueOf(produtos.getQtdProduto()));
        holder.textPrecoCompra.setText(String.valueOf(produtos.getPrecoCompra()));
        holder.textPrecoVenda.setText(String.valueOf(produtos.getPrecoVenda()));
        holder.textTipo.setText(produtos.getTipo());
    }

    @Override
    public int getItemCount() {
        return bebidaList.size();
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