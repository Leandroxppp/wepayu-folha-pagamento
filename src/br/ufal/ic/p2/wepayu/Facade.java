package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.excecoes.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.Deque;

// Classe usada pelos testes de aceitacao (EasyAccept) - cada comando do
// script vira uma chamada de metodo aqui. Persistencia em disco e o
// undo/redo ficam nessa classe; a logica de negocio em si esta na Sistema.
public class Facade {

    private static final String ARQUIVO_PERSISTENCIA = "wepayu.db";

    private Sistema sistema;
    private final Deque<Sistema> pilhaUndo = new ArrayDeque<>();
    private final Deque<Sistema> pilhaRedo = new ArrayDeque<>();

    public Facade() {
        Sistema carregado = carregarDoDisco();
        this.sistema = (carregado != null) ? carregado : new Sistema();
    }

    // persistencia em disco e o memento usado pra undo/redo

    private static Sistema carregarDoDisco() {
        File arquivo = new File(ARQUIVO_PERSISTENCIA);
        if (!arquivo.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (Sistema) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    private void salvarNoDisco() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_PERSISTENCIA))) {
            oos.writeObject(sistema);
        } catch (Exception e) {
            // Em um sistema real trataríamos/logaríamos; aqui simplesmente ignoramos,
            // já que não há como comunicar isso de volta pelo script de testes.
        }
    }

    private static Sistema clonar(Sistema original) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(original);
            }
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
                return (Sistema) ois.readObject();
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha interna ao clonar o estado do sistema.", e);
        }
    }

    private void checarEncerrado() throws SistemaEncerrado {
        if (sistema.isEncerrado()) {
            throw new SistemaEncerrado();
        }
    }

    // comandos gerais

    public void zerarSistema() throws SistemaEncerrado {
        checarEncerrado();
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = new Sistema();
    }

    public void encerrarSistema() throws SistemaEncerrado {
        checarEncerrado();
        salvarNoDisco();
        sistema.setEncerrado(true);
    }

    public void undo() throws SistemaEncerrado, SemComandoDesfazer {
        checarEncerrado();
        if (pilhaUndo.isEmpty()) {
            throw new SemComandoDesfazer();
        }
        pilhaRedo.push(sistema);
        sistema = pilhaUndo.pop();
    }

    public void redo() throws SistemaEncerrado, SemComandoRefazer {
        checarEncerrado();
        if (pilhaRedo.isEmpty()) {
            throw new SemComandoRefazer();
        }
        pilhaUndo.push(sistema);
        sistema = pilhaRedo.pop();
    }

    public String getNumeroDeEmpregados() throws SistemaEncerrado {
        checarEncerrado();
        return String.valueOf(sistema.getNumeroDeEmpregados());
    }

    // empregados: criação, remoção, consulta e alteração

    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws SistemaEncerrado, NomeNulo, EnderecoNulo, TipoInvalido, TipoNaoAplicavel, SalarioNulo,
            SalarioNaoNumerico, SalarioNegativo {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        String id = copia.criarEmpregado(nome, endereco, tipo, salario);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
        return id;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws SistemaEncerrado, NomeNulo, EnderecoNulo, TipoInvalido, TipoNaoAplicavel, SalarioNulo,
            SalarioNaoNumerico, SalarioNegativo, ComissaoNula, ComissaoNaoNumerica, ComissaoNegativa {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        String id = copia.criarEmpregado(nome, endereco, tipo, salario, comissao);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
        return id;
    }

    public void removerEmpregado(String emp) throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        copia.removerEmpregado(emp);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
    }

    public String getEmpregadoPorNome(String nome, int indice) throws SistemaEncerrado, EmpregadoNaoEncontradoPorNome {
        checarEncerrado();
        return sistema.getEmpregadoPorNome(nome, indice);
    }

    public String getAtributoEmpregado(String emp, String atributo)
            throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste, AtributoNaoExiste,
            EmpregadoNaoComissionado, EmpregadoNaoSindicalizado, EmpregadoNaoRecebeEmBanco {
        checarEncerrado();
        return sistema.getAtributoEmpregado(emp, atributo);
    }

    public void alteraEmpregado(String emp, String atributo, String valor)
            throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste, AtributoNaoExiste, NomeNulo,
            EnderecoNulo, TipoInvalido, SalarioNulo, SalarioNaoNumerico, SalarioNegativo, EmpregadoNaoComissionado,
            ComissaoNula, ComissaoNaoNumerica, ComissaoNegativa, ValorNaoBooleano, IdentificacaoSindicatoNula,
            BancoNulo, MetodoPagamentoInvalido {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        copia.alteraEmpregado(emp, atributo, valor);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String extra)
            throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste, AtributoNaoExiste, TipoInvalido,
            SalarioNulo, SalarioNaoNumerico, SalarioNegativo, ComissaoNula, ComissaoNaoNumerica, ComissaoNegativa {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        copia.alteraEmpregado(emp, atributo, valor, extra);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical)
            throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste, AtributoNaoExiste,
            ValorNaoBooleano, IdentificacaoSindicatoNula, TaxaSindicalNula, TaxaSindicalNaoNumerica,
            TaxaSindicalNegativa, IdSindicatoDuplicado {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        copia.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
    }

    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia,
                                 String contaCorrente)
            throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste, AtributoNaoExiste,
            MetodoPagamentoInvalido, BancoNulo, AgenciaNula, ContaCorrenteNula {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        copia.alteraEmpregado(emp, atributo, valor1, banco, agencia, contaCorrente);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
    }

    // lançamentos

    public void lancaCartao(String emp, String data, String horas)
            throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoHorista,
            DataInvalida, HorasNula, HorasNaoNumerica, HorasNaoPositivas {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        copia.lancaCartao(emp, data, horas);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
    }

    public void lancaVenda(String emp, String data, String valor)
            throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoComissionado,
            DataInvalida, ValorNulo, ValorNaoNumerico, ValorNaoPositivo {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        copia.lancaVenda(emp, data, valor);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
    }

    public void lancaTaxaServico(String membro, String data, String valor)
            throws SistemaEncerrado, IdentificacaoMembroNula, MembroNaoExiste, DataInvalida, ValorNulo,
            ValorNaoNumerico, ValorNaoPositivo {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        copia.lancaTaxaServico(membro, data, valor);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
    }

    // consultas de período

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoHorista,
            DataInicialInvalida, DataFinalInvalida, DataInicialPosteriorFinal {
        checarEncerrado();
        return sistema.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal)
            throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoHorista,
            DataInicialInvalida, DataFinalInvalida, DataInicialPosteriorFinal {
        checarEncerrado();
        return sistema.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal)
            throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoComissionado,
            DataInicialInvalida, DataFinalInvalida, DataInicialPosteriorFinal {
        checarEncerrado();
        return sistema.getVendasRealizadas(emp, dataInicial, dataFinal);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal)
            throws SistemaEncerrado, IdentificacaoEmpregadoNula, EmpregadoNaoExiste, EmpregadoNaoSindicalizado,
            DataInicialInvalida, DataFinalInvalida, DataInicialPosteriorFinal {
        checarEncerrado();
        return sistema.getTaxasServico(emp, dataInicial, dataFinal);
    }

    // folha de pagamento

    public String totalFolha(String data) throws SistemaEncerrado, DataInvalida {
        checarEncerrado();
        return sistema.totalFolha(data);
    }

    public void rodaFolha(String data, String saida) throws SistemaEncerrado, DataInvalida, ErroArquivoSaida {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        copia.rodaFolha(data, saida);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
    }
}
