package br.unigran.tcc.Model;

public class AluguelFinalizado {
    private String idNomenAluguel;
    private String idTelefoneAluguel;
    private String data;
    private String hora;
    private Double desconto;
    private double subtotal;
    private double total;
    private String usuarioId;

    // Construtor padrão
    public AluguelFinalizado() {}

    // Getters e Setters


    public String getIdNomenAluguel() {
        return idNomenAluguel;
    }

    public void setIdNomenAluguel(String idNomenAluguel) {
        this.idNomenAluguel = idNomenAluguel;
    }

    public String getIdTelefoneAluguel() {
        return idTelefoneAluguel;
    }

    public void setIdTelefoneAluguel(String idTelefoneAluguel) {
        this.idTelefoneAluguel = idTelefoneAluguel;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
}
