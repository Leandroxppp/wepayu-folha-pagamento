package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.excecoes.*;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.util.Formatos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Logica de negocio do sistema de folha de pagamento (WePayU). Essa classe
// nao sabe nada sobre a linguagem de scripts do EasyAccept - quem traduz
// "comando do script -> metodo" e a Facade.
public class Sistema implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final BigDecimal OITO = new BigDecimal(8);
    private static final BigDecimal UM_E_MEIO = new BigDecimal("1.5");
    private static final LocalDate REFERENCIA_COMISSIONADO = LocalDate.of(2005, 1, 14);

    private Map<String, Empregado> empregados = new LinkedHashMap<>();
    private int proximoId = 1;

    // Não é persistido: uma nova execução do programa sempre começa "aberta".
    private transient boolean encerrado = false;

    public boolean isEncerrado() {
        return encerrado;
    }

    public void setEncerrado(boolean encerrado) {
        this.encerrado = encerrado;
    }

    public int getNumeroDeEmpregados() {
        return empregados.size();
    }

    // Criação / remoção

    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws NomeNulo, EnderecoNulo, TipoInvalido, TipoNaoAplicavel, SalarioNulo, SalarioNaoNumerico,
            SalarioNegativo {
        validarNome(nome);
        validarEndereco(endereco);
        validarTipoValido(tipo);
        if (tipo.equals("comissionado")) {
            throw new TipoNaoAplicavel();
        }
        BigDecimal sal = validarSalario(salario);
        String id = proximoId();
        Empregado e;
        if (tipo.equals("horista")) {
            e = new EmpregadoHorista(id, nome, endereco, sal);
        } else {
            e = new EmpregadoAssalariado(id, nome, endereco, sal);
        }
        empregados.put(id, e);
        return id;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws NomeNulo, EnderecoNulo, TipoInvalido, TipoNaoAplicavel, SalarioNulo, SalarioNaoNumerico,
            SalarioNegativo, ComissaoNula, ComissaoNaoNumerica, ComissaoNegativa {
        validarNome(nome);
        validarEndereco(endereco);
        validarTipoValido(tipo);
        if (!tipo.equals("comissionado")) {
            throw new TipoNaoAplicavel();
        }
        BigDecimal sal = validarSalario(salario);
        BigDecimal com = validarComissao(comissao);
        String id = proximoId();
        Empregado e = new EmpregadoComissionado(id, nome, endereco, sal, com);
        empregados.put(id, e);
        return id;
    }

    public void removerEmpregado(String emp) throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste {
        Empregado e = buscarEmpregado(emp);
        empregados.remove(e.getId());
    }

    // Consultas

    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoEncontradoPorNome {
        int contador = 0;
        for (Empregado e : empregados.values()) {
            if (e.getNome().contains(nome)) {
                contador++;
                if (contador == indice) {
                    return e.getId();
                }
            }
        }
        throw new EmpregadoNaoEncontradoPorNome();
    }

    public String getAtributoEmpregado(String emp, String atributo)
            throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste, AtributoNaoExiste, EmpregadoNaoComissionado,
            EmpregadoNaoSindicalizado, EmpregadoNaoRecebeEmBanco {
        Empregado e = buscarEmpregado(emp);
        return valorDoAtributo(e, atributo);
    }

    private String valorDoAtributo(Empregado e, String atributo)
            throws AtributoNaoExiste, EmpregadoNaoComissionado, EmpregadoNaoSindicalizado, EmpregadoNaoRecebeEmBanco {
        if (atributo == null) {
            throw new AtributoNaoExiste();
        }
        switch (atributo) {
            case "nome":
                return e.getNome();
            case "endereco":
                return e.getEndereco();
            case "tipo":
                return e.getTipo();
            case "salario":
                return Formatos.formatarMoeda(e.getSalarioBase());
            case "comissao":
                if (!(e instanceof EmpregadoComissionado)) {
                    throw new EmpregadoNaoComissionado();
                }
                return Formatos.formatarMoeda(((EmpregadoComissionado) e).getTaxaComissao());
            case "sindicalizado":
                return e.isSindicalizado() ? "true" : "false";
            case "idSindicato":
                if (!e.isSindicalizado()) {
                    throw new EmpregadoNaoSindicalizado();
                }
                return e.getSindicato().getIdMembro();
            case "taxaSindical":
                if (!e.isSindicalizado()) {
                    throw new EmpregadoNaoSindicalizado();
                }
                return Formatos.formatarMoeda(e.getSindicato().getTaxaSindical());
            case "metodoPagamento":
                return e.getMetodoPagamento().getTag();
            case "banco":
                if (!(e.getMetodoPagamento() instanceof Banco)) {
                    throw new EmpregadoNaoRecebeEmBanco();
                }
                return ((Banco) e.getMetodoPagamento()).getBanco();
            case "agencia":
                if (!(e.getMetodoPagamento() instanceof Banco)) {
                    throw new EmpregadoNaoRecebeEmBanco();
                }
                return ((Banco) e.getMetodoPagamento()).getAgencia();
            case "contaCorrente":
                if (!(e.getMetodoPagamento() instanceof Banco)) {
                    throw new EmpregadoNaoRecebeEmBanco();
                }
                return ((Banco) e.getMetodoPagamento()).getContaCorrente();
            default:
                throw new AtributoNaoExiste();
        }
    }

    // Alteração de atributos (uma sobrecarga por número de argumentos)

    public void alteraEmpregado(String emp, String atributo, String valor)
            throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste, AtributoNaoExiste, NomeNulo, EnderecoNulo,
            TipoInvalido, SalarioNulo, SalarioNaoNumerico, SalarioNegativo, EmpregadoNaoComissionado, ComissaoNula,
            ComissaoNaoNumerica, ComissaoNegativa, ValorNaoBooleano, IdentificacaoSindicatoNula, BancoNulo,
            MetodoPagamentoInvalido {
        Empregado e = buscarEmpregado(emp);
        if (atributo == null) {
            throw new AtributoNaoExiste();
        }
        switch (atributo) {
            case "nome":
                validarNome(valor);
                e.setNome(valor);
                break;
            case "endereco":
                validarEndereco(valor);
                e.setEndereco(valor);
                break;
            case "tipo":
                alterarTipoSimples(e, valor);
                break;
            case "salario": {
                BigDecimal sal = validarSalario(valor);
                e.setSalarioBase(sal);
                break;
            }
            case "comissao": {
                if (!(e instanceof EmpregadoComissionado)) {
                    throw new EmpregadoNaoComissionado();
                }
                BigDecimal com = validarComissao(valor);
                ((EmpregadoComissionado) e).setTaxaComissao(com);
                break;
            }
            case "sindicalizado": {
                if (!"true".equals(valor) && !"false".equals(valor)) {
                    throw new ValorNaoBooleano();
                }
                if ("false".equals(valor)) {
                    e.setSindicalizado(false);
                    e.setSindicato(null);
                } else {
                    // "true" sem idSindicato/taxaSindical: dados obrigatorios ausentes.
                    throw new IdentificacaoSindicatoNula();
                }
                break;
            }
            case "metodoPagamento": {
                if ("emMaos".equals(valor)) {
                    e.setMetodoPagamento(new EmMaos());
                } else if ("correios".equals(valor)) {
                    e.setMetodoPagamento(new Correios());
                } else if ("banco".equals(valor)) {
                    throw new BancoNulo();
                } else {
                    throw new MetodoPagamentoInvalido();
                }
                break;
            }
            default:
                throw new AtributoNaoExiste();
        }
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String extra)
            throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste, AtributoNaoExiste, TipoInvalido, SalarioNulo,
            SalarioNaoNumerico, SalarioNegativo, ComissaoNula, ComissaoNaoNumerica, ComissaoNegativa {
        Empregado e = buscarEmpregado(emp);
        if (!"tipo".equals(atributo)) {
            throw new AtributoNaoExiste();
        }
        if (valor == null || !(valor.equals("horista") || valor.equals("assalariado") || valor.equals("comissionado"))) {
            throw new TipoInvalido();
        }
        Empregado novo;
        if (valor.equals("horista")) {
            BigDecimal sal = validarSalario(extra);
            novo = new EmpregadoHorista(e.getId(), e.getNome(), e.getEndereco(), sal);
        } else if (valor.equals("comissionado")) {
            BigDecimal com = validarComissao(extra);
            novo = new EmpregadoComissionado(e.getId(), e.getNome(), e.getEndereco(), e.getSalarioBase(), com);
        } else {
            novo = new EmpregadoAssalariado(e.getId(), e.getNome(), e.getEndereco(), e.getSalarioBase());
        }
        copiarDadosComuns(e, novo);
        empregados.put(novo.getId(), novo);
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical)
            throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste, AtributoNaoExiste, ValorNaoBooleano,
            IdentificacaoSindicatoNula, TaxaSindicalNula, TaxaSindicalNaoNumerica, TaxaSindicalNegativa,
            IdSindicatoDuplicado {
        Empregado e = buscarEmpregado(emp);
        if (!"sindicalizado".equals(atributo)) {
            throw new AtributoNaoExiste();
        }
        if (!"true".equals(valor) && !"false".equals(valor)) {
            throw new ValorNaoBooleano();
        }
        if (idSindicato == null || idSindicato.trim().isEmpty()) {
            throw new IdentificacaoSindicatoNula();
        }
        BigDecimal taxa = validarTaxaSindical(taxaSindical);
        for (Empregado outro : empregados.values()) {
            if (outro != e && outro.isSindicalizado() && outro.getSindicato() != null
                    && idSindicato.equals(outro.getSindicato().getIdMembro())) {
                throw new IdSindicatoDuplicado();
            }
        }
        MembroSindicato ms = new MembroSindicato(idSindicato, taxa);
        ms.setUltimoPagamentoSindical(e.getUltimoPagamento());
        e.setSindicalizado(true);
        e.setSindicato(ms);
    }

    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia,
                                 String contaCorrente)
            throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste, AtributoNaoExiste, MetodoPagamentoInvalido,
            BancoNulo, AgenciaNula, ContaCorrenteNula {
        Empregado e = buscarEmpregado(emp);
        if (!"metodoPagamento".equals(atributo)) {
            throw new AtributoNaoExiste();
        }
        if (!"banco".equals(valor1)) {
            throw new MetodoPagamentoInvalido();
        }
        if (banco == null || banco.trim().isEmpty()) {
            throw new BancoNulo();
        }
        if (agencia == null || agencia.trim().isEmpty()) {
            throw new AgenciaNula();
        }
        if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
            throw new ContaCorrenteNula();
        }
        e.setMetodoPagamento(new Banco(banco, agencia, contaCorrente));
    }

    private void alterarTipoSimples(Empregado e, String valor)
            throws TipoInvalido, SalarioNulo, SalarioNaoNumerico, SalarioNegativo {
        if (valor == null || !(valor.equals("horista") || valor.equals("assalariado") || valor.equals("comissionado"))) {
            throw new TipoInvalido();
        }
        BigDecimal salarioAnterior = e.getSalarioBase();
        Empregado novo;
        if (valor.equals("horista")) {
            novo = new EmpregadoHorista(e.getId(), e.getNome(), e.getEndereco(), salarioAnterior);
        } else if (valor.equals("comissionado")) {
            novo = new EmpregadoComissionado(e.getId(), e.getNome(), e.getEndereco(), salarioAnterior, BigDecimal.ZERO);
        } else {
            novo = new EmpregadoAssalariado(e.getId(), e.getNome(), e.getEndereco(), salarioAnterior);
        }
        copiarDadosComuns(e, novo);
        empregados.put(novo.getId(), novo);
    }

    private void copiarDadosComuns(Empregado origem, Empregado destino) {
        destino.setSindicalizado(origem.isSindicalizado());
        destino.setSindicato(origem.getSindicato());
        destino.setMetodoPagamento(origem.getMetodoPagamento());
        destino.setUltimoPagamento(origem.getUltimoPagamento());
    }

    // Lançamentos

    public void lancaCartao(String emp, String data, String horas)
            throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoHorista, DataInvalida, HorasNula,
            HorasNaoNumerica, HorasNaoPositivas {
        Empregado e = buscarEmpregado(emp);
        if (!(e instanceof EmpregadoHorista)) {
            throw new EmpregadoNaoHorista();
        }
        LocalDate d = validarData(data);
        BigDecimal h = validarHoras(horas);
        ((EmpregadoHorista) e).getCartoes().add(new CartaoPonto(d, h));
    }

    public void lancaVenda(String emp, String data, String valor)
            throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoComissionado, DataInvalida, ValorNulo,
            ValorNaoNumerico, ValorNaoPositivo {
        Empregado e = buscarEmpregado(emp);
        if (!(e instanceof EmpregadoComissionado)) {
            throw new EmpregadoNaoComissionado();
        }
        LocalDate d = validarData(data);
        BigDecimal v = validarValorLancamento(valor);
        ((EmpregadoComissionado) e).getVendas().add(new ResultadoVenda(d, v));
    }

    public void lancaTaxaServico(String membro, String data, String valor)
            throws IdentificacaoMembroNula, MembroNaoExiste, DataInvalida, ValorNulo, ValorNaoNumerico,
            ValorNaoPositivo {
        Empregado e = buscarPorIdSindicato(membro);
        LocalDate d = validarData(data);
        BigDecimal v = validarValorLancamento(valor);
        e.getSindicato().getTaxasServico().add(new TaxaServico(d, v));
    }

    // Consultas de período (intervalo [dataInicial, dataFinal) )

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoHorista, DataInicialInvalida,
            DataFinalInvalida, DataInicialPosteriorFinal {
        Empregado e = buscarEmpregado(emp);
        if (!(e instanceof EmpregadoHorista)) {
            throw new EmpregadoNaoHorista();
        }
        LocalDate di = validarDataInicial(dataInicial);
        LocalDate df = validarDataFinal(dataFinal);
        if (di.isAfter(df)) {
            throw new DataInicialPosteriorFinal();
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CartaoPonto c : ((EmpregadoHorista) e).getCartoes()) {
            if (!c.getData().isBefore(di) && c.getData().isBefore(df)) {
                BigDecimal horas = c.getHoras();
                total = total.add(horas.compareTo(OITO) > 0 ? OITO : horas);
            }
        }
        return Formatos.formatarHoras(total);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoHorista, DataInicialInvalida,
            DataFinalInvalida, DataInicialPosteriorFinal {
        Empregado e = buscarEmpregado(emp);
        if (!(e instanceof EmpregadoHorista)) {
            throw new EmpregadoNaoHorista();
        }
        LocalDate di = validarDataInicial(dataInicial);
        LocalDate df = validarDataFinal(dataFinal);
        if (di.isAfter(df)) {
            throw new DataInicialPosteriorFinal();
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CartaoPonto c : ((EmpregadoHorista) e).getCartoes()) {
            if (!c.getData().isBefore(di) && c.getData().isBefore(df)) {
                BigDecimal horas = c.getHoras();
                if (horas.compareTo(OITO) > 0) {
                    total = total.add(horas.subtract(OITO));
                }
            }
        }
        return Formatos.formatarHoras(total);
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoComissionado, DataInicialInvalida,
            DataFinalInvalida, DataInicialPosteriorFinal {
        Empregado e = buscarEmpregado(emp);
        if (!(e instanceof EmpregadoComissionado)) {
            throw new EmpregadoNaoComissionado();
        }
        LocalDate di = validarDataInicial(dataInicial);
        LocalDate df = validarDataFinal(dataFinal);
        if (di.isAfter(df)) {
            throw new DataInicialPosteriorFinal();
        }
        BigDecimal total = BigDecimal.ZERO;
        for (ResultadoVenda v : ((EmpregadoComissionado) e).getVendas()) {
            if (!v.getData().isBefore(di) && v.getData().isBefore(df)) {
                total = total.add(v.getValor());
            }
        }
        return Formatos.formatarMoeda(total);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal)
            throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoSindicalizado, DataInicialInvalida,
            DataFinalInvalida, DataInicialPosteriorFinal {
        Empregado e = buscarEmpregado(emp);
        if (!e.isSindicalizado()) {
            throw new EmpregadoNaoSindicalizado();
        }
        LocalDate di = validarDataInicial(dataInicial);
        LocalDate df = validarDataFinal(dataFinal);
        if (di.isAfter(df)) {
            throw new DataInicialPosteriorFinal();
        }
        BigDecimal total = BigDecimal.ZERO;
        for (TaxaServico t : e.getSindicato().getTaxasServico()) {
            if (!t.getData().isBefore(di) && t.getData().isBefore(df)) {
                total = total.add(t.getValor());
            }
        }
        return Formatos.formatarMoeda(total);
    }

    // Folha de pagamento

    private boolean elegivelHorista(LocalDate data) {
        return data.getDayOfWeek() == DayOfWeek.FRIDAY;
    }

    private boolean elegivelAssalariado(LocalDate data) {
        return data.equals(Formatos.ultimoDiaUtilDoMes(data.getYear(), data.getMonthValue()));
    }

    private boolean elegivelComissionado(LocalDate data) {
        if (data.getDayOfWeek() != DayOfWeek.FRIDAY) {
            return false;
        }
        long dias = ChronoUnit.DAYS.between(REFERENCIA_COMISSIONADO, data);
        return Math.floorMod(dias, 14) == 0;
    }

    // desconto[0] e liquido[1], considerando o teto de "contracheque nunca negativo"
    private BigDecimal[] calcularDescontos(Empregado e, LocalDate data, BigDecimal bruto, boolean persistir) {
        BigDecimal desconto = BigDecimal.ZERO;
        if (e.isSindicalizado()) {
            MembroSindicato ms = e.getSindicato();
            long dias = ChronoUnit.DAYS.between(ms.getUltimoPagamentoSindical(), data);
            BigDecimal duesSindical = ms.getTaxaSindical().multiply(BigDecimal.valueOf(dias));
            BigDecimal servicosPendentes = BigDecimal.ZERO;
            List<TaxaServico> pendentes = new ArrayList<>();
            for (TaxaServico ts : ms.getTaxasServico()) {
                if (!ts.isPaga() && !ts.getData().isAfter(data)) {
                    servicosPendentes = servicosPendentes.add(ts.getValor());
                    pendentes.add(ts);
                }
            }
            BigDecimal totalDevido = duesSindical.add(servicosPendentes).setScale(2, RoundingMode.FLOOR);
            if (totalDevido.compareTo(bruto) > 0) {
                desconto = bruto;
            } else {
                desconto = totalDevido;
                if (persistir) {
                    ms.setUltimoPagamentoSindical(data);
                    for (TaxaServico ts : pendentes) {
                        ts.setPaga(true);
                    }
                }
            }
        }
        BigDecimal liquido = bruto.subtract(desconto).setScale(2, RoundingMode.FLOOR);
        return new BigDecimal[]{desconto, liquido};
    }

    // {horasNormais, horasExtras, bruto, desconto, liquido}
    private BigDecimal[] calcularHorista(EmpregadoHorista h, LocalDate data, boolean persistir) {
        if (data.equals(h.getUltimaDataFolha())) {
            return h.getUltimoResultadoFolha();
        }
        LocalDate pointer = h.getUltimoPagamento();
        BigDecimal horasNormais = BigDecimal.ZERO;
        BigDecimal horasExtras = BigDecimal.ZERO;
        for (CartaoPonto c : h.getCartoes()) {
            if (c.getData().isAfter(pointer) && !c.getData().isAfter(data)) {
                BigDecimal horas = c.getHoras();
                if (horas.compareTo(OITO) > 0) {
                    horasNormais = horasNormais.add(OITO);
                    horasExtras = horasExtras.add(horas.subtract(OITO));
                } else {
                    horasNormais = horasNormais.add(horas);
                }
            }
        }
        BigDecimal bruto = horasNormais.multiply(h.getSalarioBase())
                .add(horasExtras.multiply(h.getSalarioBase()).multiply(UM_E_MEIO))
                .setScale(2, RoundingMode.FLOOR);
        BigDecimal[] dl = calcularDescontos(h, data, bruto, persistir);
        BigDecimal[] resultado = new BigDecimal[]{horasNormais, horasExtras, bruto, dl[0], dl[1]};
        if (persistir) {
            h.setUltimoPagamento(data);
            h.setUltimaDataFolha(data);
            h.setUltimoResultadoFolha(resultado);
        }
        return resultado;
    }

    // {bruto, desconto, liquido}
    private BigDecimal[] calcularAssalariado(EmpregadoAssalariado a, LocalDate data, boolean persistir) {
        if (data.equals(a.getUltimaDataFolha())) {
            return a.getUltimoResultadoFolha();
        }
        BigDecimal bruto = a.getSalarioBase().setScale(2, RoundingMode.FLOOR);
        BigDecimal[] dl = calcularDescontos(a, data, bruto, persistir);
        BigDecimal[] resultado = new BigDecimal[]{bruto, dl[0], dl[1]};
        if (persistir) {
            a.setUltimoPagamento(data);
            a.setUltimaDataFolha(data);
            a.setUltimoResultadoFolha(resultado);
        }
        return resultado;
    }

    // {fixo, vendas, comissao, bruto, desconto, liquido}
    private BigDecimal[] calcularComissionado(EmpregadoComissionado c, LocalDate data, boolean persistir) {
        if (data.equals(c.getUltimaDataFolha())) {
            return c.getUltimoResultadoFolha();
        }
        LocalDate pointer = c.getUltimoPagamento();
        BigDecimal vendas = BigDecimal.ZERO;
        for (ResultadoVenda v : c.getVendas()) {
            if (v.getData().isAfter(pointer) && !v.getData().isAfter(data)) {
                vendas = vendas.add(v.getValor());
            }
        }
        vendas = vendas.setScale(2, RoundingMode.FLOOR);
        BigDecimal fixo = c.getSalarioBase().multiply(new BigDecimal(24))
                .divide(new BigDecimal(52), 10, RoundingMode.FLOOR)
                .setScale(2, RoundingMode.FLOOR);
        BigDecimal comissao = vendas.multiply(c.getTaxaComissao()).setScale(2, RoundingMode.FLOOR);
        BigDecimal bruto = fixo.add(comissao).setScale(2, RoundingMode.FLOOR);
        BigDecimal[] dl = calcularDescontos(c, data, bruto, persistir);
        BigDecimal[] resultado = new BigDecimal[]{fixo, vendas, comissao, bruto, dl[0], dl[1]};
        if (persistir) {
            c.setUltimoPagamento(data);
            c.setUltimaDataFolha(data);
            c.setUltimoResultadoFolha(resultado);
        }
        return resultado;
    }

    public String totalFolha(String data) throws DataInvalida {
        LocalDate d = validarData(data);
        BigDecimal total = BigDecimal.ZERO;
        for (Empregado e : empregados.values()) {
            if (e instanceof EmpregadoComissionado) {
                if (elegivelComissionado(d)) {
                    BigDecimal[] r = calcularComissionado((EmpregadoComissionado) e, d, false);
                    total = total.add(r[3]);
                }
            } else if (e instanceof EmpregadoHorista) {
                if (elegivelHorista(d)) {
                    BigDecimal[] r = calcularHorista((EmpregadoHorista) e, d, false);
                    total = total.add(r[2]);
                }
            } else if (e instanceof EmpregadoAssalariado) {
                if (elegivelAssalariado(d)) {
                    BigDecimal[] r = calcularAssalariado((EmpregadoAssalariado) e, d, false);
                    total = total.add(r[0]);
                }
            }
        }
        return Formatos.formatarMoeda(total);
    }

    public void rodaFolha(String data, String saida) throws DataInvalida, ErroArquivoSaida {
        LocalDate d = validarData(data);

        List<EmpregadoHorista> horistas = new ArrayList<>();
        List<EmpregadoAssalariado> assalariados = new ArrayList<>();
        List<EmpregadoComissionado> comissionados = new ArrayList<>();
        for (Empregado e : empregados.values()) {
            if (e instanceof EmpregadoComissionado) {
                if (elegivelComissionado(d)) {
                    comissionados.add((EmpregadoComissionado) e);
                }
            } else if (e instanceof EmpregadoHorista) {
                if (elegivelHorista(d)) {
                    horistas.add((EmpregadoHorista) e);
                }
            } else if (e instanceof EmpregadoAssalariado) {
                if (elegivelAssalariado(d)) {
                    assalariados.add((EmpregadoAssalariado) e);
                }
            }
        }
        horistas.sort(Comparator.comparing(Empregado::getNome));
        assalariados.sort(Comparator.comparing(Empregado::getNome));
        comissionados.sort(Comparator.comparing(Empregado::getNome));

        StringBuilder sb = new StringBuilder();
        String titulo = "FOLHA DE PAGAMENTO DO DIA " + d.toString();
        linha(sb, titulo);
        linha(sb, repete('=', titulo.length()));
        linha(sb, "");

        BigDecimal totalGeral = BigDecimal.ZERO;

        // secao dos horistas
        int[] wH = {36, 5, 5, 13, 9, 15};
        linha(sb, borda());
        linha(sb, tituloSecao("HORISTAS"));
        linha(sb, borda());
        linha(sb, cabecalho(new String[]{"Nome", "Horas", "Extra", "Salario Bruto", "Descontos", "Salario Liquido"}, wH));
        linha(sb, separador(wH));
        BigDecimal totHoras = BigDecimal.ZERO, totExtra = BigDecimal.ZERO, totBrutoH = BigDecimal.ZERO,
                totDescH = BigDecimal.ZERO, totLiqH = BigDecimal.ZERO;
        for (EmpregadoHorista h : horistas) {
            BigDecimal[] r = calcularHorista(h, d, true);
            totHoras = totHoras.add(r[0]);
            totExtra = totExtra.add(r[1]);
            totBrutoH = totBrutoH.add(r[2]);
            totDescH = totDescH.add(r[3]);
            totLiqH = totLiqH.add(r[4]);
            linha(sb, linhaHorista(h, r, wH));
        }
        linha(sb, "");
        linha(sb, totalHorista(totHoras, totExtra, totBrutoH, totDescH, totLiqH, wH));
        linha(sb, "");
        totalGeral = totalGeral.add(totBrutoH);

        // secao dos assalariados
        int[] wA = {48, 13, 9, 15};
        linha(sb, borda());
        linha(sb, tituloSecao("ASSALARIADOS"));
        linha(sb, borda());
        linha(sb, cabecalho(new String[]{"Nome", "Salario Bruto", "Descontos", "Salario Liquido"}, wA));
        linha(sb, separador(wA));
        BigDecimal totBrutoA = BigDecimal.ZERO, totDescA = BigDecimal.ZERO, totLiqA = BigDecimal.ZERO;
        for (EmpregadoAssalariado a : assalariados) {
            BigDecimal[] r = calcularAssalariado(a, d, true);
            totBrutoA = totBrutoA.add(r[0]);
            totDescA = totDescA.add(r[1]);
            totLiqA = totLiqA.add(r[2]);
            linha(sb, linhaAssalariado(a, r, wA));
        }
        linha(sb, "");
        linha(sb, totalAssalariado(totBrutoA, totDescA, totLiqA, wA));
        linha(sb, "");
        totalGeral = totalGeral.add(totBrutoA);

        // secao dos comissionados
        int[] wC = {21, 8, 8, 8, 13, 9, 15};
        linha(sb, borda());
        linha(sb, tituloSecao("COMISSIONADOS"));
        linha(sb, borda());
        linha(sb, cabecalho(new String[]{"Nome", "Fixo", "Vendas", "Comissao", "Salario Bruto", "Descontos",
                "Salario Liquido"}, wC));
        linha(sb, separador(wC));
        BigDecimal totFixoC = BigDecimal.ZERO, totVendasC = BigDecimal.ZERO, totComC = BigDecimal.ZERO,
                totBrutoC = BigDecimal.ZERO, totDescC = BigDecimal.ZERO, totLiqC = BigDecimal.ZERO;
        for (EmpregadoComissionado c : comissionados) {
            BigDecimal[] r = calcularComissionado(c, d, true);
            totFixoC = totFixoC.add(r[0]);
            totVendasC = totVendasC.add(r[1]);
            totComC = totComC.add(r[2]);
            totBrutoC = totBrutoC.add(r[3]);
            totDescC = totDescC.add(r[4]);
            totLiqC = totLiqC.add(r[5]);
            linha(sb, linhaComissionado(c, r, wC));
        }
        linha(sb, "");
        linha(sb, totalComissionado(totFixoC, totVendasC, totComC, totBrutoC, totDescC, totLiqC, wC));
        linha(sb, "");
        totalGeral = totalGeral.add(totBrutoC);

        sb.append("TOTAL FOLHA: ").append(Formatos.formatarMoeda(totalGeral)).append("\r\n");

        escreverArquivo(saida, sb.toString());
    }

    private void escreverArquivo(String caminho, String conteudo) throws ErroArquivoSaida {
        try (FileWriter fw = new FileWriter(caminho)) {
            fw.write(conteudo);
        } catch (IOException ex) {
            throw new ErroArquivoSaida();
        }
    }

    private static void linha(StringBuilder sb, String texto) {
        sb.append(texto).append("\r\n");
    }

    private static String repete(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static String borda() {
        return repete('=', 127);
    }

    private static String tituloSecao(String nome) {
        String meio = " " + nome + " ";
        String esquerda = repete('=', 21);
        String direita = repete('=', 127 - 21 - meio.length());
        return esquerda + meio + direita;
    }

    private static String cabecalho(String[] labels, int[] widths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < labels.length; i++) {
            sb.append(padRight(labels[i], widths[i]));
            sb.append(' ');
        }
        sb.append("Metodo");
        return sb.toString();
    }

    private static String separador(int[] widths) {
        StringBuilder sb = new StringBuilder();
        for (int w : widths) {
            sb.append(repete('=', w));
            sb.append(' ');
        }
        sb.append(repete('=', 38));
        return sb.toString();
    }

    private static String padRight(String s, int width) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static String padLeft(String s, int width) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width - s.length(); i++) {
            sb.append(' ');
        }
        sb.append(s);
        return sb.toString();
    }

    private String linhaHorista(EmpregadoHorista h, BigDecimal[] r, int[] w) {
        String horas = Formatos.formatarHoras(r[0]);
        String extra = Formatos.formatarHoras(r[1]);
        String bruto = Formatos.formatarMoeda(r[2]);
        String desc = Formatos.formatarMoeda(r[3]);
        String liq = Formatos.formatarMoeda(r[4]);
        String metodo = h.getMetodoPagamento().descricao(h.getEndereco());
        return padRight(h.getNome(), w[0]) + " " + padLeft(horas, w[1]) + " " + padLeft(extra, w[2]) + " "
                + padLeft(bruto, w[3]) + " " + padLeft(desc, w[4]) + " " + padLeft(liq, w[5]) + " " + metodo;
    }

    private String totalHorista(BigDecimal horas, BigDecimal extra, BigDecimal bruto, BigDecimal desc,
                                 BigDecimal liq, int[] w) {
        return padRight("TOTAL HORISTAS", w[0]) + " " + padLeft(Formatos.formatarHoras(horas), w[1]) + " "
                + padLeft(Formatos.formatarHoras(extra), w[2]) + " " + padLeft(Formatos.formatarMoeda(bruto), w[3])
                + " " + padLeft(Formatos.formatarMoeda(desc), w[4]) + " " + padLeft(Formatos.formatarMoeda(liq), w[5]);
    }

    private String linhaAssalariado(EmpregadoAssalariado a, BigDecimal[] r, int[] w) {
        String bruto = Formatos.formatarMoeda(r[0]);
        String desc = Formatos.formatarMoeda(r[1]);
        String liq = Formatos.formatarMoeda(r[2]);
        String metodo = a.getMetodoPagamento().descricao(a.getEndereco());
        return padRight(a.getNome(), w[0]) + " " + padLeft(bruto, w[1]) + " " + padLeft(desc, w[2]) + " "
                + padLeft(liq, w[3]) + " " + metodo;
    }

    private String totalAssalariado(BigDecimal bruto, BigDecimal desc, BigDecimal liq, int[] w) {
        return padRight("TOTAL ASSALARIADOS", w[0]) + " " + padLeft(Formatos.formatarMoeda(bruto), w[1]) + " "
                + padLeft(Formatos.formatarMoeda(desc), w[2]) + " " + padLeft(Formatos.formatarMoeda(liq), w[3]);
    }

    private String linhaComissionado(EmpregadoComissionado c, BigDecimal[] r, int[] w) {
        String fixo = Formatos.formatarMoeda(r[0]);
        String vendas = Formatos.formatarMoeda(r[1]);
        String comissao = Formatos.formatarMoeda(r[2]);
        String bruto = Formatos.formatarMoeda(r[3]);
        String desc = Formatos.formatarMoeda(r[4]);
        String liq = Formatos.formatarMoeda(r[5]);
        String metodo = c.getMetodoPagamento().descricao(c.getEndereco());
        return padRight(c.getNome(), w[0]) + " " + padLeft(fixo, w[1]) + " " + padLeft(vendas, w[2]) + " "
                + padLeft(comissao, w[3]) + " " + padLeft(bruto, w[4]) + " " + padLeft(desc, w[5]) + " "
                + padLeft(liq, w[6]) + " " + metodo;
    }

    private String totalComissionado(BigDecimal fixo, BigDecimal vendas, BigDecimal comissao, BigDecimal bruto,
                                      BigDecimal desc, BigDecimal liq, int[] w) {
        return padRight("TOTAL COMISSIONADOS", w[0]) + " " + padLeft(Formatos.formatarMoeda(fixo), w[1]) + " "
                + padLeft(Formatos.formatarMoeda(vendas), w[2]) + " " + padLeft(Formatos.formatarMoeda(comissao), w[3])
                + " " + padLeft(Formatos.formatarMoeda(bruto), w[4]) + " " + padLeft(Formatos.formatarMoeda(desc), w[5])
                + " " + padLeft(Formatos.formatarMoeda(liq), w[6]);
    }

    // Auxiliares de validação - cada um decide qual excecao especifica lancar

    private Empregado buscarEmpregado(String emp) throws IdentificacaoEmpregadoNula, EmpregadoNaoExiste {
        if (emp == null || emp.trim().isEmpty()) {
            throw new IdentificacaoEmpregadoNula();
        }
        Empregado e = empregados.get(emp);
        if (e == null) {
            throw new EmpregadoNaoExiste();
        }
        return e;
    }

    private Empregado buscarPorIdSindicato(String membro) throws IdentificacaoMembroNula, MembroNaoExiste {
        if (membro == null || membro.trim().isEmpty()) {
            throw new IdentificacaoMembroNula();
        }
        for (Empregado e : empregados.values()) {
            if (e.isSindicalizado() && e.getSindicato() != null && membro.equals(e.getSindicato().getIdMembro())) {
                return e;
            }
        }
        throw new MembroNaoExiste();
    }

    private void validarNome(String nome) throws NomeNulo {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NomeNulo();
        }
    }

    private void validarEndereco(String endereco) throws EnderecoNulo {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new EnderecoNulo();
        }
    }

    private void validarTipoValido(String tipo) throws TipoInvalido {
        if (tipo == null || !(tipo.equals("horista") || tipo.equals("assalariado") || tipo.equals("comissionado"))) {
            throw new TipoInvalido();
        }
    }

    private BigDecimal validarSalario(String s) throws SalarioNulo, SalarioNaoNumerico, SalarioNegativo {
        if (s == null || s.trim().isEmpty()) {
            throw new SalarioNulo();
        }
        BigDecimal v = Formatos.tentarParseNumero(s);
        if (v == null) {
            throw new SalarioNaoNumerico();
        }
        if (v.compareTo(BigDecimal.ZERO) < 0) {
            throw new SalarioNegativo();
        }
        return v;
    }

    private BigDecimal validarComissao(String s) throws ComissaoNula, ComissaoNaoNumerica, ComissaoNegativa {
        if (s == null || s.trim().isEmpty()) {
            throw new ComissaoNula();
        }
        BigDecimal v = Formatos.tentarParseNumero(s);
        if (v == null) {
            throw new ComissaoNaoNumerica();
        }
        if (v.compareTo(BigDecimal.ZERO) < 0) {
            throw new ComissaoNegativa();
        }
        return v;
    }

    private BigDecimal validarTaxaSindical(String s)
            throws TaxaSindicalNula, TaxaSindicalNaoNumerica, TaxaSindicalNegativa {
        if (s == null || s.trim().isEmpty()) {
            throw new TaxaSindicalNula();
        }
        BigDecimal v = Formatos.tentarParseNumero(s);
        if (v == null) {
            throw new TaxaSindicalNaoNumerica();
        }
        if (v.compareTo(BigDecimal.ZERO) < 0) {
            throw new TaxaSindicalNegativa();
        }
        return v;
    }

    private BigDecimal validarHoras(String s) throws HorasNula, HorasNaoNumerica, HorasNaoPositivas {
        if (s == null || s.trim().isEmpty()) {
            throw new HorasNula();
        }
        BigDecimal v = Formatos.tentarParseNumero(s);
        if (v == null) {
            throw new HorasNaoNumerica();
        }
        if (v.compareTo(BigDecimal.ZERO) <= 0) {
            throw new HorasNaoPositivas();
        }
        return v;
    }

    private BigDecimal validarValorLancamento(String s) throws ValorNulo, ValorNaoNumerico, ValorNaoPositivo {
        if (s == null || s.trim().isEmpty()) {
            throw new ValorNulo();
        }
        BigDecimal v = Formatos.tentarParseNumero(s);
        if (v == null) {
            throw new ValorNaoNumerico();
        }
        if (v.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorNaoPositivo();
        }
        return v;
    }

    private LocalDate validarData(String s) throws DataInvalida {
        LocalDate d = Formatos.tentarParseData(s);
        if (d == null) {
            throw new DataInvalida();
        }
        return d;
    }

    private LocalDate validarDataInicial(String s) throws DataInicialInvalida {
        LocalDate d = Formatos.tentarParseData(s);
        if (d == null) {
            throw new DataInicialInvalida();
        }
        return d;
    }

    private LocalDate validarDataFinal(String s) throws DataFinalInvalida {
        LocalDate d = Formatos.tentarParseData(s);
        if (d == null) {
            throw new DataFinalInvalida();
        }
        return d;
    }

    private String proximoId() {
        String id = String.valueOf(proximoId);
        proximoId++;
        return id;
    }
}
