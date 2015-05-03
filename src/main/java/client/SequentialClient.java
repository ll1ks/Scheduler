package client;

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
 * Sample client which launches different dummy GroovyTask to the TaskScheduler.
 * This class is monothread
 *
 */
public class SequentialClient {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SequentialClient.class);

    /**
     * Number of task per type (listSum1, listSum10, listSum100)
     */
    public static final int TASK_POOL = 10000;

    /**
     * Result computed by the tasks
     */
    private static int total = 0;

    /**
     * First task list. All tasks in the list are the same
     */
    private List<Task> listSum1;

    /**
     * Second task list. All tasks in the list are the same
     */
    private List<Task> listSum10;

    /**
     * Third task list. All tasks in the list are the same
     */
    private List<Task> listSum100;

    /**
     * Default constructor
     */
    public SequentialClient() {
        this.listSum1 = new ArrayList<Task>();
        this.listSum10 = new ArrayList<Task>();
        this.listSum100 = new ArrayList<Task>();

        init();
    }

    /**
     * Initializes the task lists
     */
    private void init() {
        for (int i = 0; i < 10000; i++) {
            listSum1.add(new GroovyTask("[].sum(1)"));
            listSum10.add(new GroovyTask("[].sum(10)"));
            listSum100.add(new GroovyTask("[].sum(100)"));

        }
    }

    /**
     * Submits the tasks to the task scheduler
     * 
     * @throws SchedulerException
     *             If something wrong happend in the scheduler
     */
    public void submitTasks() throws SchedulerException {
        TaskScheduler taskScheduler = TaskScheduler.getInstance();

        for (int i = 0; i < TASK_POOL; i++) {
            taskScheduler.submitTask(listSum1.get(i));
            taskScheduler.submitTask(listSum10.get(i));
            taskScheduler.submitTask(listSum100.get(i));
        }
    }

    /**
     * Retrieves all the tasks results
     * 
     * @throws SchedulerException
     *             If something wrong happened in the scheduler
     */
    public void retrieveAllResults() throws SchedulerException {
        int remainingTasks = TASK_POOL * 3;
        while (remainingTasks > 0) {
            remainingTasks = remainingTasks - retrieveResults(listSum1);
            remainingTasks = remainingTasks - retrieveResults(listSum10);
            remainingTasks = remainingTasks - retrieveResults(listSum100);
        }

    }

    /**
     * Tries to retrieves the results of a task list
     * 
     * @param taskList
     *            Task list to check
     * @return The number of results retrieved
     * @throws SchedulerException
     *             If something wrong happened in the scheduler
     */
    private int retrieveResults(List<Task> taskList) throws SchedulerException {
        TaskScheduler taskScheduler = TaskScheduler.getInstance();

        int taskFinished = 0;
        for (Task task : taskList) {

            if (taskScheduler.isTaskDone(task)) {
                try {
                    SequentialClient.total += (Integer) taskScheduler.getTaskResult(task);
                } catch (ExecutionException e) {
                    LOGGER.error("Task " + task + " failed executing", e);
                }
                taskScheduler.removeTask(task);
                taskFinished++;
            }
        }
        return taskFinished;

    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        LOGGER.info("SequentialClient is running...");

        SequentialClient linearClient = new SequentialClient();

        try {
            linearClient.submitTasks();
            linearClient.retrieveAllResults();
        } catch (SchedulerException se) {
            LOGGER.error("An error occured with the task scheduler", se);
        } finally {
            TaskScheduler.shutdown();
        }
        long end = System.currentTimeMillis();

        LOGGER.info("SequentialClient is finished.");
        LOGGER.info("Total computed = " + SequentialClient.total + "; Total expected = 1110000");
        LOGGER.info("Execution time = " + (end - start) + " ms");
    }
}
