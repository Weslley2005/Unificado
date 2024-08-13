package br.unigran.tcc.Model;

public class Produtos {
    private String nome;
    private Integer qtdProduto;
    private Float precoCompra;
    private Float precoVenda;
    private String tipo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQtdProduto() {
        return qtdProduto;
    }

    public void setQtdProduto(Integer qtdProduto) {
        this.qtdProduto = qtdProduto;
    }

    public Float getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(Float precoCompra) {
        this.precoCompra = precoCompra;
    }

    public Float getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(Float precoVenda) {
        this.precoVenda = precoVenda;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
