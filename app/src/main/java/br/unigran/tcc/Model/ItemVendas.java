package br.unigran.tcc.Model;

public class ItemVendas {
    private String id;
    private String nome;
    private double precoTotal;
    private int quantidade;
    private double precoUnitaria;

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

    public void setPrecoTotal(double precoTotal) {
        this.precoTotal = precoTotal;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoUnitaria() {
        return precoUnitaria;
    }

    public void setPrecoUnitaria(double precoUnitaria) {
        this.precoUnitaria = precoUnitaria;
    }
}
