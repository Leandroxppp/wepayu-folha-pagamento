package br.ufal.ic.p2.wepayu.excecoes;

public class ErroArquivoSaidaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ErroArquivoSaidaException() {
        super("Nao foi possivel escrever o arquivo de saida.");
    }
}
