package br.ufal.ic.p2.wepayu.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

// parsing e formatacao de numeros/datas no padrao usado nos scripts de
// teste (virgula decimal, data d/M/yyyy). So faz o parsing "cru" - quem
// decide qual excecao lancar em caso de erro e a Sistema.
public class Formatos {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Formatos() {
    }

    // tenta converter "23,32" -> 23.32; devolve null se nao for um numero valido
    public static BigDecimal tentarParseNumero(String valor) {
        if (valor == null) {
            return null;
        }
        String texto = valor.trim().replace(',', '.');
        try {
            return new BigDecimal(texto);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // tenta converter "3/1/2005" -> LocalDate; devolve null se a data for invalida
    public static LocalDate tentarParseData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(data.trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // valor monetario com 2 casas e virgula - trunca em vez de arredondar,
    // e assim que os arquivos ok/*.txt calculam (ex: salario*24/52)
    public static String formatarMoeda(BigDecimal valor) {
        BigDecimal truncado = valor.setScale(2, RoundingMode.FLOOR);
        return truncado.toPlainString().replace('.', ',');
    }

    // formata horas sem casas decimais quando nao precisa (8 em vez de 8,0)
    public static String formatarHoras(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        BigDecimal semZeros = valor.stripTrailingZeros();
        if (semZeros.scale() < 0) {
            semZeros = semZeros.setScale(0);
        }
        return semZeros.toPlainString().replace('.', ',');
    }

    // ultimo dia util (seg a sex) do mes, sem considerar feriados
    public static LocalDate ultimoDiaUtilDoMes(int ano, int mes) {
        LocalDate ultimoDia = LocalDate.of(ano, mes, 1).plusMonths(1).minusDays(1);
        while (ultimoDia.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDia.getDayOfWeek() == DayOfWeek.SUNDAY) {
            ultimoDia = ultimoDia.minusDays(1);
        }
        return ultimoDia;
    }
}
