package samos.model;

public class Sala {
    private Long id;
    private String nome;
    private String localizacao;
    private int capacidade;
    private boolean reservavel;
    public Sala() {}
    public Sala(Long id, String nome, String localizacao, int capacidade) {
        this.id = id; this.nome = nome; this.localizacao = localizacao; this.capacidade = capacidade; this.reservavel = true;
    }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; } public void setNome(String nome) { this.nome = nome; }
    public String getLocalizacao() { return localizacao; } public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public int getCapacidade() { return capacidade; } public void setCapacidade(int capacidade) { this.capacidade = capacidade; }
    public boolean isReservavel() { return reservavel; } public void setReservavel(boolean reservavel) { this.reservavel = reservavel; }
    @Override public String toString() { return "Sala [ID: " + id + ", Nome: " + nome + ", Status: " + (reservavel ? "Disponível" : "Manutenção") + "]"; }
}
