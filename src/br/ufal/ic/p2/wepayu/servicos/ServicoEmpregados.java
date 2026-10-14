package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.excecoes.*;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.FabricaEmpregados;
import br.ufal.ic.p2.wepayu.models.TipoEmpregado;
import br.ufal.ic.p2.wepayu.repositorio.ContextoSistema;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.util.Entrada;

import java.math.BigDecimal;

/** Cadastro de empregados: criação, remoção e busca por nome. */
public class ServicoEmpregados {

    private final ContextoSistema contexto;

    public ServicoEmpregados(ContextoSistema contexto) {
        this.contexto = contexto;
    }

    /** Cria horista ou assalariado. */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws WePayUException {
        TipoEmpregado tipoEmpregado = TipoEmpregado.de(tipo);
        if (tipoEmpregado == TipoEmpregado.COMISSIONADO) {
            throw new TipoNaoAplicavelException();
        }
        return cadastrar(tipoEmpregado, nome, endereco, Entrada.salario(salario), null);
    }

    /** Cria comissionado. */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws WePayUException {
        TipoEmpregado tipoEmpregado = TipoEmpregado.de(tipo);
        if (tipoEmpregado != TipoEmpregado.COMISSIONADO) {
            throw new TipoNaoAplicavelException();
        }
        return cadastrar(tipoEmpregado, nome, endereco, Entrada.salario(salario), Entrada.comissao(comissao));
    }

    public void removerEmpregado(String emp) throws WePayUException {
        RepositorioEmpregados repositorio = contexto.empregados();
        repositorio.remover(repositorio.buscar(emp));
    }

    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoEncontradoPorNomeException {
        return contexto.empregados().buscarPorNome(nome, indice).getId();
    }

    public int getNumeroDeEmpregados() {
        return contexto.empregados().quantidade();
    }

    private String cadastrar(TipoEmpregado tipo, String nome, String endereco, BigDecimal salario,
                             BigDecimal comissao) throws WePayUException {
        RepositorioEmpregados repositorio = contexto.empregados();
        Empregado empregado = FabricaEmpregados.criar(tipo, repositorio.gerarId(), nome, endereco, salario, comissao);
        repositorio.adicionar(empregado);
        return empregado.getId();
    }
}
