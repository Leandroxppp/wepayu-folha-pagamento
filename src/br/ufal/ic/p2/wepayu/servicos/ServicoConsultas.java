package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.excecoes.*;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Periodo;
import br.ufal.ic.p2.wepayu.repositorio.ContextoSistema;
import br.ufal.ic.p2.wepayu.util.Entrada;
import br.ufal.ic.p2.wepayu.util.Formatos;

import java.time.LocalDate;

/** Consultas de horas, vendas e taxas de serviço de um empregado num período [dataInicial, dataFinal). */
public class ServicoConsultas {

    private final ContextoSistema contexto;

    public ServicoConsultas(ContextoSistema contexto) {
        this.contexto = contexto;
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws WePayUException {
        Empregado empregado = contexto.empregados().buscar(emp);
        return Formatos.formatarHoras(empregado.getHorasNormais(periodo(dataInicial, dataFinal)));
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws WePayUException {
        Empregado empregado = contexto.empregados().buscar(emp);
        return Formatos.formatarHoras(empregado.getHorasExtras(periodo(dataInicial, dataFinal)));
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        Empregado empregado = contexto.empregados().buscar(emp);
        return Formatos.formatarMoeda(empregado.getVendas(periodo(dataInicial, dataFinal)));
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws WePayUException {
        Empregado empregado = contexto.empregados().buscar(emp);
        return Formatos.formatarMoeda(empregado.getTaxasServico(periodo(dataInicial, dataFinal)));
    }

    private static Periodo periodo(String dataInicial, String dataFinal)
            throws DataInicialInvalidaException, DataFinalInvalidaException, DataInicialPosteriorFinalException {
        LocalDate inicio = Entrada.dataInicial(dataInicial);
        LocalDate fim = Entrada.dataFinal(dataFinal);
        if (inicio.isAfter(fim)) {
            throw new DataInicialPosteriorFinalException();
        }
        return Periodo.entre(inicio, fim);
    }
}
