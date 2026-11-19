package simulador.modelo;
import simulador.excecoes.AcaoInvalidaException;

public interface Atacante {
	String atacar(Personagem alvo)throws AcaoInvalidaException;
	String usarHabilidade(Personagem alvo)throws AcaoInvalidaException;
	boolean podeAgir();
}
