package br.ufal.ic.p2.wepayu.relatorio;

import br.ufal.ic.p2.wepayu.models.Contracheque;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Uma seção do relatório da folha (horistas, assalariados ou comissionados), com seus totais. */
final class SecaoRelatorio<C extends Contracheque> {

    private static final int LARGURA_RELATORIO = 127;
    private static final int RECUO_TITULO = 21;
    private static final int LARGURA_METODO = 38;

    private final String titulo;
    private final String rotuloTotal;
    private final int larguraNome;
    private final List<Coluna<C>> colunas;
    private final List<C> contracheques = new ArrayList<>();

    SecaoRelatorio(String titulo, String rotuloTotal, int larguraNome, List<Coluna<C>> colunas) {
        this.titulo = titulo;
        this.rotuloTotal = rotuloTotal;
        this.larguraNome = larguraNome;
        this.colunas = colunas;
    }

    void adicionar(C contracheque) {
        contracheques.add(contracheque);
    }

    BigDecimal totalBruto() {
        BigDecimal total = BigDecimal.ZERO;
        for (C contracheque : contracheques) {
            total = total.add(contracheque.getBruto());
        }
        return total;
    }

    void escreverEm(TextoRelatorio texto) {
        texto.linha(repetir('=', LARGURA_RELATORIO));
        texto.linha(linhaDoTitulo());
        texto.linha(repetir('=', LARGURA_RELATORIO));
        texto.linha(cabecalho());
        texto.linha(separador());
        for (C contracheque : contracheques) {
            texto.linha(linhaDoEmpregado(contracheque));
        }
        texto.linha("");
        texto.linha(linhaDeTotais());
        texto.linha("");
    }

    private String linhaDoTitulo() {
        String meio = " " + titulo + " ";
        return repetir('=', RECUO_TITULO) + meio + repetir('=', LARGURA_RELATORIO - RECUO_TITULO - meio.length());
    }

    private String cabecalho() {
        StringBuilder linha = new StringBuilder(aEsquerda("Nome", larguraNome)).append(' ');
        for (Coluna<C> coluna : colunas) {
            linha.append(aEsquerda(coluna.getTitulo(), coluna.getLargura())).append(' ');
        }
        return linha.append("Metodo").toString();
    }

    private String separador() {
        StringBuilder linha = new StringBuilder(repetir('=', larguraNome)).append(' ');
        for (Coluna<C> coluna : colunas) {
            linha.append(repetir('=', coluna.getLargura())).append(' ');
        }
        return linha.append(repetir('=', LARGURA_METODO)).toString();
    }

    private String linhaDoEmpregado(C contracheque) {
        StringBuilder linha = new StringBuilder(aEsquerda(contracheque.getNomeEmpregado(), larguraNome));
        for (Coluna<C> coluna : colunas) {
            String valor = coluna.formatar(coluna.valorDe(contracheque));
            linha.append(' ').append(aDireita(valor, coluna.getLargura()));
        }
        return linha.append(' ').append(contracheque.getMetodoPagamento()).toString();
    }

    private String linhaDeTotais() {
        StringBuilder linha = new StringBuilder(aEsquerda(rotuloTotal, larguraNome));
        for (Coluna<C> coluna : colunas) {
            BigDecimal soma = BigDecimal.ZERO;
            for (C contracheque : contracheques) {
                soma = soma.add(coluna.valorDe(contracheque));
            }
            linha.append(' ').append(aDireita(coluna.formatar(soma), coluna.getLargura()));
        }
        return linha.toString();
    }

    private static String repetir(char caractere, int vezes) {
        return new String(new char[vezes]).replace('\0', caractere);
    }

    private static String aEsquerda(String texto, int largura) {
        return String.format("%-" + largura + "s", texto);
    }

    private static String aDireita(String texto, int largura) {
        return String.format("%" + largura + "s", texto);
    }
}
