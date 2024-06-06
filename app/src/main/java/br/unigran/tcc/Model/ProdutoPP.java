package br.unigran.tcc.Model;

public class ProdutoPP {
    private Integer id;
    private String nome;
    private Float precoVP;
    private TipoProdutoPP tipoProdutoPP;

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

    public Float getPrecoVP() {
        return precoVP;
    }

    public void setPrecoVP(Float precoVP) {
        this.precoVP = precoVP;
    }

    public TipoProdutoPP getTipoProdutoPP() {
        return tipoProdutoPP;
    }

    public void setTipoProdutoPP(TipoProdutoPP tipoProdutoPP) {
        this.tipoProdutoPP = tipoProdutoPP;
    }
}

