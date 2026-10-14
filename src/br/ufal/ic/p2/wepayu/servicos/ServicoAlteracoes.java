package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.excecoes.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.repositorio.ContextoSistema;
import br.ufal.ic.p2.wepayu.util.Entrada;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Alteração dos atributos de um empregado. Há uma operação para cada forma do
 * comando alteraEmpregado (com 3, 4, 5 ou 6 argumentos).
 */
public class ServicoAlteracoes {

    @FunctionalInterface
    private interface Alteracao {
        void aplicar(Empregado empregado, String valor) throws WePayUException;
    }

    private final ContextoSistema contexto;
    private final Map<String, Alteracao> alteracoes = new HashMap<>();

    public ServicoAlteracoes(ContextoSistema contexto) {
        this.contexto = contexto;
        alteracoes.put("nome", Empregado::setNome);
        alteracoes.put("endereco", Empregado::setEndereco);
        alteracoes.put("salario", (e, valor) -> e.setSalarioBase(Entrada.salario(valor)));
        alteracoes.put("comissao", (e, valor) -> e.setComissao(Entrada.comissao(valor)));
        alteracoes.put("tipo", (e, valor) -> trocarTipo(e, TipoEmpregado.de(valor), null, null));
        alteracoes.put("sindicalizado", this::desfiliarOuExigirDados);
        alteracoes.put("metodoPagamento", this::alterarMetodoSemDados);
        alteracoes.put("agendaPagamento", (e, valor) -> e.setAgenda(contexto.agendas().buscar(valor)));
    }

    /** alteraEmpregado emp atributo valor */
    public void alterar(String emp, String atributo, String valor) throws WePayUException {
        Empregado empregado = contexto.empregados().buscar(emp);
        Alteracao alteracao = atributo == null ? null : alteracoes.get(atributo);
        if (alteracao == null) {
            throw new AtributoNaoExisteException();
        }
        alteracao.aplicar(empregado, valor);
    }

    /** alteraEmpregado emp atributo=tipo valor complemento (novo salário por hora ou comissão). */
    public void alterarTipo(String emp, String atributo, String valor, String complemento) throws WePayUException {
        Empregado empregado = contexto.empregados().buscar(emp);
        exigirAtributo("tipo", atributo);
        TipoEmpregado tipo = TipoEmpregado.de(valor);
        BigDecimal salario = tipo == TipoEmpregado.HORISTA ? Entrada.salario(complemento) : null;
        BigDecimal comissao = tipo == TipoEmpregado.COMISSIONADO ? Entrada.comissao(complemento) : null;
        trocarTipo(empregado, tipo, salario, comissao);
    }

    /** alteraEmpregado emp atributo=sindicalizado valor idSindicato taxaSindical */
    public void alterarSindicalizacao(String emp, String atributo, String valor, String idSindicato,
                                      String taxaSindical) throws WePayUException {
        Empregado empregado = contexto.empregados().buscar(emp);
        exigirAtributo("sindicalizado", atributo);
        if (!Entrada.booleano(valor)) {
            empregado.desfiliar();
            return;
        }
        BigDecimal taxa = Entrada.taxaSindical(taxaSindical);
        MembroSindicato membro = new MembroSindicato(idSindicato, taxa, empregado.getUltimoPagamento());
        if (contexto.empregados().idSindicatoEmUso(idSindicato, empregado)) {
            throw new IdSindicatoDuplicadoException();
        }
        empregado.filiar(membro);
    }

    /** alteraEmpregado emp atributo=metodoPagamento valor1=banco banco agencia contaCorrente */
    public void alterarParaBanco(String emp, String atributo, String valor, String banco, String agencia,
                                 String contaCorrente) throws WePayUException {
        Empregado empregado = contexto.empregados().buscar(emp);
        exigirAtributo("metodoPagamento", atributo);
        if (!"banco".equals(valor)) {
            throw new MetodoPagamentoInvalidoException();
        }
        empregado.setMetodoPagamento(new Banco(banco, agencia, contaCorrente));
    }

    private void exigirAtributo(String esperado, String recebido) throws AtributoNaoExisteException {
        if (!esperado.equals(recebido)) {
            throw new AtributoNaoExisteException();
        }
    }

    /** sindicalizado=false desliga o empregado do sindicato; true precisa de identificação e taxa. */
    private void desfiliarOuExigirDados(Empregado empregado, String valor) throws WePayUException {
        if (Entrada.booleano(valor)) {
            throw new IdentificacaoSindicatoNulaException();
        }
        empregado.desfiliar();
    }

    private void alterarMetodoSemDados(Empregado empregado, String valor) throws WePayUException {
        if ("emMaos".equals(valor)) {
            empregado.setMetodoPagamento(new EmMaos());
        } else if ("correios".equals(valor)) {
            empregado.setMetodoPagamento(new Correios());
        } else if ("banco".equals(valor)) {
            throw new BancoNuloException();
        } else {
            throw new MetodoPagamentoInvalidoException();
        }
    }

    /**
     * Se o tipo é o mesmo, só atualiza salário ou comissão informados; se é outro, converte o empregado
     * (com a agenda padrão do novo tipo) e o coloca no lugar do antigo.
     */
    private void trocarTipo(Empregado empregado, TipoEmpregado tipo, BigDecimal salario, BigDecimal comissao)
            throws WePayUException {
        if (tipo != empregado.getTipo()) {
            contexto.empregados().substituir(FabricaEmpregados.converter(empregado, tipo, salario, comissao));
            return;
        }
        if (salario != null) {
            empregado.setSalarioBase(salario);
        }
        if (comissao != null) {
            empregado.setComissao(comissao);
        }
    }
}
