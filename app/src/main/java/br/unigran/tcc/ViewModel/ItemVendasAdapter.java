package br.unigran.tcc.ViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.unigran.tcc.Model.ItemVendas;
import br.unigran.tcc.R;

public class ItemVendasAdapter extends RecyclerView.Adapter<ItemVendasAdapter.ItemAluguelViewHolder> {

    private List<ItemVendas> listaItens;

    public ItemVendasAdapter(List<ItemVendas> listaItens) {
        this.listaItens = listaItens;
    }

    @NonNull
    @Override
    public ItemAluguelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_item_vendas, parent, false);
        return new ItemAluguelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAluguelViewHolder holder, int position) {
        ItemVendas item = listaItens.get(position);
        holder.textNome.setText("Nome: " + item.getNome());
        holder.textQuantidade.setText("Quantidade: " + item.getQuantidade());
        holder.textPreco.setText("Preço: R$" + item.getPrecoTotal());
    }

    @Override
    public int getItemCount() {
        return listaItens.size();
    }

    public static class ItemAluguelViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textQuantidade, textPreco;

        public ItemAluguelViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textQuantidade = itemView.findViewById(R.id.textQuantidade);
            textPreco = itemView.findViewById(R.id.textPreco);
        }
    }
}