package simulador.modelo;

import simulador.excecoes.AcaoInvalidaException;

public abstract class Item implements Usavel {

    private final String nome;

    protected Item(String nome) {
        this.nome = nome;
    }

    @Override
    public final String usar(Personagem alvo) throws AcaoInvalidaException {
        if (!podeSerUsadoEm(alvo)) {
            throw new AcaoInvalidaException(nome + " não pode ser usado agora: " + getCondicaoDeUso());
        }
        return aplicarEfeito(alvo);
    }

    protected abstract String aplicarEfeito(Personagem alvo);

    public abstract boolean isUsadoEmAliado();

    public abstract String getCondicaoDeUso();

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
