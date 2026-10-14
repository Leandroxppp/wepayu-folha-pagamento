package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.excecoes.TipoInvalidoException;

/** Os tipos de empregado do sistema e o texto usado para identificá-los nos comandos. */
public enum TipoEmpregado {
    HORISTA("horista"),
    ASSALARIADO("assalariado"),
    COMISSIONADO("comissionado");

    private final String texto;

    TipoEmpregado(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public static TipoEmpregado de(String texto) throws TipoInvalidoException {
        for (TipoEmpregado tipo : values()) {
            if (tipo.texto.equals(texto)) {
                return tipo;
            }
        }
        throw new TipoInvalidoException();
    }
}
