package exceptions;

/**
 * Exception class used to inform missing Terminal Reader drivers
 *
 * @author Anderson Rodrigo Davi
 * 
 * Created on 03/10/16. 
 */
public class TerminalCardDriversNotAvailableException extends RuntimeException {

	private static final long serialVersionUID = 1113590278908460082L;

	public TerminalCardDriversNotAvailableException(String message) {
        super(message);
    }
}
