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

public class PVAlimentoAdapter extends RecyclerView.Adapter<PVAlimentoAdapter.ViewHolder> {
    private final List<Produtos> produtoList;
    private final ListarPVAlimentos listarProdutos;

    public PVAlimentoAdapter(List<Produtos> produtoList, ListarPVAlimentos listarProdutos) {
        this.produtoList = produtoList;
        this.listarProdutos = listarProdutos;
    }

    @NonNull
    @Override
    public PVAlimentoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_listar_pvalimentos, parent, false);
        return new PVAlimentoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PVAlimentoAdapter.ViewHolder holder, int position) {
        Produtos produtos = produtoList.get(position);
        holder.textNome.setText(produtos.getNome());
        holder.textQtd.setText(String.valueOf(produtos.getQtdProduto()));
        holder.textPrecoVenda.setText(String.format("R$: %.2f",produtos.getPrecoVenda()));

        holder.itemView.setOnClickListener(view ->{
            Intent intent = new Intent(listarProdutos, ACPVAlimento.class);
            intent.putExtra("nome", produtos.getNome());
            intent.putExtra("qtdProduto", produtos.getQtdProduto());
            intent.putExtra("precoVenda", produtos.getPrecoVenda());
            listarProdutos.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return produtoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textQtd, textPrecoVenda;
        ImageButton btnExcluir;
        ImageButton btnEditar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textQtd = itemView.findViewById(R.id.textQtd);
            textPrecoVenda = itemView.findViewById(R.id.textPrecoAlugM);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}
