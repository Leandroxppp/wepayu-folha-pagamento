import easyaccept.EasyAccept;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Executa os testes de aceitação (EasyAccept) pela IDE.
 *
 * Sem argumentos: roda TODOS os testes (us1 até us10, incluindo os usN_1 de persistência).
 * Com um argumento (ex.: tests/us7.txt): roda apenas aquele arquivo.
 *
 * Cada usN_1 precisa rodar em outra JVM, logo depois do usN, porque verifica a
 * persistência em wepayu.db. Por isso cada arquivo é executado em um processo separado.
 */
public class Main {

    private static final int TOTAL_US = 10;

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            EasyAccept.main(new String[]{"br.ufal.ic.p2.wepayu.Facade", args[0]});
            return;
        }

        boolean falhou = false;
        for (int n = 1; n <= TOTAL_US; n++) {
            new File("wepayu.db").delete();
            falhou |= !rodar("tests/us" + n + ".txt");
            if (new File("tests/us" + n + "_1.txt").exists()) {
                falhou |= !rodar("tests/us" + n + "_1.txt");
            }
        }
        new File("wepayu.db").delete();
        System.out.println(falhou ? "\n>>> HOUVE FALHAS" : "\n>>> TODOS OS TESTES PASSARAM");
        System.exit(falhou ? 1 : 0);
    }

    /** Roda um arquivo de teste em uma JVM nova e devolve true se passou. */
    private static boolean rodar(String arquivo) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        cmd.add("-cp");
        cmd.add(System.getProperty("java.class.path"));
        cmd.add("Main");
        cmd.add(arquivo);
        Process p = new ProcessBuilder(cmd).inheritIO().start();
        return p.waitFor() == 0;
    }
}
