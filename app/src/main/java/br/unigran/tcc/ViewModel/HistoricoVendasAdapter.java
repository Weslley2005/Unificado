package br.unigran.tcc.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.unigran.tcc.Model.FinalizaVendas;
import br.unigran.tcc.R;

public class HistoricoVendasAdapter extends RecyclerView.Adapter<HistoricoVendasAdapter.AluguelViewHolder>{
    private List<FinalizaVendas> listaVendas;
    private Context context;

    public HistoricoVendasAdapter(List<FinalizaVendas> listaVendas, HistoricoVendas context) {
        this.listaVendas = listaVendas;
        this.context = context;
    }



    @NonNull
    @Override
    public HistoricoVendasAdapter.AluguelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_finalizar_aluguel, parent, false);
        return new HistoricoVendasAdapter.AluguelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoricoVendasAdapter.AluguelViewHolder holder, int position) {
        FinalizaVendas vendas = listaVendas.get(position);
        holder.textTotal.setText(String.format("Total: R$%.2f", vendas.getTotal()));
        holder.textData.setText("Data: " + vendas.getData());
        holder.textHora.setText("Hora: " + vendas.getHora());
        holder.textIdNomeAluguel.setText("Nome: " + vendas.getidNomenAluguel());

        holder.buttonDetalhes.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalhesHistoricoVendas.class);
            intent.putExtra("aluguelId", vendas.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaVendas.size();
    }

    public static class AluguelViewHolder extends RecyclerView.ViewHolder {
        TextView textTotal, textData, textHora, textIdNomeAluguel;
        Button buttonDetalhes;

        public AluguelViewHolder(@NonNull View itemView) {
            super(itemView);
            textTotal = itemView.findViewById(R.id.textTotal);
            textData = itemView.findViewById(R.id.textData);
            textHora = itemView.findViewById(R.id.textHora);
            textIdNomeAluguel = itemView.findViewById(R.id.textIdNomeAluguel);
            buttonDetalhes = itemView.findViewById(R.id.buttonDetalhes);
        }
    }
}
