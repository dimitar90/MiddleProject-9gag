package exceptions;

public class PostException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8047089780385272532L;

	public PostException() {
		super();
	}

	public PostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PostException(String message, Throwable cause) {
		super(message, cause);
	}

	public PostException(String message) {
		super(message);
	}

	public PostException(Throwable cause) {
		super(cause);
	}

	
}
