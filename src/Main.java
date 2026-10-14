import easyaccept.EasyAccept;

// Roda os testes de aceitacao via EasyAccept. So descomentar a linha do
// teste que quiser rodar. Pra testar persistencia de verdade (usN_1),
// tem que rodar em execucoes separadas da JVM, senao nao vale o teste.
public class Main {
    public static void main(String[] args) {
        String facade = "br.ufal.ic.p2.wepayu.Facade";

        EasyAccept.main(new String[]{facade, "tests/us1.txt"});
        // EasyAccept.main(new String[]{facade, "tests/us1_1.txt"});

        // EasyAccept.main(new String[]{facade, "tests/us2.txt"});
        // EasyAccept.main(new String[]{facade, "tests/us2_1.txt"});

        // EasyAccept.main(new String[]{facade, "tests/us3.txt"});
        // EasyAccept.main(new String[]{facade, "tests/us3_1.txt"});

        // EasyAccept.main(new String[]{facade, "tests/us4.txt"});
        // EasyAccept.main(new String[]{facade, "tests/us4_1.txt"});

        // EasyAccept.main(new String[]{facade, "tests/us5.txt"});
        // EasyAccept.main(new String[]{facade, "tests/us5_1.txt"});

        // EasyAccept.main(new String[]{facade, "tests/us6.txt"});
        // EasyAccept.main(new String[]{facade, "tests/us6_1.txt"});

        // EasyAccept.main(new String[]{facade, "tests/us7.txt"});

        // EasyAccept.main(new String[]{facade, "tests/us8.txt"});
    }
}
