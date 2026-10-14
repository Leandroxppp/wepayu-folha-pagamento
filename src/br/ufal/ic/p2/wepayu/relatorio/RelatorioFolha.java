package br.ufal.ic.p2.wepayu.relatorio;

import br.ufal.ic.p2.wepayu.excecoes.ErroArquivoSaidaException;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.util.Formatos;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Relatório da folha de pagamento de um dia. Recebe os contracheques (um por
 * empregado pago), separa cada um na seção do seu tipo e escreve o relatório.
 */
public class RelatorioFolha implements VisitanteContracheque {

    private final LocalDate data;

    private final SecaoRelatorio<ContrachequeHorista> horistas = new SecaoRelatorio<>(
            "HORISTAS", "TOTAL HORISTAS", 36, Arrays.asList(
            new Coluna<ContrachequeHorista>("Horas", 5, Formato.HORAS, ContrachequeHorista::getHorasNormais),
            new Coluna<ContrachequeHorista>("Extra", 5, Formato.HORAS, ContrachequeHorista::getHorasExtras),
            Coluna.<ContrachequeHorista>bruto(),
            Coluna.<ContrachequeHorista>descontos(),
            Coluna.<ContrachequeHorista>liquido()));

    private final SecaoRelatorio<ContrachequeAssalariado> assalariados = new SecaoRelatorio<>(
            "ASSALARIADOS", "TOTAL ASSALARIADOS", 48, Arrays.asList(
            Coluna.<ContrachequeAssalariado>bruto(),
            Coluna.<ContrachequeAssalariado>descontos(),
            Coluna.<ContrachequeAssalariado>liquido()));

    private final SecaoRelatorio<ContrachequeComissionado> comissionados = new SecaoRelatorio<>(
            "COMISSIONADOS", "TOTAL COMISSIONADOS", 21, Arrays.asList(
            new Coluna<ContrachequeComissionado>("Fixo", 8, Formato.MOEDA, ContrachequeComissionado::getFixo),
            new Coluna<ContrachequeComissionado>("Vendas", 8, Formato.MOEDA, ContrachequeComissionado::getVendas),
            new Coluna<ContrachequeComissionado>("Comissao", 8, Formato.MOEDA, ContrachequeComissionado::getComissao),
            Coluna.<ContrachequeComissionado>bruto(),
            Coluna.<ContrachequeComissionado>descontos(),
            Coluna.<ContrachequeComissionado>liquido()));

    public RelatorioFolha(LocalDate data) {
        this.data = data;
    }

    @Override
    public void visitar(ContrachequeHorista contracheque) {
        horistas.adicionar(contracheque);
    }

    @Override
    public void visitar(ContrachequeAssalariado contracheque) {
        assalariados.adicionar(contracheque);
    }

    @Override
    public void visitar(ContrachequeComissionado contracheque) {
        comissionados.adicionar(contracheque);
    }

    public void gravarEm(String caminho) throws ErroArquivoSaidaException {
        try (FileWriter arquivo = new FileWriter(caminho)) {
            arquivo.write(montarTexto());
        } catch (IOException e) {
            throw new ErroArquivoSaidaException();
        }
    }

    private String montarTexto() {
        TextoRelatorio texto = new TextoRelatorio();
        String titulo = "FOLHA DE PAGAMENTO DO DIA " + data;
        texto.linha(titulo);
        texto.linha(new String(new char[titulo.length()]).replace('\0', '='));
        texto.linha("");
        horistas.escreverEm(texto);
        assalariados.escreverEm(texto);
        comissionados.escreverEm(texto);
        BigDecimal totalGeral = horistas.totalBruto().add(assalariados.totalBruto()).add(comissionados.totalBruto());
        texto.linha("TOTAL FOLHA: " + Formatos.formatarMoeda(totalGeral));
        return texto.toString();
    }
}
