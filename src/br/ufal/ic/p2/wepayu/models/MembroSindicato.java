package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.agenda.CalendarioFolha;
import br.ufal.ic.p2.wepayu.excecoes.IdentificacaoSindicatoNulaException;
import br.ufal.ic.p2.wepayu.excecoes.TaxaSindicalNulaException;
import br.ufal.ic.p2.wepayu.excecoes.TaxaSindicalNegativaException;
import br.ufal.ic.p2.wepayu.excecoes.ValorNaoPositivoException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Dados sindicais de um empregado. */
public class MembroSindicato implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private BigDecimal taxaSindical;
    private final List<TaxaServico> taxasServico = new ArrayList<>();
    private LocalDate ultimoPagamentoSindical;

    public MembroSindicato(String id, BigDecimal taxaSindical)
            throws IdentificacaoSindicatoNulaException, TaxaSindicalNulaException, TaxaSindicalNegativaException {
        this(id, taxaSindical, CalendarioFolha.vesperaDoInicio());
    }

    public MembroSindicato(String id, BigDecimal taxaSindical, LocalDate ultimoPagamento)
            throws IdentificacaoSindicatoNulaException, TaxaSindicalNulaException, TaxaSindicalNegativaException {
        if (id == null || id.trim().isEmpty()) throw new IdentificacaoSindicatoNulaException();
        if (taxaSindical == null) throw new TaxaSindicalNulaException();
        if (taxaSindical.compareTo(BigDecimal.ZERO) < 0) throw new TaxaSindicalNegativaException();
        this.id = id;
        this.taxaSindical = taxaSindical;
        this.ultimoPagamentoSindical = ultimoPagamento;
    }

    public String getId() { return id; }
    public BigDecimal getTaxaSindical() { return taxaSindical; }
    public LocalDate getUltimoPagamentoSindical() { return ultimoPagamentoSindical; }
    public void setUltimoPagamentoSindical(LocalDate data) { this.ultimoPagamentoSindical = data; }

    public void setTaxaSindical(BigDecimal valor) throws TaxaSindicalNulaException, TaxaSindicalNegativaException {
        if (valor == null) throw new TaxaSindicalNulaException();
        if (valor.compareTo(BigDecimal.ZERO) < 0) throw new TaxaSindicalNegativaException();
        taxaSindical = valor;
    }

    public void registrar(TaxaServico taxa) throws ValorNaoPositivoException {
        if (taxa.getValor().compareTo(BigDecimal.ZERO) <= 0) throw new ValorNaoPositivoException();
        taxasServico.add(taxa);
    }

    public List<TaxaServico> getTaxasServico() {
        return Collections.unmodifiableList(taxasServico);
    }
}
