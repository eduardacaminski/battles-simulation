package simulador.modelo;

public class Mago extends Personagem{
	 private static final int VIDA_MAXIMA = 85;
	    private static final int ATAQUE = 7;
	    private static final int DEFESA = 4;
	    private static final int TEMPO_RECARGA = 3;
	    private static final int PODER_MAGICO = 32;

	    private final int poderMagico;

	    public Mago(String nome) {
	        this(nome, VIDA_MAXIMA);
	    }

	    public Mago(String nome, int vidaAtual) {
	        super(nome, VIDA_MAXIMA, vidaAtual, ATAQUE, DEFESA);
	        this.poderMagico = PODER_MAGICO;
	    }
	    @Override
	    protected String executarHabilidade(Personagem alvo) {
	        int defesaReduzida = alvo.getDefesa() / 2;
	        int dano = calcularDano(poderMagico, defesaReduzida);
	        int danoCausado = alvo.receberDano(dano);
	        return getNome() + " lançou Bola de Fogo em " + alvo.getNome()
	                + " e causou " + danoCausado + " de dano mágico.";
	    }
	    @Override
	    protected int getTempoRecarga() {
	        return TEMPO_RECARGA;
	    }

	    public int getPoderMagico() {
	        return poderMagico;
	    }

	    @Override
	    public String getTipo() {
	        return "Mago";
	    }

	    @Override
	    public String getNomeHabilidade() {
	        return "Bola de Fogo";
	    }

	    @Override
	    public String getDescricaoHabilidade() {
	        return "Dano mágico de " + poderMagico + " que ignora metade da defesa do alvo. Uma vez a cada "
	                + TEMPO_RECARGA + " rodadas.";
	    }

	    @Override
	    public String toString() {
	        return super.toString() + " | Poder mágico: " + poderMagico;
	    }
	}

