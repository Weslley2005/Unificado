package br.unigran.tcc.ViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.unigran.tcc.Model.EquipamentoAluguel;
import br.unigran.tcc.R;

public class CarrinhoAluguelAdapter extends RecyclerView.Adapter<CarrinhoAluguelAdapter.ViewHolder> {

    private final List<EquipamentoAluguel> listaCarrinho;

    public CarrinhoAluguelAdapter(List<EquipamentoAluguel> listaCarrinho) {
        this.listaCarrinho = listaCarrinho;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_carrinho_aluguel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EquipamentoAluguel equipAlug = listaCarrinho.get(position);
        holder.textNome.setText(equipAlug.getNome());
        holder.textPreco.setText(String.format("R$%.2f", equipAlug.getPrecoAluguelI()));
        holder.textQtd.setText(String.format("Qtd: %d", equipAlug.getQtdAluguel()));

        // Botão de excluir com diálogo de confirmação

    }

    @Override
    public int getItemCount() {
        return listaCarrinho.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textPreco, textQtd;
        ImageButton btnExcluir;
        ImageButton btnEditar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textPreco = itemView.findViewById(R.id.textPreco);
            textQtd = itemView.findViewById(R.id.textQtdAluguel);
            btnExcluir = itemView.findViewById(R.id.btnDeletarCarrinhoAluguel);  // Corrigido o ID do botão
            btnEditar = itemView.findViewById(R.id.btnEditarCarrinhoAluguel);   // Corrigido o ID do botão
        }
    }
}
