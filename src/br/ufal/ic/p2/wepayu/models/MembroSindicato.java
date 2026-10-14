package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.excecoes.IdentificacaoSindicatoNulaException;
import br.ufal.ic.p2.wepayu.excecoes.TaxaSindicalNegativaException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Filiação de um empregado ao sindicato: identificação no sindicato, taxa
 * sindical (valor cobrado por dia) e as taxas de serviço lançadas.
 */
public class MembroSindicato implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private BigDecimal taxaSindical;
    private final List<TaxaServico> taxasServico = new ArrayList<>();
    private LocalDate ultimaCobranca;

    /** @param ultimaCobranca data até a qual a taxa sindical já foi cobrada */
    public MembroSindicato(String id, BigDecimal taxaSindical, LocalDate ultimaCobranca)
            throws IdentificacaoSindicatoNulaException, TaxaSindicalNegativaException {
        if (id == null || id.trim().isEmpty()) {
            throw new IdentificacaoSindicatoNulaException();
        }
        this.id = id;
        setTaxaSindical(taxaSindical);
        this.ultimaCobranca = ultimaCobranca;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getTaxaSindical() {
        return taxaSindical;
    }

    public void setTaxaSindical(BigDecimal taxaSindical) throws TaxaSindicalNegativaException {
        if (taxaSindical == null || taxaSindical.signum() < 0) {
            throw new TaxaSindicalNegativaException();
        }
        this.taxaSindical = taxaSindical;
    }

    public void registrar(TaxaServico taxa) {
        taxasServico.add(taxa);
    }

    public List<TaxaServico> getTaxasServico() {
        return Collections.unmodifiableList(taxasServico);
    }

    public BigDecimal totalTaxasServico(Periodo periodo) {
        return periodo.filtrar(taxasServico).stream()
                .map(TaxaServico::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Taxa sindical acumulada desde a última cobrança mais as taxas de serviço pendentes até a data. */
    public BigDecimal calcularDebito(LocalDate data) {
        long dias = ChronoUnit.DAYS.between(ultimaCobranca, data);
        BigDecimal taxaDoPeriodo = taxaSindical.multiply(BigDecimal.valueOf(dias));
        return taxaDoPeriodo.add(totalPendente(data)).setScale(2, RoundingMode.FLOOR);
    }

    /** Registra que tudo o que estava pendente até a data foi descontado. */
    public void quitar(LocalDate data) {
        pendentesAte(data).forEach(TaxaServico::quitar);
        ultimaCobranca = data;
    }

    private BigDecimal totalPendente(LocalDate data) {
        return pendentesAte(data).stream().map(TaxaServico::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<TaxaServico> pendentesAte(LocalDate data) {
        List<TaxaServico> pendentes = new ArrayList<>();
        for (TaxaServico taxa : taxasServico) {
            if (!taxa.isPaga() && !taxa.getData().isAfter(data)) {
                pendentes.add(taxa);
            }
        }
        return pendentes;
    }
}
