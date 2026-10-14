package br.ufal.ic.p2.wepayu.excecoes;

public class FalhaPersistenciaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public FalhaPersistenciaException() {
        super("Nao foi possivel gravar os dados do sistema.");
    }
}
