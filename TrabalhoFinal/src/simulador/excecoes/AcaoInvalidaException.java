package simulador.excecoes;

public class AcaoInvalidaException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public AcaoInvalidaException(String mensagem) {
		super(mensagem);
	}
}
