package br.unigran.tcc.Model;

public class FinalizaAlugueis {
    private String id;
    private double total;
    private String data;
    private String hora;
    private String idNomenAluguel;
    private String idTelefoneAluguel;
    private double desconto;
    private double subTotal;
    private String usuarioId;

    // Construtor padrão (necessário para Firestore)
    public FinalizaAlugueis() {}

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

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
}
