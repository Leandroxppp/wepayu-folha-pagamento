package br.ufal.ic.p2.wepayu.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Conversão de números e datas no padrão usado nos scripts de teste (vírgula
 * decimal, datas no formato d/M/aaaa). Só faz o parsing "cru": quem decide qual
 * exceção lançar em caso de erro é a classe {@link Entrada}.
 */
public final class Formatos {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Formatos() {
    }

    /** Converte "23,32" em 23.32; devolve null se o texto não for um número. */
    public static BigDecimal tentarParseNumero(String texto) {
        try {
            return new BigDecimal(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Converte "3/1/2005" em data; devolve null se a data for inválida. */
    public static LocalDate tentarParseData(String texto) {
        try {
            return LocalDate.parse(texto.trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Valor monetário com 2 casas e vírgula. Trunca em vez de arredondar, que é
     * como os arquivos de referência calculam (ex.: salário * 24 / 52).
     */
    public static String formatarMoeda(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.FLOOR).toPlainString().replace('.', ',');
    }

    /** Horas sem casas decimais quando não precisa (8 em vez de 8,0). */
    public static String formatarHoras(BigDecimal valor) {
        if (valor.signum() == 0) {
            return "0";
        }
        BigDecimal semZeros = valor.stripTrailingZeros();
        if (semZeros.scale() < 0) {
            semZeros = semZeros.setScale(0);
        }
        return semZeros.toPlainString().replace('.', ',');
    }
}
