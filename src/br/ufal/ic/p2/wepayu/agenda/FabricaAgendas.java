package br.ufal.ic.p2.wepayu.agenda;

import br.ufal.ic.p2.wepayu.excecoes.DescricaoAgendaInvalidaException;

import java.util.regex.Pattern;

/**
 * Cria agendas a partir da descrição textual. Formatos aceitos:
 * "semanal D" (D de 1 a 7), "semanal N D" (N de 1 a 52, D de 1 a 7),
 * "mensal D" (D de 1 a 28) e "mensal $" (último dia útil).
 */
public final class FabricaAgendas {

    private static final Pattern NUMERO = Pattern.compile("\\d{1,3}");
    private static final String ULTIMO_DIA_UTIL = "$";

    private FabricaAgendas() {
    }

    public static AgendaPagamento criar(String descricao) throws DescricaoAgendaInvalidaException {
        if (descricao == null) {
            throw new DescricaoAgendaInvalidaException();
        }
        String normalizada = normalizar(descricao);
        String[] partes = normalizada.split(" ");
        switch (partes[0]) {
            case "semanal":
                return criarSemanal(partes, normalizada);
            case "mensal":
                return criarMensal(partes, normalizada);
            default:
                throw new DescricaoAgendaInvalidaException();
        }
    }

    /** Tira espaços sobrando: " semanal   2  5 " vira "semanal 2 5". */
    public static String normalizar(String descricao) {
        return descricao == null ? "" : descricao.trim().replaceAll("\\s+", " ");
    }

    private static AgendaPagamento criarSemanal(String[] partes, String descricao)
            throws DescricaoAgendaInvalidaException {
        if (partes.length == 2) {
            return new AgendaSemanal(1, inteiro(partes[1], 1, 7), descricao);
        }
        if (partes.length == 3) {
            return new AgendaSemanal(inteiro(partes[1], 1, 52), inteiro(partes[2], 1, 7), descricao);
        }
        throw new DescricaoAgendaInvalidaException();
    }

    private static AgendaPagamento criarMensal(String[] partes, String descricao)
            throws DescricaoAgendaInvalidaException {
        if (partes.length != 2) {
            throw new DescricaoAgendaInvalidaException();
        }
        int dia = ULTIMO_DIA_UTIL.equals(partes[1]) ? AgendaMensal.ULTIMO_DIA_UTIL : inteiro(partes[1], 1, 28);
        return new AgendaMensal(dia, descricao);
    }

    private static int inteiro(String texto, int minimo, int maximo) throws DescricaoAgendaInvalidaException {
        if (!NUMERO.matcher(texto).matches()) {
            throw new DescricaoAgendaInvalidaException();
        }
        int valor = Integer.parseInt(texto);
        if (valor < minimo || valor > maximo) {
            throw new DescricaoAgendaInvalidaException();
        }
        return valor;
    }
}
