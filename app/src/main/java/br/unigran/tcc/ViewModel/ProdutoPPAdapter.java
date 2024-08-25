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

public class ProdutoPPAdapter extends RecyclerView.Adapter<ProdutoPPAdapter.ViewHolder>{

    private final List<ProdutoPP> produtoPPList;
    private final ListarProdutoPP listarProdutoPP;

    public ProdutoPPAdapter(List<ProdutoPP> produtoPPList, ListarProdutoPP listarProdutoPP) {
        this.produtoPPList = produtoPPList;
        this.listarProdutoPP = listarProdutoPP;
    }

    @NonNull
    @Override
    public ProdutoPPAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_produto_pp, parent, false);
        return new ProdutoPPAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoPPAdapter.ViewHolder holder, int position) {
        ProdutoPP produtoPP = produtoPPList.get(position);
        holder.textNome.setText(produtoPP.getNome());
        holder.textPrecoVenda.setText(String.valueOf(produtoPP.getPrecoVenda()));
        holder.textTipo.setText(produtoPP.getTipoProdutoPP());

        holder.btnExcluir.setOnClickListener(v -> {
            listarProdutoPP.showConfirmationDialog(position, produtoPP);
        });

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(listarProdutoPP, CadastroProdutoPP.class);
            intent.putExtra("produtoPP", produtoPP);
            listarProdutoPP.startActivity(intent);
            voltar();
        });
    }



    @Override
    public int getItemCount() {
        return produtoPPList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textPrecoVenda, textTipo;
        ImageButton btnExcluir;
        ImageButton btnEditar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textPrecoVenda = itemView.findViewById(R.id.textPrecoVenda);
            textTipo = itemView.findViewById(R.id.textTipo);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }

    public void voltar() {
        listarProdutoPP.finish();
    }

}
