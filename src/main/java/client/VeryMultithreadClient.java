package client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scheduler.TaskScheduler;
import client.thread.impl.GroovyVeryMultithread;

/**
 * Sample client which launches different dummy GroovyTask to the TaskScheduler.
 * This class is multithreaded and uses a lot of threads
 *
 */
public class VeryMultithreadClient {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(VeryMultithreadClient.class);

    /**
     * Number of task per thread
     */
    private static final int TASK_POOL = 10;

    /**
     * Result computed by the tasks
     */
    private static volatile int total = 0;

    // Do not instanciate
    private VeryMultithreadClient() {
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
        LOGGER.info("VeryMultithreadClient is running...");
        long start = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        try {
            for (int i = 0; i < 1000; i++) {
                executorService.submit(new GroovyVeryMultithread(TASK_POOL, 1));
                executorService.submit(new GroovyVeryMultithread(TASK_POOL, 10));
                executorService.submit(new GroovyVeryMultithread(TASK_POOL, 100));
            }
        } finally {
            executorService.shutdown();
        }
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ie) {
            LOGGER.error("Executor failed to wait termination", ie);
        } finally {
            TaskScheduler.shutdown();
        }

        long end = System.currentTimeMillis();

        LOGGER.info("VeryMultithreadClient is finished.");
        LOGGER.info("Total computed = " + VeryMultithreadClient.total + "; Total expected = 1110000");
        LOGGER.info("Execution time = " + (end - start) + " ms");
    }
}
