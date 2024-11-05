package br.unigran.tcc.Model;

public class FinalizaVendas {
    private String id;
    private double total;
    private String data;
    private String hora;
    private String NomenAluguel;
    private double desconto;
    private double subTotal;
    private String usuarioId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
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

    public String getNomenAluguel() {
        return NomenAluguel;
    }

    public void setNomenAluguel(String nomenAluguel) {
        NomenAluguel = nomenAluguel;
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
