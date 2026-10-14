package br.ufal.ic.p2.wepayu.util;

import br.ufal.ic.p2.wepayu.excecoes.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Supplier;

/**
 * Traduz os textos recebidos pela fachada em números, datas e booleanos,
 * lançando a exceção específica de cada campo quando o texto é vazio ou não
 * tem o formato esperado. Regras de valor (negativo, zero) ficam nos modelos.
 */
public final class Entrada {

    private Entrada() {
    }

    public static BigDecimal salario(String texto) throws SalarioNuloException, SalarioNaoNumericoException {
        return numero(texto, SalarioNuloException::new, SalarioNaoNumericoException::new);
    }

    public static BigDecimal comissao(String texto) throws ComissaoNulaException, ComissaoNaoNumericaException {
        return numero(texto, ComissaoNulaException::new, ComissaoNaoNumericaException::new);
    }

    public static BigDecimal taxaSindical(String texto)
            throws TaxaSindicalNulaException, TaxaSindicalNaoNumericaException {
        return numero(texto, TaxaSindicalNulaException::new, TaxaSindicalNaoNumericaException::new);
    }

    public static BigDecimal horas(String texto) throws HorasNulaException, HorasNaoNumericaException {
        return numero(texto, HorasNulaException::new, HorasNaoNumericaException::new);
    }

    public static BigDecimal valor(String texto) throws ValorNuloException, ValorNaoNumericoException {
        return numero(texto, ValorNuloException::new, ValorNaoNumericoException::new);
    }

    public static LocalDate data(String texto) throws DataInvalidaException {
        return data(texto, DataInvalidaException::new);
    }

    public static LocalDate dataInicial(String texto) throws DataInicialInvalidaException {
        return data(texto, DataInicialInvalidaException::new);
    }

    public static LocalDate dataFinal(String texto) throws DataFinalInvalidaException {
        return data(texto, DataFinalInvalidaException::new);
    }

    public static boolean booleano(String texto) throws ValorNaoBooleanoException {
        if ("true".equals(texto)) {
            return true;
        }
        if ("false".equals(texto)) {
            return false;
        }
        throw new ValorNaoBooleanoException();
    }

    public static boolean vazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private static <N extends Exception, F extends Exception> BigDecimal numero(
            String texto, Supplier<N> erroNulo, Supplier<F> erroFormato) throws N, F {
        if (vazio(texto)) {
            throw erroNulo.get();
        }
        BigDecimal numero = Formatos.tentarParseNumero(texto);
        if (numero == null) {
            throw erroFormato.get();
        }
        return numero;
    }

    private static <E extends Exception> LocalDate data(String texto, Supplier<E> erro) throws E {
        LocalDate data = vazio(texto) ? null : Formatos.tentarParseData(texto);
        if (data == null) {
            throw erro.get();
        }
        return data;
    }
}
