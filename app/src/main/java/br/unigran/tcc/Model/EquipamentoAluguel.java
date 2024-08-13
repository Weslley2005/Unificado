package br.unigran.tcc.Model;

public class EquipamentoAluguel {
    private String nome;
    private Integer qtdAluguel;
    private Float precoAluguel;
    private TipoAluguel tipoAluguem;

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

    public TipoAluguel getTipoAluguel() {
        return tipoAluguem;
    }

    public void setTipoAluguem(TipoAluguel tipoAluguem) {
        this.tipoAluguem = tipoAluguem;
    }


}
