package br.ufal.ic.p2.wepayu;

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

    private void checarEncerrado() throws Exception {
        if (sistema.isEncerrado()) {
            throw new Exception("Nao pode dar comandos depois de encerrarSistema.");
        }
    }

    private interface Mutacao {
        void executar(Sistema s) throws Exception;
    }

    private interface MutacaoComRetorno<T> {
        T executar(Sistema s) throws Exception;
    }

    private void executarMutacao(Mutacao m) throws Exception {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        m.executar(copia);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
    }

    private <T> T executarMutacaoComRetorno(MutacaoComRetorno<T> m) throws Exception {
        checarEncerrado();
        Sistema copia = clonar(sistema);
        T resultado = m.executar(copia);
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = copia;
        return resultado;
    }

    // comandos gerais

    public void zerarSistema() throws Exception {
        checarEncerrado();
        pilhaUndo.push(sistema);
        pilhaRedo.clear();
        sistema = new Sistema();
    }

    public void encerrarSistema() throws Exception {
        checarEncerrado();
        salvarNoDisco();
        sistema.setEncerrado(true);
    }

    public void undo() throws Exception {
        checarEncerrado();
        if (pilhaUndo.isEmpty()) {
            throw new Exception("Nao ha comando a desfazer.");
        }
        pilhaRedo.push(sistema);
        sistema = pilhaUndo.pop();
    }

    public void redo() throws Exception {
        checarEncerrado();
        if (pilhaRedo.isEmpty()) {
            throw new Exception("Nao ha comando a refazer.");
        }
        pilhaUndo.push(sistema);
        sistema = pilhaRedo.pop();
    }

    public String getNumeroDeEmpregados() throws Exception {
        checarEncerrado();
        return String.valueOf(sistema.getNumeroDeEmpregados());
    }

    // empregados: criação, remoção, consulta e alteração

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        return executarMutacaoComRetorno(s -> s.criarEmpregado(nome, endereco, tipo, salario));
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws Exception {
        return executarMutacaoComRetorno(s -> s.criarEmpregado(nome, endereco, tipo, salario, comissao));
    }

    public void removerEmpregado(String emp) throws Exception {
        executarMutacao(s -> s.removerEmpregado(emp));
    }

    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        checarEncerrado();
        return sistema.getEmpregadoPorNome(nome, indice);
    }

    public String getAtributoEmpregado(String emp, String atributo) throws Exception {
        checarEncerrado();
        return sistema.getAtributoEmpregado(emp, atributo);
    }

    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception {
        executarMutacao(s -> s.alteraEmpregado(emp, atributo, valor));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String extra) throws Exception {
        executarMutacao(s -> s.alteraEmpregado(emp, atributo, valor, extra));
    }

    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical)
            throws Exception {
        executarMutacao(s -> s.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical));
    }

    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia,
                                 String contaCorrente) throws Exception {
        executarMutacao(s -> s.alteraEmpregado(emp, atributo, valor1, banco, agencia, contaCorrente));
    }

    // lançamentos

    public void lancaCartao(String emp, String data, String horas) throws Exception {
        executarMutacao(s -> s.lancaCartao(emp, data, horas));
    }

    public void lancaVenda(String emp, String data, String valor) throws Exception {
        executarMutacao(s -> s.lancaVenda(emp, data, valor));
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception {
        executarMutacao(s -> s.lancaTaxaServico(membro, data, valor));
    }

    // consultas de período

    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception {
        checarEncerrado();
        return sistema.getHorasNormaisTrabalhadas(emp, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception {
        checarEncerrado();
        return sistema.getHorasExtrasTrabalhadas(emp, dataInicial, dataFinal);
    }

    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception {
        checarEncerrado();
        return sistema.getVendasRealizadas(emp, dataInicial, dataFinal);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception {
        checarEncerrado();
        return sistema.getTaxasServico(emp, dataInicial, dataFinal);
    }

    // folha de pagamento

    public String totalFolha(String data) throws Exception {
        checarEncerrado();
        return sistema.totalFolha(data);
    }

    public void rodaFolha(String data, String saida) throws Exception {
        executarMutacao(s -> s.rodaFolha(data, saida));
    }
}
