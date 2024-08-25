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

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ViewHolder> {
    private final List<Produtos> produtoList;
    private final ListarProdutos listarProdutos;

    public ProdutoAdapter(List<Produtos> produtoList, ListarProdutos listarProdutos) {
        this.produtoList = produtoList;
        this.listarProdutos = listarProdutos;
    }

    @NonNull
    @Override
    public ProdutoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_produtos_alimentos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoAdapter.ViewHolder holder, int position) {
        Produtos produtos = produtoList.get(position);
        holder.textNome.setText(produtos.getNome());
        holder.textQtd.setText(String.valueOf(produtos.getQtdProduto()));
        holder.textPrecoCompra.setText(String.valueOf(produtos.getPrecoCompra()));
        holder.textPrecoVenda.setText(String.valueOf(produtos.getPrecoVenda()));
        holder.textTipo.setText(produtos.getTipo());

        holder.btnExcluir.setOnClickListener(v -> {
            listarProdutos.mostrarDialogoDeConfirmacao(position, produtos);
        });

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(listarProdutos, CadastroProduto.class);
            intent.putExtra("produto", produtos);
            listarProdutos.startActivity(intent);
            voltar();
        });
    }



    @Override
    public int getItemCount() {
        return produtoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textQtd, textPrecoCompra, textPrecoVenda, textTipo;
        ImageButton btnExcluir;
        ImageButton btnEditar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textQtd = itemView.findViewById(R.id.textQtd);
            textPrecoCompra = itemView.findViewById(R.id.textPrecoCompra);
            textPrecoVenda = itemView.findViewById(R.id.textPrecoVenda);
            textTipo = itemView.findViewById(R.id.textTipo);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }

    public void voltar() {
        listarProdutos.finish();
    }
}
