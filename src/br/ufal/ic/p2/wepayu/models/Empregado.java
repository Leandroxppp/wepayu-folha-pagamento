package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.agenda.AgendaPagamento;
import br.ufal.ic.p2.wepayu.agenda.CalendarioFolha;
import br.ufal.ic.p2.wepayu.excecoes.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Raiz do modelo de empregados. Reúne apenas o estado e os comportamentos que
 * são realmente comuns aos três tipos; as regras de cálculo ficam nas subclasses.
 */
public abstract class Empregado implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String nome;
    private String endereco;
    private MembroSindicato sindicato;
    private MetodoPagamento metodoPagamento = new EmMaos();
    private AgendaPagamento agenda;

    // Histórico é mantido mesmo quando o empregado muda de tipo.
    private final ArrayList<CartaoPonto> cartoes = new ArrayList<>();
    private final ArrayList<ResultadoVenda> vendas = new ArrayList<>();

    private LocalDate ultimoPagamento = CalendarioFolha.vesperaDoInicio();
    private LocalDate ultimaDataFolha;
    private Contracheque ultimoContracheque;

    protected Empregado(String id, String nome, String endereco, AgendaPagamento agenda)
            throws NomeNuloException, EnderecoNuloException {
        this.id = id;
        setNome(nome);
        setEndereco(endereco);
        this.agenda = Objects.requireNonNull(agenda, "Agenda de pagamento nao pode ser nula.");
    }

    /** Construtor usado na troca de tipo, preservando estado e histórico. */
    protected Empregado(Empregado origem, AgendaPagamento novaAgenda) {
        this.id = origem.id;
        this.nome = origem.nome;
        this.endereco = origem.endereco;
        this.sindicato = origem.sindicato;
        this.metodoPagamento = origem.metodoPagamento;
        this.agenda = Objects.requireNonNull(novaAgenda, "Agenda de pagamento nao pode ser nula.");
        this.cartoes.addAll(origem.cartoes);
        this.vendas.addAll(origem.vendas);
        this.ultimoPagamento = origem.ultimoPagamento;
        // O tipo mudou; um cache do tipo anterior não deve ser reaproveitado.
        this.ultimaDataFolha = null;
        this.ultimoContracheque = null;
    }

    public abstract TipoEmpregado getTipo();
    public abstract BigDecimal getSalarioBase();
    public abstract void setSalarioBase(BigDecimal valor) throws SalarioNuloException, SalarioNegativoException;

    public final String getId() { return id; }
    public final String getNome() { return nome; }
    public final String getEndereco() { return endereco; }
    public final MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public final AgendaPagamento getAgenda() { return agenda; }
    public final LocalDate getUltimoPagamento() { return ultimoPagamento; }

    public final void setNome(String nome) throws NomeNuloException {
        if (nome == null || nome.trim().isEmpty()) throw new NomeNuloException();
        this.nome = nome;
    }

    public final void setEndereco(String endereco) throws EnderecoNuloException {
        if (endereco == null || endereco.trim().isEmpty()) throw new EnderecoNuloException();
        this.endereco = endereco;
    }

    public final void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = Objects.requireNonNull(
                metodoPagamento, "Metodo de pagamento nao pode ser nulo.");
    }

    public final void setAgenda(AgendaPagamento agenda) {
        this.agenda = Objects.requireNonNull(agenda, "Agenda de pagamento nao pode ser nula.");
        limparCacheFolha();
    }

    public final boolean isSindicalizado() {
        return sindicato != null;
    }

    public final MembroSindicato getSindicato() throws EmpregadoNaoSindicalizadoException {
        if (sindicato == null) throw new EmpregadoNaoSindicalizadoException();
        return sindicato;
    }

    public final void filiar(MembroSindicato membro) {
        this.sindicato = Objects.requireNonNull(membro, "Membro do sindicato nao pode ser nulo.");
    }

    public final void desfiliar() {
        this.sindicato = null;
    }

    public BigDecimal getComissao() throws EmpregadoNaoComissionadoException {
        throw new EmpregadoNaoComissionadoException();
    }

    public void setComissao(BigDecimal valor) throws EmpregadoNaoComissionadoException, ComissaoNegativaException, ComissaoNulaException {
        throw new EmpregadoNaoComissionadoException();
    }

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

    public final BigDecimal getTaxasServico(Periodo periodo) throws EmpregadoNaoSindicalizadoException {
        BigDecimal total = BigDecimal.ZERO;
        for (TaxaServico taxa : periodo.filtrar(getSindicato().getTaxasServico())) {
            total = total.add(taxa.getValor());
        }
        return total;
    }

    public final boolean ehDiaDePagamento(LocalDate data) {
        return agenda.ehDiaDePagamento(data);
    }

    /** Valor bruto previsto, sem alterar o estado do empregado. */
    public final BigDecimal calcularBruto(LocalDate data) {
        if (data.equals(ultimaDataFolha) && ultimoContracheque != null) {
            return ultimoContracheque.getBruto();
        }
        return calcularValores(data).getBruto();
    }

    /** Efetiva o pagamento e devolve o contracheque correspondente. */
    public final Contracheque pagar(LocalDate data) {
        if (data.equals(ultimaDataFolha) && ultimoContracheque != null) {
            return ultimoContracheque;
        }
        Contracheque contracheque = calcularValores(data);
        BigDecimal desconto = calcularDescontos(data, contracheque.getBruto(), true);
        contracheque.aplicarDescontos(desconto);
        ultimoPagamento = data;
        ultimaDataFolha = data;
        ultimoContracheque = contracheque;
        return contracheque;
    }

    protected abstract Contracheque calcularValores(LocalDate data);

    protected final Periodo periodoDesdeUltimoPagamento(LocalDate data) {
        // O período de cálculo é definido pela agenda, não por uma chamada anterior de rodaFolha.
        // Assim totalFolha pode ser consultado isoladamente em várias datas e ainda produz o
        // valor correspondente àquele período de pagamento.
        return Periodo.apos(agenda.pagamentoAnterior(data), data);
    }

    protected final List<CartaoPonto> cartoesNo(Periodo periodo) {
        return periodo.filtrar(cartoes);
    }

    protected final List<ResultadoVenda> vendasNo(Periodo periodo) {
        return periodo.filtrar(vendas);
    }

    protected final void registrarCartao(CartaoPonto cartao) {
        cartoes.add(cartao);
        limparCacheFolha();
    }

    protected final void registrarVenda(ResultadoVenda venda) {
        vendas.add(venda);
        limparCacheFolha();
    }

    protected final String descricaoMetodoPagamento() {
        return metodoPagamento.descricao(endereco);
    }

    private BigDecimal calcularDescontos(LocalDate data, BigDecimal bruto, boolean persistir) {
        if (sindicato == null) return BigDecimal.ZERO.setScale(2);

        long dias = ChronoUnit.DAYS.between(sindicato.getUltimoPagamentoSindical(), data);
        BigDecimal taxaSindical = sindicato.getTaxaSindical().multiply(BigDecimal.valueOf(dias));
        BigDecimal servicos = BigDecimal.ZERO;
        List<TaxaServico> pendentes = new ArrayList<>();
        for (TaxaServico taxa : sindicato.getTaxasServico()) {
            if (!taxa.isPaga() && !taxa.getData().isAfter(data)) {
                servicos = servicos.add(taxa.getValor());
                pendentes.add(taxa);
            }
        }

        BigDecimal devido = taxaSindical.add(servicos).setScale(2, RoundingMode.FLOOR);
        if (devido.compareTo(bruto) > 0) {
            return bruto.setScale(2, RoundingMode.FLOOR);
        }
        if (persistir) {
            sindicato.setUltimoPagamentoSindical(data);
            for (TaxaServico taxa : pendentes) taxa.setPaga(true);
        }
        return devido;
    }

    protected final void limparCacheFolha() {
        ultimaDataFolha = null;
        ultimoContracheque = null;
    }
}
