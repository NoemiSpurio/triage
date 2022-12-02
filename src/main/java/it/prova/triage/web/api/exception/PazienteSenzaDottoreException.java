package it.prova.triage.web.api.exception;

public class PazienteSenzaDottoreException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PazienteSenzaDottoreException(String message) {
		super(message);
	}
}
