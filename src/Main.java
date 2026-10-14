import easyaccept.EasyAccept;

/** Atalho opcional para executar um script do EasyAccept pela IDE. */
public class Main {
    public static void main(String[] args) {
        String teste = args.length > 0 ? args[0] : "tests/us10.txt";
        EasyAccept.main(new String[]{"br.ufal.ic.p2.wepayu.Facade", teste});
    }
}
