package scheduler.exception;

/**
 * 
 * Exception thrown when something happens in the TaskScheduler
 *
 */
public class SchedulerException extends AbstractShedulerException {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -3583788674034421071L;

    /**
     * Constructs a new SchedulerException
     * 
     * @param message
     *            Exception message
     */
    public SchedulerException(String message) {
        super(message);
    }

    /**
     * Constructs a new SchedulerException
     * 
     * @param message
     *            Exception message
     * @param cause
     *            Root exception
     */
    public SchedulerException(String message, Throwable cause) {
        super(message, cause);
    }

}
