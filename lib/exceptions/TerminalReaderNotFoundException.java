package exceptions;

/**
 * Exception class used to inform Terminal Reader not found
 * 
 * @author Layon Luciano
 * 
 * Created on 03/10/16.
 */

public class TerminalReaderNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2213613255634083279L;

	public TerminalReaderNotFoundException(String message) {
		super(message);
	}
}
