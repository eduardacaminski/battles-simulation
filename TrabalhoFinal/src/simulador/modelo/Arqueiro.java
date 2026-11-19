package simulador.modelo;

import java.util.Random;

public class Arqueiro extends Personagem {

    private static final int VIDA_MAXIMA = 95;
    private static final int ATAQUE = 14;
    private static final int DEFESA = 6;
    private static final int TEMPO_RECARGA = 2;
    private static final double CHANCE_CRITICO = 0.35;
    private static final int MULTIPLICADOR_CRITICO = 2;
    private static final Random SORTEIO = new Random();
    
    public Arqueiro(String nome) {
        this(nome, VIDA_MAXIMA);
    }

    public Arqueiro(String nome, int vidaAtual) {
        super(nome, VIDA_MAXIMA, vidaAtual, ATAQUE, DEFESA);
    }
    @Override
    protected String executarHabilidade(Personagem alvo) {
        int dano = calcularDano(getAtaque(), 0);
        boolean critico = SORTEIO.nextDouble() < CHANCE_CRITICO;
        if (critico) {
            dano = dano * MULTIPLICADOR_CRITICO;
        }
        int danoCausado = alvo.receberDano(dano);
        String textoCritico = critico ? " ACERTO CRÍTICO!" : "";
        return getNome() + " usou Tiro Preciso em " + alvo.getNome() + "." + textoCritico
                + " Causou " + danoCausado + " de dano.";
    }
    @Override
    protected int getTempoRecarga() {
        return TEMPO_RECARGA;
    }

    @Override
    public String getTipo() {
        return "Arqueiro";
    }

    @Override
    public String getNomeHabilidade() {
        return "Tiro Preciso";
    }

    @Override
    public String getDescricaoHabilidade() {
        return "Ignora toda a defesa do alvo e tem 35% de chance de crítico (dano dobrado). Uma vez a cada "
                + TEMPO_RECARGA + " rodadas.";
    }
}

