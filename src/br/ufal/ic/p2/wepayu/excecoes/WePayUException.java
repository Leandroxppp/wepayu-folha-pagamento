package br.ufal.ic.p2.wepayu.excecoes;

/**
 * Classe-mae de todos os erros de negocio do WePayU. Cada erro concreto tem a
 * sua propria subclasse (com mensagem fixa), mas quem so quer propagar
 * "qualquer erro do sistema" declara apenas esta classe no throws.
 */
public abstract class WePayUException extends Exception {
    private static final long serialVersionUID = 1L;

    protected WePayUException(String mensagem) {
        super(mensagem);
    }
}
