package br.ufal.ic.p2.wepayu.repositorio;

/**
 * Aponta para a base de dados em uso. Os serviços consultam o contexto a cada
 * operação, então o desfazer/refazer pode trocar a base inteira sem que eles
 * percebam.
 */
public class ContextoSistema {

    private BaseDados base;

    public ContextoSistema(BaseDados base) {
        this.base = base;
    }

    public BaseDados getBase() {
        return base;
    }

    public void setBase(BaseDados base) {
        this.base = base;
    }

    public RepositorioEmpregados empregados() {
        return base.getEmpregados();
    }

    public RepositorioAgendas agendas() {
        return base.getAgendas();
    }
}
