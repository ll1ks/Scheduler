package client.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scheduler.TaskScheduler;
import scheduler.exception.SchedulerException;
import scheduler.task.Task;
import scheduler.task.impl.GroovyTask;

/**
 * GroovyThread abstract class
 * 
 * Defines a thread which launches several dummy GroovyTask through
 * TaskScheduler and gets the results
 *
 */
public abstract class GroovyThread implements Runnable {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyThread.class);

    /**
     * Task list
     */
    private List<Task> groovyTaskList;

    /**
     * Number of tasks
     */
    private int nbTasks;

    /**
     * Constructs a new GroovyThread
     * 
     * @param nbTasks
     *            Number of tasks to generate
     * @param value
     *            Value used for generating the dummy GroovyTasks
     */
    public GroovyThread(int nbTasks, int value) {
        this.groovyTaskList = new ArrayList<Task>();
        this.nbTasks = nbTasks;
        init(value);
    }

    /**
     * Initialize the task list
     * 
     * @param value
     *            Value used for generating the dummy GroovyTasks
     */
    private void init(int value) {
        String script = "[].sum(" + value + ")";
        for (int i = 0; i < nbTasks; i++) {
            groovyTaskList.add(new GroovyTask(script));
        }
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        LOGGER.info(threadName + " >>> started...");

        TaskScheduler taskScheduler = TaskScheduler.getInstance();

        try {
            // Submit tasks
            for (Task task : groovyTaskList) {
                taskScheduler.submitTask(task);
            }

            // Get results
            LOGGER.info(threadName + " >>> handling results");
            int remainingTask = groovyTaskList.size();
            while (remainingTask > 0) {
                for (Task task : groovyTaskList) {
                    if (taskScheduler.isTaskDone(task)) {
                        try {
                            updateResults((Integer) taskScheduler.getTaskResult(task));
                        } catch (ExecutionException e) {
                            LOGGER.error("Task " + task + " failed executing", e);
                        }
                        taskScheduler.removeTask(task);
                        remainingTask--;
                    }
                }
            }
        } catch (SchedulerException se) {
            LOGGER.error("An error occured with the task scheduler", se);
        } finally {
            LOGGER.info(threadName + " >>> finished");
        }
    }

    /**
     * Updates the result of a main thread
     * 
     * @param value
     *            Result computed by a task
     */
    public abstract void updateResults(int value);
}
