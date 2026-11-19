package simulador.modelo;

import simulador.excecoes.AcaoInvalidaException;

public abstract class Personagem implements Atacante {

    public static final int DANO_MINIMO = 1;

    private final String nome;
    private final int vidaMaxima;
    private final int ataque;
    private final int defesa;
    private int vidaAtual;
    private int recargaRestante;
    
    protected Personagem(String nome, int vidaMaxima, int vidaAtual, int ataque, int defesa) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do personagem não pode ser vazio.");
        }
        if (nome.contains(";")) {
            throw new IllegalArgumentException("O nome do personagem não pode conter ';'.");
        }
        if (vidaMaxima <= 0 || ataque < 0 || defesa < 0) {
            throw new IllegalArgumentException("Atributos inválidos para o personagem " + nome + ".");
        }
        this.nome = nome.trim();
        this.vidaMaxima = vidaMaxima;
        this.ataque = ataque;
        this.defesa = defesa;
        setVidaAtual(vidaAtual);
        this.recargaRestante = 0;
    }
    private void setVidaAtual(int novaVida) {
        this.vidaAtual = Math.max(0, Math.min(novaVida, vidaMaxima));
    }

    public int receberDano(int dano) {
        if (dano <= 0 || !estaVivo()) {
            return 0;
        }
        int vidaAntes = vidaAtual;
        setVidaAtual(vidaAtual - dano);
        return vidaAntes - vidaAtual;
    }

    public int curar(int quantidade) {
        if (quantidade <= 0 || !estaVivo()) {
            return 0;
        }
        int vidaAntes = vidaAtual;
        setVidaAtual(vidaAtual + quantidade);
        return vidaAtual - vidaAntes;
    }

    public void restaurarVidaTotal() {
        setVidaAtual(vidaMaxima);
        recargaRestante = 0;
    }

    public boolean estaVivo() {
        return vidaAtual > 0;
    }

    @Override
    public boolean podeAgir() {
        return estaVivo();
    }
    public static int calcularDano(int poderDeAtaque, int defesaDoAlvo) {
        return Math.max(DANO_MINIMO, poderDeAtaque - defesaDoAlvo);
    }

    @Override
    public final String atacar(Personagem alvo) throws AcaoInvalidaException {
        validarAcao(alvo);
        int dano = calcularDano(ataque, alvo.getDefesa());
        int danoCausado = alvo.receberDano(dano);
        return nome + " atacou " + alvo.getNome() + " e causou " + danoCausado + " de dano."
                + descreverSituacao(alvo);
    }

    @Override
    public final String usarHabilidade(Personagem alvo) throws AcaoInvalidaException {
        validarAcao(alvo);
        if (!isHabilidadeDisponivel()) {
            throw new AcaoInvalidaException(getNomeHabilidade() + " de " + nome
                    + " está em recarga por mais " + recargaRestante + " rodada(s).");
        }
        String resultado = executarHabilidade(alvo);
        recargaRestante = getTempoRecarga();
        return resultado + descreverSituacao(alvo);
    }
    protected void validarAcao(Personagem alvo) throws AcaoInvalidaException {
        if (!podeAgir()) {
            throw new AcaoInvalidaException(nome + " está derrotado(a) e não pode agir.");
        }
        if (alvo == null) {
            throw new AcaoInvalidaException("Nenhum alvo foi escolhido.");
        }
        if (alvo == this) {
            throw new AcaoInvalidaException(nome + " não pode atacar a si mesmo(a).");
        }
        if (!alvo.estaVivo()) {
            throw new AcaoInvalidaException(alvo.getNome() + " já foi derrotado(a).");
        }
    }

    private String descreverSituacao(Personagem alvo) {
        if (!alvo.estaVivo()) {
            return " " + alvo.getNome() + " foi derrotado(a)!";
        }
        return " Vida de " + alvo.getNome() + ": " + alvo.getVidaAtual() + "/" + alvo.getVidaMaxima() + ".";
    }
    public boolean isHabilidadeDisponivel() {
        return recargaRestante == 0;
    }

    public int getRecargaRestante() {
        return recargaRestante;
    }

    public void passarRodada() {
        if (recargaRestante > 0) {
            recargaRestante--;
        }
    }

    public void prepararParaBatalha() {
        recargaRestante = 0;
    }
    protected abstract String executarHabilidade(Personagem alvo);

    protected abstract int getTempoRecarga();

    public abstract String getTipo();

    public abstract String getNomeHabilidade();

    public abstract String getDescricaoHabilidade();
    public String getNome() {
        return nome;
    }
    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getVidaAtual() {
        return vidaAtual;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefesa() {
        return defesa;
    }

    public String getSituacao() {
        return estaVivo() ? "Ativo" : "Derrotado";
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Vida: %d/%d | Ataque: %d | Defesa: %d | %s",
                getTipo(), nome, vidaAtual, vidaMaxima, ataque, defesa, getSituacao());
    }
}
