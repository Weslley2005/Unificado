package br.unigran.tcc.Model;

import java.io.Serializable;

public class EquipamentoAluguel implements Serializable {
    private String id;
    private String nome;
    private Integer qtdAluguel;
    private Float precoAluguel;
    private String tipoAluguel;

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

    public Integer getQtdAluguel() {
        return qtdAluguel;
    }

    public void setQtdAluguel(Integer qtdAluguel) {
        this.qtdAluguel = qtdAluguel;
    }

    public Float getPrecoAluguel() {
        return precoAluguel;
    }

    public void setPrecoAluguel(Float precoAluguel) {
        this.precoAluguel = precoAluguel;
    }

    public String getTipoAluguel() {
        return tipoAluguel;
    }

    public void setTipoAluguel(String tipoAluguel) {
        this.tipoAluguel = tipoAluguel;
    }
}
