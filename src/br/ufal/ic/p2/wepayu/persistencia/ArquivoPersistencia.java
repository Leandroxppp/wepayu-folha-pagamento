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
     * Lê a base do arquivo. Arquivo inexistente significa primeira execução; arquivo existente
     * mas inválido é uma falha real e não pode ser silenciosamente substituído por uma base vazia.
     */
    public BaseDados carregarOuCriar() {
        if (!Files.exists(arquivo)) {
            return new BaseDados();
        }
        try {
            return Serializacao.desserializar(Files.readAllBytes(arquivo), BaseDados.class);
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new IllegalStateException("Nao foi possivel carregar os dados persistidos de " + arquivo, e);
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
