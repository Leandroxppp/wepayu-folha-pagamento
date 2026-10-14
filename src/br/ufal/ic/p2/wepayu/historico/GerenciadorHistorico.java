package br.ufal.ic.p2.wepayu.historico;

import br.ufal.ic.p2.wepayu.excecoes.SemComandoDesfazerException;
import br.ufal.ic.p2.wepayu.excecoes.SemComandoRefazerException;
import br.ufal.ic.p2.wepayu.excecoes.WePayUException;
import br.ufal.ic.p2.wepayu.persistencia.Serializacao;
import br.ufal.ic.p2.wepayu.repositorio.BaseDados;
import br.ufal.ic.p2.wepayu.repositorio.ContextoSistema;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Desfazer e refazer. Antes de cada comando guarda uma foto (cópia serializada)
 * da base de dados; se o comando falhar a base volta a ser exatamente a que era,
 * e se der certo a foto vai para a pilha de desfazer.
 */
public class GerenciadorHistorico {

    private final ContextoSistema contexto;
    private final Deque<byte[]> pilhaDesfazer = new ArrayDeque<>();
    private final Deque<byte[]> pilhaRefazer = new ArrayDeque<>();

    public GerenciadorHistorico(ContextoSistema contexto) {
        this.contexto = contexto;
    }

    public <T> T executar(Comando<T> comando) throws WePayUException {
        byte[] antes = tirarFoto();
        T resultado;
        try {
            resultado = comando.executar();
        } catch (WePayUException | RuntimeException e) {
            contexto.setBase(restaurar(antes));
            throw e;
        }
        pilhaDesfazer.push(antes);
        pilhaRefazer.clear();
        return resultado;
    }

    public void executar(Acao acao) throws WePayUException {
        executar(() -> {
            acao.executar();
            return null;
        });
    }

    public void desfazer() throws SemComandoDesfazerException {
        if (pilhaDesfazer.isEmpty()) {
            throw new SemComandoDesfazerException();
        }
        pilhaRefazer.push(tirarFoto());
        contexto.setBase(restaurar(pilhaDesfazer.pop()));
    }

    public void refazer() throws SemComandoRefazerException {
        if (pilhaRefazer.isEmpty()) {
            throw new SemComandoRefazerException();
        }
        pilhaDesfazer.push(tirarFoto());
        contexto.setBase(restaurar(pilhaRefazer.pop()));
    }

    private byte[] tirarFoto() {
        try {
            return Serializacao.serializar(contexto.getBase());
        } catch (IOException e) {
            throw new IllegalStateException("Falha interna ao copiar o estado do sistema", e);
        }
    }

    private static BaseDados restaurar(byte[] foto) {
        try {
            return Serializacao.desserializar(foto, BaseDados.class);
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Falha interna ao restaurar o estado do sistema", e);
        }
    }
}
