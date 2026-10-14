import easyaccept.EasyAccept;

// Roda um teste de aceitacao via EasyAccept. Troque o arquivo para rodar outro.
// Para testar persistencia (usN_1), rode em uma execucao separada da JVM logo
// depois do usN correspondente. Para rodar tudo: ./rodar-testes.sh
public class Main {
    public static void main(String[] args) {
        String facade = "br.ufal.ic.p2.wepayu.Facade";
        EasyAccept.main(new String[]{facade, "tests/us10.txt"});
    }
}
