package scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scheduler.exception.SchedulerException;
import scheduler.task.Task;

/**
 * Task scheduler which allows to submit task and retrieve results
 *
 */
public class TaskScheduler {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskScheduler.class);

    /**
     * Task scheduler instance
     */
    private static final TaskScheduler INSTANCE = new TaskScheduler();

    /**
     * Tasks submitted to the TaskScheduler
     */
    private ConcurrentMap<Task, Future<Object>> tasksRunning;

    /**
     * Tasks terminated
     */
    private ConcurrentMap<Task, Object> tasksDone;

    /**
     * Executor for handling parallel task execution
     */
    private static final ExecutorService EXECUTORSERVICE;
    static {
        EXECUTORSERVICE = Executors.newFixedThreadPool(4);
    }

    /**
     * Lock used when updating the tasks status
     */
    private Lock updateLock;

    /**
     * Private constructor
     */
    private TaskScheduler() {
        this.tasksRunning = new ConcurrentHashMap<Task, Future<Object>>();
        this.tasksDone = new ConcurrentHashMap<Task, Object>();
        this.updateLock = new ReentrantLock();
    }

    public static TaskScheduler getInstance() {
        return INSTANCE;
    }

    /**
     * Shutdowns the scheduler
     */
    public static synchronized void shutdown() {
        EXECUTORSERVICE.shutdown();
    }

    /**
     * Submits a task to the executor
     * 
     * @param task
     *            Task to be executed
     * @throws SchedulerException
     *             If the submitted task is <code>null</code>
     */
    public void submitTask(Task task) throws SchedulerException {
        if (null == task) {
            throw new SchedulerException("Submitted task is null");
        }
        Future<Object> future = EXECUTORSERVICE.submit(task);
        tasksRunning.put(task, future);
    }

    /**
     * Updates the tasks status and then removes the task if finished
     * 
     * @param task
     *            Task to be removed
     * @return <code>true</code> if the task has been removed,
     *         <code>false</code> otherwise (task is not finished or task does
     *         not exists)
     * @throws SchedulerException
     *             If the provided task is <code>null</code> or something bad
     *             happened when updating the tasks status
     */
    public boolean removeTask(Task task) throws SchedulerException {
        if (null == task) {
            throw new SchedulerException("Provided task is null");
        }
        updateTasksStatus();
        return null != tasksDone.remove(task);
    }

    /**
     * Updates the tasks status and then retrieves the running tasks
     * 
     * @return <code>List</code> object containing the running tasks
     * @throws SchedulerException
     *             If something bad happened when updating the tasks status
     */
    public List<Task> getTasksRunning() throws SchedulerException {
        updateTasksStatus();
        return new ArrayList<Task>(tasksRunning.keySet());
    }

    /**
     * Updates the tasks status and then retrieves the finished tasks
     * 
     * @return <code>List</code> object containing the finished tasks
     * @throws SchedulerException
     *             If something bad happened when updating the tasks status
     */
    public List<Task> getTasksDone() throws SchedulerException {
        updateTasksStatus();
        return new ArrayList<Task>(tasksDone.keySet());
    }

    /**
     * Checks if a task is finished
     * 
     * @param task
     *            Task to check
     * @return <code>true</code> if the task is finished, <code>false</code>
     *         otherwise
     * @throws SchedulerException
     *             If the provided task is <code>null</code> or something bad
     *             happened when updating the tasks status
     */
    public boolean isTaskDone(Task task) throws SchedulerException {
        if (null == task) {
            throw new SchedulerException("Provided task is null");
        }
        updateTasksStatus();
        return tasksDone.containsKey(task);
    }

    /**
     * Retrieves the task execution result
     * 
     * @param task
     *            Task
     * @return <code>Object</code> containing the result, <code>null</code>
     *         otherwise (task is not finished or does not exists)
     * @throws SchedulerException
     *             If the provided task is <code>null</code> or something bad
     *             happened when updating the tasks status
     * @throws ExecutionException
     *             If the task failed to execute properly
     */
    public Object getTaskResult(Task task) throws SchedulerException, ExecutionException {
        if (null == task) {
            throw new SchedulerException("Provided task is null");
        }
        updateTasksStatus();
        Object result = tasksDone.get(task);
        if (null != result && result instanceof ExecutionException) {
            throw (ExecutionException) result;
        }
        return result;
    }

    /**
     * Checks if new results are ready and updates the status accordingly
     * 
     * @throws SchedulerException
     *             If the thread pool has been interrupted
     */
    private void updateTasksStatus() throws SchedulerException {
        if (updateLock.tryLock()) {
            try {
                for (Map.Entry<Task, Future<Object>> taskRunning : tasksRunning.entrySet()) {
                    Future<Object> taskFuture = taskRunning.getValue();
                    if (taskFuture.isDone()) {
                        tasksRunning.remove(taskRunning.getKey());
                        try {
                            tasksDone.put(taskRunning.getKey(), taskFuture.get());
                        } catch (ExecutionException e) {
                            tasksDone.put(taskRunning.getKey(), e);
                            LOGGER.error("Failed to execute task " + taskRunning.getKey(), e);
                        } catch (InterruptedException ie) {
                            LOGGER.error("Scheduler has been interrupted", ie);
                            throw new SchedulerException("Scheduler service has been interrupted", ie);
                        }
                    }
                }
            } finally {
                updateLock.unlock();
            }
        }
    }
}
