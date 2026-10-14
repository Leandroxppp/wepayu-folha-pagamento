package br.ufal.ic.p2.wepayu.relatorio;

import br.ufal.ic.p2.wepayu.util.Formatos;

import java.math.BigDecimal;

/** Como um valor numérico aparece numa coluna do relatório. */
enum Formato {
    HORAS {
        @Override
        String aplicar(BigDecimal valor) {
            return Formatos.formatarHoras(valor);
        }
    },
    MOEDA {
        @Override
        String aplicar(BigDecimal valor) {
            return Formatos.formatarMoeda(valor);
        }
    };

    abstract String aplicar(BigDecimal valor);
}
