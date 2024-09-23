package br.unigran.tcc.ViewModel;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.unigran.tcc.Model.Usuario;
import br.unigran.tcc.R;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private final List<Usuario> usuarioList;

    public UsuarioAdapter(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarioList.get(position);
        holder.textNome.setText(usuario.getNome());
        holder.textCpf.setText(usuario.getCpf());
        holder.textTelefone.setText(usuario.getTelefone());
        holder.textEstado.setText(usuario.getEstado());
        holder.textCidade.setText(usuario.getCidade());
        holder.textBairro.setText(usuario.getBairro());
        holder.textRua.setText(usuario.getRua());
        holder.textNumero.setText(usuario.getNumero());
        holder.textEmail.setText(usuario.getEmail());
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textCpf, textTelefone, textEstado, textCidade, textBairro, textRua, textNumero, textEmail;

        public UsuarioViewHolder(View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textCpf = itemView.findViewById(R.id.textQtdAluguel);
            textTelefone = itemView.findViewById(R.id.textPrecoCompra);
            textEstado = itemView.findViewById(R.id.textTipo);
            textCidade = itemView.findViewById(R.id.textCidade);
            textBairro = itemView.findViewById(R.id.textBairro);
            textRua = itemView.findViewById(R.id.textRua);
            textNumero = itemView.findViewById(R.id.textNumero);
            textEmail = itemView.findViewById(R.id.textPrecoAlugM);
        }
    }


}

