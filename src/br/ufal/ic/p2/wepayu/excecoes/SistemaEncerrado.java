package br.ufal.ic.p2.wepayu.excecoes;

public class SistemaEncerrado extends Exception {
    public SistemaEncerrado() {
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}
