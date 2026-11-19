package simulador.modelo;

public class PocaoVida extends Item {

    private static final int CURA = 40;

    public PocaoVida() {
        super("Poção de Vida");
    }

    @Override
    public boolean podeSerUsadoEm(Personagem alvo) {
        return alvo != null && alvo.estaVivo() && alvo.getVidaAtual() < alvo.getVidaMaxima();
    }

    @Override
    protected String aplicarEfeito(Personagem alvo) {
        int vidaRecuperada = alvo.curar(CURA);
        return alvo.getNome() + " bebeu uma Poção de Vida e recuperou " + vidaRecuperada + " de vida.";
    }

    @Override
    public boolean isUsadoEmAliado() {
        return true;
    }

    @Override
    public String getCondicaoDeUso() {
        return "o alvo precisa ser um aliado vivo e ferido (cura até " + CURA + " de vida).";
    }
}
