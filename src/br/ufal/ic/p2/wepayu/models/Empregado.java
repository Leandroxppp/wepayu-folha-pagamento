package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.agenda.AgendaPagamento;
import br.ufal.ic.p2.wepayu.agenda.CalendarioFolha;
import br.ufal.ic.p2.wepayu.excecoes.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Empregado da empresa. Cada tipo (horista, assalariado, comissionado) sabe
 * montar o próprio contracheque; o que é comum a todos (sindicato, método e
 * agenda de pagamento, lançamentos, descontos) fica aqui.
 *
 * <p>Os cartões de ponto e as vendas ficam nesta classe para não se perderem
 * quando o empregado muda de tipo e volta.
 */
public abstract class Empregado implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String nome;
    private String endereco;
    private MembroSindicato sindicato;
    private MetodoPagamento metodoPagamento;
    private AgendaPagamento agenda;
    private final List<CartaoPonto> cartoes;
    private final List<ResultadoVenda> vendas;

    /** Data até a qual o empregado já foi pago. */
    private LocalDate ultimoPagamento;
    private Contracheque ultimoContracheque;

    protected Empregado(String id, String nome, String endereco, AgendaPagamento agenda)
            throws NomeNuloException, EnderecoNuloException {
        this.id = id;
        setNome(nome);
        setEndereco(endereco);
        this.metodoPagamento = new EmMaos();
        this.agenda = agenda;
        this.cartoes = new ArrayList<>();
        this.vendas = new ArrayList<>();
        this.ultimoPagamento = CalendarioFolha.vesperaDoInicio();
    }

    /** Usado na mudança de tipo: leva todos os dados do empregado e passa a usar a agenda do novo tipo. */
    protected Empregado(Empregado origem, AgendaPagamento agenda) {
        this.id = origem.id;
        this.nome = origem.nome;
        this.endereco = origem.endereco;
        this.sindicato = origem.sindicato;
        this.metodoPagamento = origem.metodoPagamento;
        this.agenda = agenda;
        this.cartoes = new ArrayList<>(origem.cartoes);
        this.vendas = new ArrayList<>(origem.vendas);
        this.ultimoPagamento = origem.ultimoPagamento;
    }

    // ---- identificação e dados pessoais

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public final void setNome(String nome) throws NomeNuloException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NomeNuloException();
        }
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public final void setEndereco(String endereco) throws EnderecoNuloException {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new EnderecoNuloException();
        }
        this.endereco = endereco;
    }

    public abstract TipoEmpregado getTipo();

    // ---- salário e comissão

    /** Salário por hora (horista) ou mensal (assalariado e comissionado). */
    public abstract BigDecimal getSalarioBase();

    public abstract void setSalarioBase(BigDecimal salario) throws SalarioNuloException, SalarioNegativoException;

    public BigDecimal getComissao() throws EmpregadoNaoComissionadoException {
        throw new EmpregadoNaoComissionadoException();
    }

    public void setComissao(BigDecimal comissao)
            throws EmpregadoNaoComissionadoException, ComissaoNulaException, ComissaoNegativaException {
        throw new EmpregadoNaoComissionadoException();
    }

    protected static BigDecimal exigirSalarioValido(BigDecimal salario)
            throws SalarioNuloException, SalarioNegativoException {
        if (salario == null) {
            throw new SalarioNuloException();
        }
        if (salario.signum() < 0) {
            throw new SalarioNegativoException();
        }
        return salario;
    }

    // ---- sindicato, método e agenda de pagamento

    public boolean isSindicalizado() {
        return sindicato != null;
    }

    public MembroSindicato getSindicato() throws EmpregadoNaoSindicalizadoException {
        if (sindicato == null) {
            throw new EmpregadoNaoSindicalizadoException();
        }
        return sindicato;
    }

    public void filiar(MembroSindicato membro) {
        this.sindicato = membro;
    }

    public void desfiliar() {
        this.sindicato = null;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public AgendaPagamento getAgenda() {
        return agenda;
    }

    public void setAgenda(AgendaPagamento agenda) {
        this.agenda = agenda;
    }

    public LocalDate getUltimoPagamento() {
        return ultimoPagamento;
    }

    // ---- lançamentos e consultas por período

    public void lancarCartao(LocalDate data, BigDecimal horas)
            throws EmpregadoNaoHoristaException, HorasNaoPositivasException {
        throw new EmpregadoNaoHoristaException();
    }

    public void lancarVenda(LocalDate data, BigDecimal valor)
            throws EmpregadoNaoComissionadoException, ValorNaoPositivoException {
        throw new EmpregadoNaoComissionadoException();
    }

    public BigDecimal getHorasNormais(Periodo periodo) throws EmpregadoNaoHoristaException {
        throw new EmpregadoNaoHoristaException();
    }

    public BigDecimal getHorasExtras(Periodo periodo) throws EmpregadoNaoHoristaException {
        throw new EmpregadoNaoHoristaException();
    }

    public BigDecimal getVendas(Periodo periodo) throws EmpregadoNaoComissionadoException {
        throw new EmpregadoNaoComissionadoException();
    }

    public BigDecimal getTaxasServico(Periodo periodo) throws EmpregadoNaoSindicalizadoException {
        return getSindicato().totalTaxasServico(periodo);
    }

    protected void registrarCartao(CartaoPonto cartao) {
        cartoes.add(cartao);
    }

    protected void registrarVenda(ResultadoVenda venda) {
        vendas.add(venda);
    }

    protected List<CartaoPonto> cartoesNo(Periodo periodo) {
        return periodo.filtrar(Collections.unmodifiableList(cartoes));
    }

    protected List<ResultadoVenda> vendasNo(Periodo periodo) {
        return periodo.filtrar(Collections.unmodifiableList(vendas));
    }

    // ---- pagamento

    public boolean ehDiaDePagamento(LocalDate data) {
        return agenda.ehDiaDePagamento(data);
    }

    /** Salário bruto que o empregado receberia na data, sem registrar pagamento nenhum. */
    public BigDecimal calcularBruto(LocalDate data) {
        Contracheque contracheque = foiPagoEm(data) ? ultimoContracheque : montarContracheque(periodoAte(data), data);
        return contracheque.getBruto();
    }

    /**
     * Paga o empregado na data: calcula o contracheque, desconta o que o
     * sindicato cobra (nunca mais que o bruto) e registra o pagamento. Pagar
     * duas vezes na mesma data devolve o mesmo contracheque.
     */
    public Contracheque pagar(LocalDate data) {
        if (foiPagoEm(data)) {
            return ultimoContracheque;
        }
        Contracheque contracheque = montarContracheque(periodoAte(data), data);
        descontarSindicato(contracheque, data);
        ultimoPagamento = data;
        ultimoContracheque = contracheque;
        return contracheque;
    }

    /** Monta o contracheque (sem descontos) do período: cada tipo de empregado calcula do seu jeito. */
    protected abstract Contracheque montarContracheque(Periodo periodo, LocalDate data);

    protected String descricaoMetodoPagamento() {
        return metodoPagamento.descricao(endereco);
    }

    private boolean foiPagoEm(LocalDate data) {
        return ultimoContracheque != null && ultimoContracheque.getData().equals(data);
    }

    /** O período começa depois do pagamento anterior da agenda, ou do último pagamento efetivo, o que for mais recente. */
    private Periodo periodoAte(LocalDate data) {
        LocalDate anterior = agenda.pagamentoAnterior(data);
        return Periodo.apos(anterior.isAfter(ultimoPagamento) ? anterior : ultimoPagamento, data);
    }

    private void descontarSindicato(Contracheque contracheque, LocalDate data) {
        if (sindicato == null) {
            return;
        }
        BigDecimal debito = sindicato.calcularDebito(data);
        boolean pagaTudo = debito.compareTo(contracheque.getBruto()) <= 0;
        contracheque.aplicarDescontos(pagaTudo ? debito : contracheque.getBruto());
        if (pagaTudo) {
            sindicato.quitar(data);
        }
    }
}
