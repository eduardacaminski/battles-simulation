package simulador.modelo;
import simulador.excecoes.AcaoInvalidaException;

public interface Usavel {
	 boolean podeSerUsadoEm(Personagem alvo);

	 String usar(Personagem alvo) throws AcaoInvalidaException;
}
