package scheduler.task;

import java.util.concurrent.Callable;

/**
 * A Task is a callable having an id
 *
 */
public interface Task extends Callable<Object> {

    /**
     * Gets the task id
     * 
     * @return Task id
     */
    public int getId();

}
