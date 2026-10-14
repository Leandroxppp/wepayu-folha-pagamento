package br.ufal.ic.p2.wepayu.persistencia;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/** Conversão de objetos em bytes (serialização padrão do Java) e de volta. */
public final class Serializacao {

    private Serializacao() {
    }

    public static byte[] serializar(Serializable objeto) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream saida = new ObjectOutputStream(bytes)) {
            saida.writeObject(objeto);
        }
        return bytes.toByteArray();
    }

    public static <T> T desserializar(byte[] bytes, Class<T> tipo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return tipo.cast(entrada.readObject());
        }
    }
}
