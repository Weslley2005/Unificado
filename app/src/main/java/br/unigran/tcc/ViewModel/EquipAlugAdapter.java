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

public class EquipAlugAdapter extends RecyclerView.Adapter<EquipAlugAdapter.ViewHolder>{
    private final List<EquipamentoAluguel> equipAlugList;
    private final ListarEquipAlug listarEquipAlug;

    public EquipAlugAdapter(List<EquipamentoAluguel> equipAlugList, ListarEquipAlug listarEquipAlug) {
        this.equipAlugList = equipAlugList;
        this.listarEquipAlug = listarEquipAlug;
    }

    @NonNull
    @Override
    public EquipAlugAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_equip_alug, parent, false);
        return new EquipAlugAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipAlugAdapter.ViewHolder holder, int position) {
        EquipamentoAluguel equipAlug = equipAlugList.get(position);
        holder.textNome.setText(equipAlug.getNome());
        holder.textQtdEquip.setText(String.valueOf(equipAlug.getQtdAluguel()));
        holder.textPrecoAlugelM.setText(String.format("R$: %.2f", equipAlug.getPrecoAluguelM()));
        holder.textPrecoAlugelI.setText(String.format("R$: %.2f", equipAlug.getPrecoAluguelI()));

        holder.btnExcluir.setOnClickListener(v -> {
            listarEquipAlug.showConfirmationDialog(position, equipAlug);
        });

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(listarEquipAlug, CadastroEquipAlug.class);
            intent.putExtra("equipAlug", equipAlug);
            listarEquipAlug.startActivity(intent);
            voltar();
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
            textQtdEquip = itemView.findViewById(R.id.textQtdEquip);
            textPrecoAlugelM = itemView.findViewById(R.id.textPrecoAlugM);
            textPrecoAlugelI = itemView.findViewById(R.id.textPrecoAlugI);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }

    public void voltar() {
        listarEquipAlug.finish();
    }

}

