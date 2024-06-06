package br.unigran.tcc.Model;

public class Produtos {
    private Integer id;
    private String nome;
    private Integer qtdProduto;
    private Float procoCompra;
    private Float precoVenda;
    private TipoProdutos tipo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Float getProcoCompra() {
        return procoCompra;
    }

    public void setProcoCompra(Float procoCompra) {
        this.procoCompra = procoCompra;
    }

    public Float getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(Float precoVenda) {
        this.precoVenda = precoVenda;
    }

    public TipoProdutos getTipo() {
        return tipo;
    }

    public void setTipo(TipoProdutos tipo) {
        this.tipo = tipo;
    }
}
