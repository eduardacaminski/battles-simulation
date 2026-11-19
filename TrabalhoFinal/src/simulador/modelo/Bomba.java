package simulador.modelo;

public class Bomba extends Item {

    private static final int DANO = 25;

    public Bomba() {
        super("Bomba");
    }

    @Override
    public boolean podeSerUsadoEm(Personagem alvo) {
        return alvo != null && alvo.estaVivo();
    }

    @Override
    protected String aplicarEfeito(Personagem alvo) {
        int danoCausado = alvo.receberDano(DANO);
        String texto = "A Bomba explodiu em " + alvo.getNome() + " e causou " + danoCausado + " de dano.";
        if (!alvo.estaVivo()) {
            texto += " " + alvo.getNome() + " foi derrotado(a)!";
        }
        return texto;
    }

    @Override
    public boolean isUsadoEmAliado() {
        return false;
    }

    @Override
    public String getCondicaoDeUso() {
        return "o alvo precisa ser um inimigo vivo (causa " + DANO + " de dano, ignorando a defesa).";
    }
}