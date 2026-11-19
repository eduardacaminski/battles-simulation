package simulador.modelo;

public class Guerreiro extends Personagem{
	 private static final int VIDA_MAXIMA = 130;
	    private static final int ATAQUE = 16;
	    private static final int DEFESA = 10;
	    private static final int TEMPO_RECARGA = 2;
	    private static final double MULTIPLICADOR_GOLPE_PESADO = 1.5;

	    public Guerreiro(String nome) {
	        this(nome, VIDA_MAXIMA);
	    }

	    public Guerreiro(String nome, int vidaAtual) {
	        super(nome, VIDA_MAXIMA, vidaAtual, ATAQUE, DEFESA);}
	    @Override
	    protected String executarHabilidade(Personagem alvo) {
	        int poder = (int) Math.round(getAtaque() * MULTIPLICADOR_GOLPE_PESADO);
	        int dano = calcularDano(poder, alvo.getDefesa());
	        int danoCausado = alvo.receberDano(dano);
	        return getNome() + " usou Golpe Pesado em " + alvo.getNome()
	                + " e causou " + danoCausado + " de dano.";
	    }

	    @Override
	    protected int getTempoRecarga() {
	        return TEMPO_RECARGA;
	    }

	    @Override
	    public String getTipo() {
	        return "Guerreiro";
	    }

	    @Override
	    public String getNomeHabilidade() {
	        return "Golpe Pesado";
	    }

	    @Override
	    public String getDescricaoHabilidade() {
	        return "Golpe com 150% do ataque (24 de poder) contra a defesa do alvo. Uma vez a cada "
	                + TEMPO_RECARGA + " rodadas.";
	    }
	}

