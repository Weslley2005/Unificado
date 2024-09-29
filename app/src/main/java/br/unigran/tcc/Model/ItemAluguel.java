package br.unigran.tcc.Model;

public class ItemAluguel {
    private String id; // ID do item de aluguel
    private String nome; // Nome do item
    private double precoTotal; // Preço do aluguel do item
    private int quantidade; // Quantidade do item que está sendo alugado
    private String documentoId;

    // Construtor vazio
    public ItemAluguel() {
    }

    // Construtor completo
    public ItemAluguel(String id, String nome, double precoAluguel, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.precoTotal = precoAluguel;
        this.quantidade = quantidade;
    }

    // Getters e Setters
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

    public double getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(double precoAluguel) {
        this.precoTotal = precoAluguel;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(String documentoId) {
        this.documentoId = documentoId;
    }
}
