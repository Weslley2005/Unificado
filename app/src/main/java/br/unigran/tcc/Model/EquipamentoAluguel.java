package br.unigran.tcc.Model;

import java.io.Serializable;

public class EquipamentoAluguel implements Serializable {
    private String id;
    private String nome;
    private Integer qtdAluguel;
    private Float precoAluguelM;
    private Float precoAluguelI;
    private boolean tipoAluguel;

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

    public Float getPrecoAluguelM() {
        return precoAluguelM;
    }

    public void setPrecoAluguelM(Float precoAluguelM) {
        this.precoAluguelM = precoAluguelM;
    }

    public Float getPrecoAluguelI() {
        return precoAluguelI;
    }

    public void setPrecoAluguelI(Float precoAluguelI) {
        this.precoAluguelI = precoAluguelI;
    }

    public boolean isTipoAluguel() {
        return tipoAluguel;
    }

    public void setTipoAluguel(boolean tipoAluguel) {
        this.tipoAluguel = tipoAluguel;
    }
}
