package br.unigran.tcc.ViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.unigran.tcc.R;

public class FaturamentoDiarioAluguelAdapter extends RecyclerView.Adapter<FaturamentoDiarioAluguelAdapter.ProdutoViewHolder> {

    private List<FaturamentoDiarioAlugueisImpl.ProdutoFaturamento> produtos;

    public FaturamentoDiarioAluguelAdapter(List<FaturamentoDiarioAlugueisImpl.ProdutoFaturamento> produtos) {
        this.produtos = produtos;
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_faturamento_diario_aluguel, parent, false);
        return new ProdutoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        FaturamentoDiarioAlugueisImpl.ProdutoFaturamento produto = produtos.get(position);

        // Formatação de valores monetários
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        holder.textNomeProduto.setText(produto.getNome());
        holder.textQuantidade.setText("Quantidade: " + produto.getQuantidade());
        holder.textPrecoVenda.setText("Total Venda: " + currencyFormat.format(produto.getTotalPrecoVenda()));
        holder.textPrecoLiquido.setText("Lucro: " + currencyFormat.format(produto.getPrecoLiquido()));
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public void limparDados() {
        this.produtos.clear(); // Remove todos os itens da lista
        notifyDataSetChanged(); // Notifica o RecyclerView que os dados foram removidos
    }

    public void updateProdutos(List<FaturamentoDiarioAlugueisImpl.ProdutoFaturamento> novosProdutos) {
        this.produtos.clear();
        notifyDataSetChanged();
        this.produtos.addAll(novosProdutos);
        notifyDataSetChanged();
    }

    static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeProduto, textQuantidade, textPrecoVenda, textPrecoLiquido;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeProduto = itemView.findViewById(R.id.text_nome_produto);
            textQuantidade = itemView.findViewById(R.id.text_quantidade);
            textPrecoVenda = itemView.findViewById(R.id.text_preco_venda);
            textPrecoLiquido = itemView.findViewById(R.id.text_preco_liquido);
        }
    }
}