package scheduler.task;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Defines an abstract class for running Script Task
 *
 */
public abstract class AbstractScriptTask implements Task {

    /**
     * Unique id generator
     */
    private static final AtomicInteger UNIQUEID = new AtomicInteger();

    /**
     * Task unique id
     */
    private int taskId;

    /**
     * Task script
     */
    private String script;

    /**
     * Default constructor
     */
    public AbstractScriptTask(String script) {
        this.taskId = UNIQUEID.getAndIncrement();
        this.script = script;
    }

    @Override
    public int getId() {
        return taskId;
    }

    /**
     * Gets the task script
     * 
     * @return Task script
     */
    public String getScript() {
        return script;
    }

}
