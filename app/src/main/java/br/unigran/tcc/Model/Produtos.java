package br.unigran.tcc.Model;

public class Produtos {
    private Integer id;
    private String nome;
    private Integer qtdProduto;
    private Float precoCompra;
    private Double precoVenda;
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
        return precoCompra;
    }

    public void setProcoCompra(Double procoCompra) {
        this.precoCompra = precoCompra;
    }

    public Double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(Double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public TipoProdutos getTipo() {
        return tipo;
    }

    public void setTipo(TipoProdutos tipo) {
        this.tipo = tipo;
    }
}
