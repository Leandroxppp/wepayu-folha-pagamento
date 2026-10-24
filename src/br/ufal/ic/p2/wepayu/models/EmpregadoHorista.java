package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.agenda.AgendasPadrao;
import br.ufal.ic.p2.wepayu.excecoes.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class EmpregadoHorista extends Empregado {
    private static final long serialVersionUID = 1L;
    private static final BigDecimal OITO = new BigDecimal("8");
    private static final BigDecimal UM_E_MEIO = new BigDecimal("1.5");

    private BigDecimal salarioPorHora;

    public EmpregadoHorista(String id, String nome, String endereco, BigDecimal salarioPorHora)
            throws NomeNuloException, EnderecoNuloException, SalarioNuloException, SalarioNegativoException {
        super(id, nome, endereco, AgendasPadrao.SEMANAL_SEXTA);
        setSalarioBase(salarioPorHora);
    }

    public EmpregadoHorista(Empregado origem, BigDecimal salarioPorHora) throws SalarioNuloException, SalarioNegativoException {
        super(origem, AgendasPadrao.SEMANAL_SEXTA);
        setSalarioBase(salarioPorHora);
    }

    @Override public TipoEmpregado getTipo() { return TipoEmpregado.HORISTA; }
    @Override public BigDecimal getSalarioBase() { return salarioPorHora; }

    @Override
    public void setSalarioBase(BigDecimal valor) throws SalarioNuloException, SalarioNegativoException {
        if (valor == null) throw new SalarioNuloException();
        if (valor.compareTo(BigDecimal.ZERO) < 0) throw new SalarioNegativoException();
        salarioPorHora = valor;
        limparCacheFolha();
    }

    @Override
    public void lancarCartao(LocalDate data, BigDecimal horas) throws HorasNaoPositivasException {
        if (horas.compareTo(BigDecimal.ZERO) <= 0) throw new HorasNaoPositivasException();
        registrarCartao(new CartaoPonto(data, horas));
    }

    @Override
    public BigDecimal getHorasNormais(Periodo periodo) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartaoPonto cartao : cartoesNo(periodo)) {
            total = total.add(cartao.getHoras().min(OITO));
        }
        return total;
    }

    @Override
    public BigDecimal getHorasExtras(Periodo periodo) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartaoPonto cartao : cartoesNo(periodo)) {
            if (cartao.getHoras().compareTo(OITO) > 0) total = total.add(cartao.getHoras().subtract(OITO));
        }
        return total;
    }

    @Override
    protected Contracheque calcularValores(LocalDate data) {
        Periodo periodo = periodoDesdeUltimoPagamento(data);
        BigDecimal normais = getHorasNormais(periodo);
        BigDecimal extras = getHorasExtras(periodo);
        BigDecimal bruto = normais.multiply(salarioPorHora)
                .add(extras.multiply(salarioPorHora).multiply(UM_E_MEIO))
                .setScale(2, RoundingMode.FLOOR);
        return new ContrachequeHorista(data, getNome(), descricaoMetodoPagamento(), normais, extras, bruto);
    }
}
