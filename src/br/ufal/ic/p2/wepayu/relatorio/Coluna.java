package br.ufal.ic.p2.wepayu.relatorio;

import br.ufal.ic.p2.wepayu.models.Contracheque;

import java.math.BigDecimal;
import java.util.function.Function;

/** Coluna numérica de uma seção do relatório: título, largura, formato e de onde sai o valor. */
final class Coluna<C extends Contracheque> {

    private final String titulo;
    private final int largura;
    private final Formato formato;
    private final Function<C, BigDecimal> valor;

    Coluna(String titulo, int largura, Formato formato, Function<C, BigDecimal> valor) {
        this.titulo = titulo;
        this.largura = largura;
        this.formato = formato;
        this.valor = valor;
    }

    static <C extends Contracheque> Coluna<C> bruto() {
        return new Coluna<C>("Salario Bruto", 13, Formato.MOEDA, Contracheque::getBruto);
    }

    static <C extends Contracheque> Coluna<C> descontos() {
        return new Coluna<C>("Descontos", 9, Formato.MOEDA, Contracheque::getDescontos);
    }

    static <C extends Contracheque> Coluna<C> liquido() {
        return new Coluna<C>("Salario Liquido", 15, Formato.MOEDA, Contracheque::getLiquido);
    }

    String getTitulo() {
        return titulo;
    }

    int getLargura() {
        return largura;
    }

    BigDecimal valorDe(C contracheque) {
        return valor.apply(contracheque);
    }

    String formatar(BigDecimal numero) {
        return formato.aplicar(numero);
    }
}
