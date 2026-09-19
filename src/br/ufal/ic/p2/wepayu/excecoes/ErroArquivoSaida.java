package br.ufal.ic.p2.wepayu.excecoes;

public class ErroArquivoSaida extends Exception {
    public ErroArquivoSaida() {
        super("Nao foi possivel escrever o arquivo de saida.");
    }
}
