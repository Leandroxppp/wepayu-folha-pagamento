package br.ufal.ic.p2.wepayu.persistencia;

import br.ufal.ic.p2.wepayu.excecoes.FalhaPersistenciaException;
import br.ufal.ic.p2.wepayu.repositorio.BaseDados;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Grava e lê a base de dados num arquivo, para os dados sobreviverem entre uma execução e outra. */
public class ArquivoPersistencia {

    private final Path arquivo;

    public ArquivoPersistencia(String caminho) {
        this.arquivo = Paths.get(caminho);
    }

    /**
     * Lê a base do arquivo. Se ele não existe, começa uma base vazia; se existe mas não pode ser
     * lido (corrompido ou de outra versão do programa), avisa no erro padrão e começa vazia.
     */
    public BaseDados carregarOuCriar() {
        if (!Files.exists(arquivo)) {
            return new BaseDados();
        }
        try {
            return Serializacao.desserializar(Files.readAllBytes(arquivo), BaseDados.class);
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("Aviso: nao foi possivel ler " + arquivo + " (" + e + "); iniciando sem dados.");
            return new BaseDados();
        }
    }

    public void salvar(BaseDados base) throws FalhaPersistenciaException {
        try {
            Files.write(arquivo, Serializacao.serializar(base));
        } catch (IOException e) {
            throw new FalhaPersistenciaException();
        }
    }
}
