package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.agenda.AgendasPadrao;
import br.ufal.ic.p2.wepayu.excecoes.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/** Recebe por hora trabalhada; horas extras (acima de 8 por dia) valem 1,5 vez a hora normal. */
public class EmpregadoHorista extends Empregado {
    private static final long serialVersionUID = 1L;

    private static final BigDecimal ADICIONAL_HORA_EXTRA = new BigDecimal("1.5");

    private BigDecimal salarioPorHora;

    public EmpregadoHorista(String id, String nome, String endereco, BigDecimal salarioPorHora)
            throws NomeNuloException, EnderecoNuloException, SalarioNuloException, SalarioNegativoException {
        super(id, nome, endereco, AgendasPadrao.SEMANAL_SEXTA);
        setSalarioBase(salarioPorHora);
    }

    public EmpregadoHorista(Empregado origem, BigDecimal salarioPorHora)
            throws SalarioNuloException, SalarioNegativoException {
        super(origem, AgendasPadrao.SEMANAL_SEXTA);
        setSalarioBase(salarioPorHora);
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.HORISTA;
    }

    @Override
    public BigDecimal getSalarioBase() {
        return salarioPorHora;
    }

    @Override
    public void setSalarioBase(BigDecimal salario) throws SalarioNuloException, SalarioNegativoException {
        this.salarioPorHora = exigirSalarioValido(salario);
    }

    @Override
    public void lancarCartao(LocalDate data, BigDecimal horas) throws HorasNaoPositivasException {
        registrarCartao(new CartaoPonto(data, horas));
    }

    @Override
    public BigDecimal getHorasNormais(Periodo periodo) {
        return cartoesNo(periodo).stream().map(CartaoPonto::getHorasNormais).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getHorasExtras(Periodo periodo) {
        return cartoesNo(periodo).stream().map(CartaoPonto::getHorasExtras).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    protected Contracheque montarContracheque(Periodo periodo, LocalDate data) {
        BigDecimal normais = getHorasNormais(periodo);
        BigDecimal extras = getHorasExtras(periodo);
        BigDecimal bruto = normais.multiply(salarioPorHora)
                .add(extras.multiply(salarioPorHora).multiply(ADICIONAL_HORA_EXTRA))
                .setScale(2, RoundingMode.FLOOR);
        return new ContrachequeHorista(data, getNome(), descricaoMetodoPagamento(), normais, extras, bruto);
    }
}
