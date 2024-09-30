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

import br.unigran.tcc.Model.FinalizarAlugueis;
import br.unigran.tcc.R;

public class FinalizarAluguelAdapter extends RecyclerView.Adapter<FinalizarAluguelAdapter.AluguelViewHolder> {

    private List<FinalizarAlugueis> listaAlugueis; // Lista de aluguéis
    private Context context;

    public FinalizarAluguelAdapter(List<FinalizarAlugueis> listaAlugueis, FinalizarAluguel context) {
        this.listaAlugueis = listaAlugueis;
        this.context = context;
    }



    @NonNull
    @Override
    public AluguelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_finalizar_aluguel, parent, false);
        return new AluguelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AluguelViewHolder holder, int position) {
        FinalizarAlugueis aluguel = listaAlugueis.get(position); // Use a classe Aluguel
        holder.textTotal.setText(String.format("Total: R$%.2f", aluguel.getTotal()));
        holder.textData.setText("Data: " + aluguel.getData());
        holder.textHora.setText("Hora: " + aluguel.getHora());
        holder.textIdNomeAluguel.setText("Nome: " + aluguel.getIdNomenAluguel());
        holder.textIdTelefoneAluguel.setText("Telefone: " + aluguel.getIdTelefoneAluguel());

        // Descomente esta parte se a Activity DetalhesAluguelActivity estiver implementada
        holder.buttonDetalhes.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalhesAluguel.class);
            intent.putExtra("aluguelId", aluguel.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaAlugueis.size();
    }

    public static class AluguelViewHolder extends RecyclerView.ViewHolder {
        TextView textTotal, textData, textHora, textIdNomeAluguel, textIdTelefoneAluguel;
        Button buttonDetalhes;

        public AluguelViewHolder(@NonNull View itemView) {
            super(itemView);
            textTotal = itemView.findViewById(R.id.textTotal);
            textData = itemView.findViewById(R.id.textData);
            textHora = itemView.findViewById(R.id.textHora);
            textIdNomeAluguel = itemView.findViewById(R.id.textIdNomeAluguel);
            textIdTelefoneAluguel = itemView.findViewById(R.id.textIdTelefoneAluguel);
            buttonDetalhes = itemView.findViewById(R.id.buttonDetalhes);
        }
    }
}