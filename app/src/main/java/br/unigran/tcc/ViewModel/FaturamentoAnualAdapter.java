package br.unigran.tcc.ViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.unigran.tcc.R;

public class FaturamentoAnualAdapter extends RecyclerView.Adapter<FaturamentoAnualAdapter.ProdutoViewHolder> {

    private List<FaturamentoAnualImpl.ProdutoFaturamento> produtos;

    public FaturamentoAnualAdapter(List<FaturamentoAnualImpl.ProdutoFaturamento> produtos) {
        this.produtos = produtos;
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_faturamento_anual, parent, false);
        return new ProdutoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        FaturamentoAnualImpl.ProdutoFaturamento produto = produtos.get(position);
        holder.textNomeProduto.setText(produto.getNome());
        holder.textQuantidade.setText("Quantidade: " + produto.getQuantidade());
        holder.textPrecoCompra.setText("Total Compra: R$ " + produto.getTotalPrecoCompra());
        holder.textPrecoVenda.setText("Total Venda: R$ " + produto.getTotalPrecoVenda());
        holder.textPrecoLiquido.setText("Lucro: R$ " + produto.getPrecoLiquido());
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public void limparDados() {
        this.produtos.clear();
        notifyDataSetChanged();
    }

    public void updateProdutos(List<FaturamentoAnualImpl.ProdutoFaturamento> novosProdutos) {
        this.produtos.clear();
        this.produtos.addAll(novosProdutos);
        notifyDataSetChanged();
    }

    static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeProduto, textQuantidade, textPrecoCompra, textPrecoVenda, textPrecoLiquido;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeProduto = itemView.findViewById(R.id.text_nome_produto);
            textQuantidade = itemView.findViewById(R.id.text_quantidade);
            textPrecoCompra = itemView.findViewById(R.id.text_preco_compra);
            textPrecoVenda = itemView.findViewById(R.id.text_preco_venda);
            textPrecoLiquido = itemView.findViewById(R.id.text_preco_liquido);
        }
    }
}