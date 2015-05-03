package client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scheduler.TaskScheduler;
import client.thread.impl.GroovyMultithread;

/**
 * Sample client which launches different dummy GroovyTask to the TaskScheduler.
 * This class is multithreaded and uses 3 threads
 *
 */
public class MultithreadClient {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MultithreadClient.class);

    /**
     * Number of task per thread
     */
    private static final int TASK_POOL = 10000;

    /**
     * Result computed by the tasks
     */
    private static int total = 0;

    // Do not instanciate
    private MultithreadClient() {
    }

    /**
     * Add task result to total
     * 
     * @param value
     *            Value to add to the total
     */
    public static synchronized void addTotal(int value) {
        total += value;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        LOGGER.info("MulithreadClient is running...");

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        try {
            executorService.submit(new GroovyMultithread(TASK_POOL, 1));
            executorService.submit(new GroovyMultithread(TASK_POOL, 10));
            executorService.submit(new GroovyMultithread(TASK_POOL, 100));
        } finally {
            executorService.shutdown();
        }
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            LOGGER.error("Executor failed to wait termination", e);
        } finally {
            TaskScheduler.shutdown();
        }

        long end = System.currentTimeMillis();

        LOGGER.info("MulithreadClient is finished.");
        LOGGER.info("Total computed = " + MultithreadClient.total + "; Total expected = 1110000");
        LOGGER.info("Execution time = " + (end - start) + " ms");
    }
}
