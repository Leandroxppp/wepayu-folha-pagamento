package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Empregado implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String nome;
    protected String endereco;
    protected boolean sindicalizado = false;
    protected MembroSindicato sindicato;
    protected MetodoPagamento metodoPagamento = new EmMaos();

    // marca ate onde o empregado ja foi pago (exclusive)
    protected LocalDate ultimoPagamento = LocalDate.of(2004, 12, 31);

    // guarda o ultimo resultado de folha calculado - serve pra rodar a
    // folha duas vezes na mesma data e dar sempre o mesmo resultado
    protected LocalDate ultimaDataFolha;
    protected BigDecimal[] ultimoResultadoFolha;

    protected Empregado(String id, String nome, String endereco) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
    }

    public abstract String getTipo();

    /** salario por hora (horista) ou mensal (os outros dois tipos) */
    public abstract BigDecimal getSalarioBase();

    public abstract void setSalarioBase(BigDecimal valor);

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public boolean isSindicalizado() {
        return sindicalizado;
    }

    public void setSindicalizado(boolean sindicalizado) {
        this.sindicalizado = sindicalizado;
    }

    public MembroSindicato getSindicato() {
        return sindicato;
    }

    public void setSindicato(MembroSindicato sindicato) {
        this.sindicato = sindicato;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public LocalDate getUltimoPagamento() {
        return ultimoPagamento;
    }

    public void setUltimoPagamento(LocalDate data) {
        this.ultimoPagamento = data;
    }

    public LocalDate getUltimaDataFolha() {
        return ultimaDataFolha;
    }

    public void setUltimaDataFolha(LocalDate data) {
        this.ultimaDataFolha = data;
    }

    public BigDecimal[] getUltimoResultadoFolha() {
        return ultimoResultadoFolha;
    }

    public void setUltimoResultadoFolha(BigDecimal[] resultado) {
        this.ultimoResultadoFolha = resultado;
    }
}
