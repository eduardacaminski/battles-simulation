package simulador.modelo;

import simulador.excecoes.AcaoInvalidaException;


public final class FabricaPersonagens {

    public static final String[] TIPOS = {"Guerreiro", "Mago", "Arqueiro"};
    private static final int VIDA_CHEIA = Integer.MAX_VALUE;

    private FabricaPersonagens() {
    }

    public static Personagem criar(String tipo, String nome) throws AcaoInvalidaException {
        return criar(tipo, nome, VIDA_CHEIA);
    }

    public static Personagem criar(String tipo, String nome, int vidaAtual) throws AcaoInvalidaException {
        if (tipo == null) {
            throw new AcaoInvalidaException("O tipo do personagem não foi informado.");
        }
        switch (tipo.trim().toLowerCase()) {
            case "guerreiro":
                return new Guerreiro(nome, vidaAtual);
            case "mago":
                return new Mago(nome, vidaAtual);
            case "arqueiro":
                return new Arqueiro(nome, vidaAtual);
            default:
                throw new AcaoInvalidaException("Tipo de personagem desconhecido: " + tipo);
        }
    }
}
