package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.excecoes.WePayUException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.TaxaServico;
import br.ufal.ic.p2.wepayu.repositorio.ContextoSistema;
import br.ufal.ic.p2.wepayu.util.Entrada;

/** Lançamento de cartões de ponto, vendas e taxas de serviço. */
public class ServicoLancamentos {

    private final ContextoSistema contexto;

    public ServicoLancamentos(ContextoSistema contexto) {
        this.contexto = contexto;
    }

    public void lancaCartao(String emp, String data, String horas) throws WePayUException {
        Empregado empregado = contexto.empregados().buscar(emp);
        empregado.lancarCartao(Entrada.data(data), Entrada.horas(horas));
    }

    public void lancaVenda(String emp, String data, String valor) throws WePayUException {
        Empregado empregado = contexto.empregados().buscar(emp);
        empregado.lancarVenda(Entrada.data(data), Entrada.valor(valor));
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws WePayUException {
        Empregado empregado = contexto.empregados().buscarPorIdSindicato(membro);
        empregado.getSindicato().registrar(new TaxaServico(Entrada.data(data), Entrada.valor(valor)));
    }
}
