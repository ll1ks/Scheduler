package scheduler.exception;

/**
 * AbstractSchdulerException class
 * 
 * Parent exception for TaskScheduler project
 *
 */
public class AbstractShedulerException extends Exception {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -845573142735444545L;

    /**
     * Constructor
     * 
     * @param message
     *            Exception message
     */
    public AbstractShedulerException(String message) {
        super(message);
    }

    /**
     * Constructor
     * 
     * @param message
     *            Exception message
     * @param cause
     *            Root exception
     */
    public AbstractShedulerException(String message, Throwable cause) {
        super(message, cause);
    }

}
