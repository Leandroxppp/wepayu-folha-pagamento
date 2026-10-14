package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.excecoes.DataInvalidaException;
import br.ufal.ic.p2.wepayu.excecoes.ErroArquivoSaidaException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.relatorio.RelatorioFolha;
import br.ufal.ic.p2.wepayu.repositorio.ContextoSistema;
import br.ufal.ic.p2.wepayu.util.Entrada;
import br.ufal.ic.p2.wepayu.util.Formatos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Folha de pagamento: total previsto para uma data e execução da folha com geração do relatório. */
public class ServicoFolha {

    private final ContextoSistema contexto;

    public ServicoFolha(ContextoSistema contexto) {
        this.contexto = contexto;
    }

    /** Soma dos salários brutos de quem seria pago na data. Não paga ninguém. */
    public String totalFolha(String data) throws DataInvalidaException {
        LocalDate dia = Entrada.data(data);
        BigDecimal total = BigDecimal.ZERO;
        for (Empregado empregado : empregadosPagosEm(dia)) {
            total = total.add(empregado.calcularBruto(dia));
        }
        return Formatos.formatarMoeda(total);
    }

    /** Paga quem tem pagamento na data e grava o relatório no arquivo indicado. */
    public void rodaFolha(String data, String saida) throws DataInvalidaException, ErroArquivoSaidaException {
        LocalDate dia = Entrada.data(data);
        RelatorioFolha relatorio = new RelatorioFolha(dia);
        for (Empregado empregado : empregadosPagosEm(dia)) {
            empregado.pagar(dia).aceitar(relatorio);
        }
        relatorio.gravarEm(saida);
    }

    private List<Empregado> empregadosPagosEm(LocalDate dia) {
        List<Empregado> pagos = new ArrayList<>();
        for (Empregado empregado : contexto.empregados().todos()) {
            if (empregado.ehDiaDePagamento(dia)) {
                pagos.add(empregado);
            }
        }
        pagos.sort(Comparator.comparing(Empregado::getNome));
        return pagos;
    }
}
