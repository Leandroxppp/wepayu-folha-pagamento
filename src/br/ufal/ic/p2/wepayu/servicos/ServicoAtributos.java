package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.excecoes.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.excecoes.WePayUException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.repositorio.ContextoSistema;
import br.ufal.ic.p2.wepayu.util.Formatos;

import java.util.HashMap;
import java.util.Map;

/** Consulta dos atributos de um empregado pelo nome do atributo. */
public class ServicoAtributos {

    @FunctionalInterface
    private interface Leitor {
        String ler(Empregado empregado) throws WePayUException;
    }

    private static final Map<String, Leitor> LEITORES = new HashMap<>();

    static {
        LEITORES.put("nome", Empregado::getNome);
        LEITORES.put("endereco", Empregado::getEndereco);
        LEITORES.put("tipo", e -> e.getTipo().getTexto());
        LEITORES.put("salario", e -> Formatos.formatarMoeda(e.getSalarioBase()));
        LEITORES.put("comissao", e -> Formatos.formatarMoeda(e.getComissao()));
        LEITORES.put("sindicalizado", e -> String.valueOf(e.isSindicalizado()));
        LEITORES.put("idSindicato", e -> e.getSindicato().getId());
        LEITORES.put("taxaSindical", e -> Formatos.formatarMoeda(e.getSindicato().getTaxaSindical()));
        LEITORES.put("metodoPagamento", e -> e.getMetodoPagamento().getTag());
        LEITORES.put("banco", e -> e.getMetodoPagamento().getBanco());
        LEITORES.put("agencia", e -> e.getMetodoPagamento().getAgencia());
        LEITORES.put("contaCorrente", e -> e.getMetodoPagamento().getContaCorrente());
        LEITORES.put("agendaPagamento", e -> e.getAgenda().getDescricao());
    }

    private final ContextoSistema contexto;

    public ServicoAtributos(ContextoSistema contexto) {
        this.contexto = contexto;
    }

    public String getAtributoEmpregado(String emp, String atributo) throws WePayUException {
        Empregado empregado = contexto.empregados().buscar(emp);
        Leitor leitor = atributo == null ? null : LEITORES.get(atributo);
        if (leitor == null) {
            throw new AtributoNaoExisteException();
        }
        return leitor.ler(empregado);
    }
}
