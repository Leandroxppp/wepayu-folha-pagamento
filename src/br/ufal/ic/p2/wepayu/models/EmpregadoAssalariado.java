package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.agenda.AgendaPagamento;
import br.ufal.ic.p2.wepayu.agenda.AgendasPadrao;
import br.ufal.ic.p2.wepayu.excecoes.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Recebe um salário fixo mensal, pago de acordo com a sua agenda de pagamento. */
public class EmpregadoAssalariado extends Empregado {
    private static final long serialVersionUID = 1L;

    private BigDecimal salarioMensal;

    public EmpregadoAssalariado(String id, String nome, String endereco, BigDecimal salarioMensal)
            throws NomeNuloException, EnderecoNuloException, SalarioNuloException, SalarioNegativoException {
        this(id, nome, endereco, salarioMensal, AgendasPadrao.MENSAL_ULTIMO_DIA_UTIL);
    }

    public EmpregadoAssalariado(Empregado origem, BigDecimal salarioMensal)
            throws SalarioNuloException, SalarioNegativoException {
        this(origem, salarioMensal, AgendasPadrao.MENSAL_ULTIMO_DIA_UTIL);
    }

    protected EmpregadoAssalariado(String id, String nome, String endereco, BigDecimal salarioMensal,
                                   AgendaPagamento agenda)
            throws NomeNuloException, EnderecoNuloException, SalarioNuloException, SalarioNegativoException {
        super(id, nome, endereco, agenda);
        setSalarioBase(salarioMensal);
    }

    protected EmpregadoAssalariado(Empregado origem, BigDecimal salarioMensal, AgendaPagamento agenda)
            throws SalarioNuloException, SalarioNegativoException {
        super(origem, agenda);
        setSalarioBase(salarioMensal);
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.ASSALARIADO;
    }

    @Override
    public BigDecimal getSalarioBase() {
        return salarioMensal;
    }

    @Override
    public void setSalarioBase(BigDecimal salario) throws SalarioNuloException, SalarioNegativoException {
        this.salarioMensal = exigirSalarioValido(salario);
    }

    /** Parte fixa que a agenda do empregado paga em cada pagamento. */
    protected BigDecimal calcularFixo() {
        return getAgenda().calcularSalarioFixo(salarioMensal);
    }

    @Override
    protected Contracheque montarContracheque(Periodo periodo, LocalDate data) {
        return new ContrachequeAssalariado(data, getNome(), descricaoMetodoPagamento(), calcularFixo());
    }
}
