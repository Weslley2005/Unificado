package br.unigran.tcc.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemAluguel implements Parcelable {
    private String id; // ID do item de aluguel
    private String nome; // Nome do item
    private double precoTotal; // Preço do aluguel do item
    private int quantidade; // Quantidade do item que está sendo alugado
    private String documentoId;
    private double precoAluguelI;
    private double precoAluguelM;
    private boolean tipoAluguel;

    // Construtor vazio
    public ItemAluguel() {
    }

    // Construtor completo
    public ItemAluguel(String id, String nome, double precoAluguel, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.precoTotal = precoAluguel;
        this.quantidade = quantidade;
    }

    protected ItemAluguel(Parcel in) {
        id = in.readString();
        nome = in.readString();
        precoTotal = in.readDouble();
        quantidade = in.readInt();
        documentoId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nome);
        dest.writeDouble(precoTotal);
        dest.writeInt(quantidade);
        dest.writeString(documentoId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemAluguel> CREATOR = new Creator<ItemAluguel>() {
        @Override
        public ItemAluguel createFromParcel(Parcel in) {
            return new ItemAluguel(in);
        }

        @Override
        public ItemAluguel[] newArray(int size) {
            return new ItemAluguel[size];
        }
    };

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(double precoAluguel) {
        this.precoTotal = precoAluguel;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(String documentoId) {
        this.documentoId = documentoId;
    }

    public double getPrecoAluguelI() {
        return precoAluguelI;
    }

    public void setPrecoAluguelI(double precoAluguelI) {
        this.precoAluguelI = precoAluguelI;
    }

    public double getPrecoAluguelM() {
        return precoAluguelM;
    }

    public void setPrecoAluguelM(double precoAluguelM) {
        this.precoAluguelM = precoAluguelM;
    }

    public boolean isTipoAluguel() {
        return tipoAluguel;
    }

    public void setTipoAluguel(boolean tipoAluguel) {
        this.tipoAluguel = tipoAluguel;
    }
}
