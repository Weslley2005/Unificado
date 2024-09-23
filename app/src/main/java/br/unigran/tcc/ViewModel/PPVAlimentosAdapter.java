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

import br.unigran.tcc.Model.ProdutoPP;
import br.unigran.tcc.R;

public class PPVAlimentosAdapter extends RecyclerView.Adapter<PPVAlimentosAdapter.ViewHolder> {

    private final List<ProdutoPP> produtoPPList;
    private final ListarPVAlimentos listarProdutoPP;

    public PPVAlimentosAdapter(List<ProdutoPP> produtoPPList, ListarPVAlimentos listarProdutoPP) {
        this.produtoPPList = produtoPPList;
        this.listarProdutoPP = listarProdutoPP;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_listar_ppvalimentos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProdutoPP produtoPP = produtoPPList.get(position);
        holder.textNome.setText(produtoPP.getNome());
        holder.textPrecoVenda.setText(String.format("R$: %.2f", produtoPP.getPrecoVenda()));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(listarProdutoPP, ACPPVAlimentos.class);
            intent.putExtra("nome", produtoPP.getNome());
            intent.putExtra("precoVenda", produtoPP.getPrecoVenda());
            listarProdutoPP.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return produtoPPList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textPrecoVenda;
        ImageButton btnExcluir;
        ImageButton btnEditar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textPrecoVenda = itemView.findViewById(R.id.textPrecoAlugM);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}
