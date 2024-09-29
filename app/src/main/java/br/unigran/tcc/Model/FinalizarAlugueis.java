package br.unigran.tcc.Model;

public class FinalizarAlugueis {
    private String id;
    private double total;
    private String data;
    private String hora;
    private String idNomenAluguel;
    private String idTelefoneAluguel;

    // Construtor padrão (necessário para Firestore)
    public FinalizarAlugueis() {}

    // Getters
    public String getId() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public String getIdNomenAluguel() {
        return idNomenAluguel;
    }

    public String getIdTelefoneAluguel() {
        return idTelefoneAluguel;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setIdNomenAluguel(String idNomenAluguel) {
        this.idNomenAluguel = idNomenAluguel;
    }

    public void setIdTelefoneAluguel(String idTelefoneAluguel) {
        this.idTelefoneAluguel = idTelefoneAluguel;
    }
}
