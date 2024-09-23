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

import br.unigran.tcc.Model.EquipamentoAluguel;
import br.unigran.tcc.R;

public class AluguelAdapter extends RecyclerView.Adapter<AluguelAdapter.ViewHolder>{

    private final List<EquipamentoAluguel> equipAlugList;
    private final Aluguel listarEquipAlug;

    public AluguelAdapter(List<EquipamentoAluguel> equipAlugList, Aluguel listarEquipAlug) {
        this.equipAlugList = equipAlugList;
        this.listarEquipAlug = listarEquipAlug;
    }



    @NonNull
    @Override
    public AluguelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_aluguel, parent, false);
        return new AluguelAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull AluguelAdapter.ViewHolder holder, int position) {
        EquipamentoAluguel equipAlug = equipAlugList.get(position);
        holder.textNome.setText(equipAlug.getNome());
        holder.textQtdEquip.setText(String.valueOf(equipAlug.getQtdAluguel()));
        holder.textPrecoAlugelM.setText(String.format("R$: %.2f", equipAlug.getPrecoAluguelM()));
        holder.textPrecoAlugelI.setText(String.format("R$: %.2f", equipAlug.getPrecoAluguelI()));

        holder.itemView.setOnClickListener(view ->{
            Intent intent = new Intent(listarEquipAlug, ADAluguel.class);
            intent.putExtra("nome", equipAlug.getNome());
            intent.putExtra("qtdAluguel", equipAlug.getQtdAluguel());
            intent.putExtra("precoAluguelM", equipAlug.getPrecoAluguelM());
            intent.putExtra("precoAluguelI", equipAlug.getPrecoAluguelI());
            listarEquipAlug.startActivity(intent);
        });

    }



    @Override
    public int getItemCount() {
        return equipAlugList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textQtdEquip, textPrecoAlugelM, textPrecoAlugelI;
        ImageButton btnExcluir;
        ImageButton btnEditar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textQtdEquip = itemView.findViewById(R.id.textQtdAluguel);
            textPrecoAlugelM = itemView.findViewById(R.id.textPrecoAlugM);
            textPrecoAlugelI = itemView.findViewById(R.id.textPrecoAlugi);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }

}
