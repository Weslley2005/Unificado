package br.unigran.tcc.Model;

import java.io.Serializable;

public class ProdutoPP implements Serializable {
    private String id;
    private String nome;
    private Float precoVenda;
    private String tipoProdutoPP;


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

    public Float getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(Float precoVenda) {
        this.precoVenda = precoVenda;
    }

    public String getTipoProdutoPP() {
        return tipoProdutoPP;
    }

    public void setTipoProdutoPP(String tipoProdutoPP) {
        this.tipoProdutoPP = tipoProdutoPP;
    }
}

