package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembroSindicato implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idMembro;
    private BigDecimal taxaSindical; // valor diário
    private List<TaxaServico> taxasServico = new ArrayList<>();

    // marca ate onde a taxa sindical ja foi cobrada (exclusive)
    private LocalDate ultimoPagamentoSindical = LocalDate.of(2004, 12, 31);

    public MembroSindicato(String idMembro, BigDecimal taxaSindical) {
        this.idMembro = idMembro;
        this.taxaSindical = taxaSindical;
    }

    public String getIdMembro() {
        return idMembro;
    }

    public BigDecimal getTaxaSindical() {
        return taxaSindical;
    }

    public void setTaxaSindical(BigDecimal taxaSindical) {
        this.taxaSindical = taxaSindical;
    }

    public List<TaxaServico> getTaxasServico() {
        return taxasServico;
    }

    public LocalDate getUltimoPagamentoSindical() {
        return ultimoPagamentoSindical;
    }

    public void setUltimoPagamentoSindical(LocalDate data) {
        this.ultimoPagamentoSindical = data;
    }
}
