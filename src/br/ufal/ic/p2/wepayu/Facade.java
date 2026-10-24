package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.excecoes.*;
import br.ufal.ic.p2.wepayu.historico.GerenciadorHistorico;
import br.ufal.ic.p2.wepayu.persistencia.ArquivoPersistencia;
import br.ufal.ic.p2.wepayu.repositorio.BaseDados;
import br.ufal.ic.p2.wepayu.repositorio.ContextoSistema;
import br.ufal.ic.p2.wepayu.servicos.*;

/**
 * Fachada usada pelo EasyAccept. Ela apenas traduz os comandos dos scripts para
 * os serviços da aplicação; regras de negócio, histórico e persistência ficam
 * em componentes próprios.
 */
public class Facade {

    private static final String ARQUIVO_PERSISTENCIA = "wepayu.db";

    private final ArquivoPersistencia persistencia;
    private final ContextoSistema contexto;
    private final GerenciadorHistorico historico;
    private final ServicoEmpregados empregados;
    private final ServicoAtributos atributos;
    private final ServicoAlteracoes alteracoes;
    private final ServicoLancamentos lancamentos;
    private final ServicoConsultas consultas;
    private final ServicoFolha folha;
    private final ServicoAgendas agendas;

    private boolean encerrado;

    public Facade() {
        persistencia = new ArquivoPersistencia(ARQUIVO_PERSISTENCIA);
        contexto = new ContextoSistema(persistencia.carregarOuCriar());
        historico = new GerenciadorHistorico(contexto);
        empregados = new ServicoEmpregados(contexto);
        atributos = new ServicoAtributos(contexto);
        alteracoes = new ServicoAlteracoes(contexto);
        lancamentos = new ServicoLancamentos(contexto);
        consultas = new ServicoConsultas(contexto);
        folha = new ServicoFolha(contexto);
        agendas = new ServicoAgendas(contexto);
    }

    private void checarEncerrado() throws SistemaEncerradoException {
        if (encerrado) throw new SistemaEncerradoException();
    }

    // comandos gerais

    public void zerarSistema() throws WePayUException {
        checarEncerrado();
        historico.executar(() -> contexto.setBase(new BaseDados()));
    }

    public void encerrarSistema() throws WePayUException {
        checarEncerrado();
        persistencia.salvar(contexto.getBase());
        encerrado = true;
    }

    public void undo() throws WePayUException {
        checarEncerrado();
        historico.desfazer();
    }

    public void redo() throws WePayUException {
        checarEncerrado();
        historico.refazer();
    }

    public String getNumeroDeEmpregados() throws WePayUException {
        checarEncerrado();
        return String.valueOf(empregados.getNumeroDeEmpregados());
    }

    // empregados

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws WePayUException {
        checarEncerrado();
        return historico.executar(() -> empregados.criarEmpregado(nome, endereco, tipo, salario));
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws WePayUException {
        checarEncerrado();
        return historico.executar(() -> empregados.criarEmpregado(nome, endereco, tipo, salario, comissao));
    }

    public void removerEmpregado(String emp) throws WePayUException {
        checarEncerrado();
        historico.executar(() -> empregados.removerEmpregado(emp));
    }

    public String getEmpregadoPorNome(String nome, int indice) throws WePayUException {
        checarEncerrado();
        return empregados.getEmpregadoPorNome(nome, indice);
    }

    public String getAtributoEmpregado(String emp, String atributo) throws WePayUException {
        checarEncerrado();
        return atributos.getAtributoEmpregado(emp, atributo);
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws WePayUException {
        checarEncerrado();
        historico.executar(() -> alteracoes.alterar(emp, atributo, valor));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String extra) throws WePayUException {
        checarEncerrado();
        historico.executar(() -> alteracoes.alterarTipo(emp, atributo, valor, extra));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical)
            throws WePayUException {
        checarEncerrado();
        historico.executar(() -> alteracoes.alterarSindicalizacao(emp, atributo, valor, idSindicato, taxaSindical));
    }

    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia,
                                String contaCorrente) throws WePayUException {
        checarEncerrado();
        historico.executar(() -> alteracoes.alterarParaBanco(emp, atributo, valor1, banco, agencia, contaCorrente));
    }

    // lançamentos

    public void lancaCartao(String emp, String data, String horas) throws WePayUException {
        checarEncerrado();
        historico.executar(() -> lancamentos.lancaCartao(emp, data, horas));
    }

    public void lancaVenda(String emp, String data, String valor) throws WePayUException {
        checarEncerrado();
        historico.executar(() -> lancamentos.lancaVenda(emp, data, valor));
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws WePayUException {
        checarEncerrado();
        historico.executar(() -> lancamentos.lancaTaxaServico(membro, data, valor));
    }

    // consultas

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        checarEncerrado();
        return consultas.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        checarEncerrado();
        return consultas.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        checarEncerrado();
        return consultas.getVendasRealizadas(emp, dataInicial, dataFinal);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws WePayUException {
        checarEncerrado();
        return consultas.getTaxasServico(emp, dataInicial, dataFinal);
    }

    // folha

    public String totalFolha(String data) throws WePayUException {
        checarEncerrado();
        return folha.totalFolha(data);
    }

    public void rodaFolha(String data, String saida) throws WePayUException {
        checarEncerrado();
        historico.executar(() -> folha.rodaFolha(data, saida));
    }

    // US 10

    public void criarAgendaDePagamentos(String descricao) throws WePayUException {
        checarEncerrado();
        historico.executar(() -> agendas.criarAgendaDePagamentos(descricao));
    }
}
