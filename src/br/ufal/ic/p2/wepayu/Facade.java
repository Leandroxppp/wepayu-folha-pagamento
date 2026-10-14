package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.excecoes.SistemaEncerradoException;
import br.ufal.ic.p2.wepayu.excecoes.WePayUException;
import br.ufal.ic.p2.wepayu.historico.Acao;
import br.ufal.ic.p2.wepayu.historico.Comando;
import br.ufal.ic.p2.wepayu.historico.GerenciadorHistorico;
import br.ufal.ic.p2.wepayu.persistencia.ArquivoPersistencia;
import br.ufal.ic.p2.wepayu.repositorio.BaseDados;
import br.ufal.ic.p2.wepayu.repositorio.ContextoSistema;
import br.ufal.ic.p2.wepayu.servicos.*;

/**
 * Porta de entrada usada pelos testes de aceitação (EasyAccept): cada comando
 * do script vira um método daqui. A fachada só encaminha o pedido ao serviço
 * certo; desfazer/refazer ficam no {@link GerenciadorHistorico} e a gravação em
 * arquivo no {@link ArquivoPersistencia}.
 */
public class Facade {

    private static final String ARQUIVO_DADOS = "wepayu.db";

    private final ArquivoPersistencia arquivo = new ArquivoPersistencia(ARQUIVO_DADOS);
    private final ContextoSistema contexto = new ContextoSistema(arquivo.carregarOuCriar());
    private final GerenciadorHistorico historico = new GerenciadorHistorico(contexto);

    private final ServicoEmpregados empregados = new ServicoEmpregados(contexto);
    private final ServicoAtributos atributos = new ServicoAtributos(contexto);
    private final ServicoAlteracoes alteracoes = new ServicoAlteracoes(contexto);
    private final ServicoLancamentos lancamentos = new ServicoLancamentos(contexto);
    private final ServicoConsultas consultas = new ServicoConsultas(contexto);
    private final ServicoFolha folha = new ServicoFolha(contexto);
    private final ServicoAgendas agendas = new ServicoAgendas(contexto);

    private boolean encerrado = false;

    // ---- sistema

    public void zerarSistema() throws WePayUException {
        executar(() -> contexto.setBase(new BaseDados()));
    }

    public void encerrarSistema() throws WePayUException {
        exigirAberto();
        arquivo.salvar(contexto.getBase());
        encerrado = true;
    }

    public void undo() throws WePayUException {
        exigirAberto();
        historico.desfazer();
    }

    public void redo() throws WePayUException {
        exigirAberto();
        historico.refazer();
    }

    public String getNumeroDeEmpregados() throws WePayUException {
        return consultar(() -> String.valueOf(empregados.getNumeroDeEmpregados()));
    }

    // ---- empregados

    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws WePayUException {
        return executar(() -> empregados.criarEmpregado(nome, endereco, tipo, salario));
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws WePayUException {
        return executar(() -> empregados.criarEmpregado(nome, endereco, tipo, salario, comissao));
    }

    public void removerEmpregado(String emp) throws WePayUException {
        executar(() -> empregados.removerEmpregado(emp));
    }

    public String getEmpregadoPorNome(String nome, int indice) throws WePayUException {
        return consultar(() -> empregados.getEmpregadoPorNome(nome, indice));
    }

    public String getAtributoEmpregado(String emp, String atributo) throws WePayUException {
        return consultar(() -> atributos.getAtributoEmpregado(emp, atributo));
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws WePayUException {
        executar(() -> alteracoes.alterar(emp, atributo, valor));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String complemento)
            throws WePayUException {
        executar(() -> alteracoes.alterarTipo(emp, atributo, valor, complemento));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato,
                                String taxaSindical) throws WePayUException {
        executar(() -> alteracoes.alterarSindicalizacao(emp, atributo, valor, idSindicato, taxaSindical));
    }

    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia,
                                String contaCorrente) throws WePayUException {
        executar(() -> alteracoes.alterarParaBanco(emp, atributo, valor1, banco, agencia, contaCorrente));
    }

    // ---- lançamentos e consultas por período

    public void lancaCartao(String emp, String data, String horas) throws WePayUException {
        executar(() -> lancamentos.lancaCartao(emp, data, horas));
    }

    public void lancaVenda(String emp, String data, String valor) throws WePayUException {
        executar(() -> lancamentos.lancaVenda(emp, data, valor));
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws WePayUException {
        executar(() -> lancamentos.lancaTaxaServico(membro, data, valor));
    }

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws WePayUException {
        return consultar(() -> consultas.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal));
    }

    public String getHorasTrabalhadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        return getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws WePayUException {
        return consultar(() -> consultas.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal));
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws WePayUException {
        return consultar(() -> consultas.getVendasRealizadas(emp, dataInicial, dataFinal));
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws WePayUException {
        return consultar(() -> consultas.getTaxasServico(emp, dataInicial, dataFinal));
    }

    // ---- folha e agendas

    public String totalFolha(String data) throws WePayUException {
        return consultar(() -> folha.totalFolha(data));
    }

    public void rodaFolha(String data, String saida) throws WePayUException {
        executar(() -> folha.rodaFolha(data, saida));
    }

    public void criarAgendaDePagamentos(String descricao) throws WePayUException {
        executar(() -> agendas.criarAgendaDePagamentos(descricao));
    }

    // ---- infraestrutura dos comandos

    /** Comando que altera dados: entra no histórico e é desfeito se falhar. */
    private <T> T executar(Comando<T> comando) throws WePayUException {
        exigirAberto();
        return historico.executar(comando);
    }

    private void executar(Acao acao) throws WePayUException {
        exigirAberto();
        historico.executar(acao);
    }

    /** Consulta: não altera nada, então não entra no histórico. */
    private <T> T consultar(Comando<T> consulta) throws WePayUException {
        exigirAberto();
        return consulta.executar();
    }

    private void exigirAberto() throws SistemaEncerradoException {
        if (encerrado) {
            throw new SistemaEncerradoException();
        }
    }
}
