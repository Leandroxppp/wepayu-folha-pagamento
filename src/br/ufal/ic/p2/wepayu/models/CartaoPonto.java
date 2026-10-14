package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.excecoes.HorasNaoPositivasException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Horas trabalhadas por um horista num dia. Até 8 horas são normais; o que passar disso é hora extra. */
public class CartaoPonto implements Lancamento, Serializable {
    private static final long serialVersionUID = 1L;

    private static final BigDecimal JORNADA_NORMAL = BigDecimal.valueOf(8);

    private final LocalDate data;
    private final BigDecimal horas;

    public CartaoPonto(LocalDate data, BigDecimal horas) throws HorasNaoPositivasException {
        if (horas == null || horas.signum() <= 0) {
            throw new HorasNaoPositivasException();
        }
        this.data = data;
        this.horas = horas;
    }

    @Override
    public LocalDate getData() {
        return data;
    }

    public BigDecimal getHorasNormais() {
        return horas.min(JORNADA_NORMAL);
    }

    public BigDecimal getHorasExtras() {
        return horas.subtract(JORNADA_NORMAL).max(BigDecimal.ZERO);
    }
}
