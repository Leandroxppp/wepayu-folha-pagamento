package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

/** Qualquer registro que acontece numa data (cartão de ponto, venda, taxa de serviço). */
public interface Lancamento {

    LocalDate getData();
}
