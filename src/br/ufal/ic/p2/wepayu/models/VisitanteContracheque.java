package br.ufal.ic.p2.wepayu.models;

/** Permite tratar cada tipo de contracheque sem precisar testar a classe com instanceof. */
public interface VisitanteContracheque {

    void visitar(ContrachequeHorista contracheque);

    void visitar(ContrachequeAssalariado contracheque);

    void visitar(ContrachequeComissionado contracheque);
}
