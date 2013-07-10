package exception;

/**
 * Thrown when the connection to the external calendar cannot be established.
 * 
 */
public class ExternalSynchronizationFailedException extends RuntimeException {

	public ExternalSynchronizationFailedException(Throwable cause, String message) {
		super(message, cause);
	}
	
}
