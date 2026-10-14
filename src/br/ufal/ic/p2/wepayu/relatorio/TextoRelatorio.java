package br.ufal.ic.p2.wepayu.relatorio;

/** Acumula as linhas do relatório, todas terminadas em CRLF como nos arquivos de referência. */
final class TextoRelatorio {

    private static final String QUEBRA_DE_LINHA = "\r\n";

    private final StringBuilder texto = new StringBuilder();

    void linha(String conteudo) {
        texto.append(conteudo).append(QUEBRA_DE_LINHA);
    }

    @Override
    public String toString() {
        return texto.toString();
    }
}
