package scheduler;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import scheduler.exception.SchedulerException;
import scheduler.task.impl.GroovyTask;

public class TaskSchedulerTest {
    @Rule
    public Timeout globalTimeout = new Timeout(10000, TimeUnit.MILLISECONDS);

    private static TaskScheduler taskScheduler;
    private List<GroovyTask> groovyTaskList;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        taskScheduler = TaskScheduler.getInstance();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        TaskScheduler.shutdown();
    }

    @Before
    public void setUp() throws Exception {
        groovyTaskList = new ArrayList<GroovyTask>();
        for (int i = 0; i < 50; i++) {
            groovyTaskList.add(new GroovyTask("(1..10).sum()"));
        }
    }

    @After
    public void tearDown() throws Exception {
        groovyTaskList = null;
    }

    //
    //
    // TaskScheduler.getInstance()
    //
    //

    @Test
    public void testTaskScheduler() {
        Assert.assertNotNull(taskScheduler);
    }

    //
    //
    // TaskScheduler.submitTask()
    //
    //

    @Test
    public void testSubmitTask() {
        try {
            for (GroovyTask groovyTask : groovyTaskList) {
                taskScheduler.submitTask(groovyTask);
            }
            Assert.assertTrue(taskScheduler.getTasksRunning().size() > 0);
        } catch (SchedulerException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = SchedulerException.class)
    public void testSubmitTaskNull() throws SchedulerException {
        taskScheduler.submitTask(null);
        fail("Should throw a SchedulerException");
    }

    //
    //
    // TaskScheduler.removeTask()
    //
    //

    @Test
    public void testRemoveTask() {
        GroovyTask groovyTask = groovyTaskList.get(0);
        try {
            taskScheduler.submitTask(groovyTask);
            while (!taskScheduler.isTaskDone(groovyTask)) {
            }
            Assert.assertTrue(taskScheduler.removeTask(groovyTask));
            Assert.assertFalse(taskScheduler.removeTask(groovyTask));
        } catch (SchedulerException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = SchedulerException.class)
    public void testRemoveTaskNull() throws SchedulerException {
        taskScheduler.removeTask(null);
        fail("Should throw a SchedulerException");
    }

    //
    //
    // TaskScheduler.getTasksRunning()
    //
    //

    @Test
    public void testGetTasksRunning() {
        try {
            taskScheduler.submitTask(new GroovyTask("sleep(100)"));
            Assert.assertTrue(taskScheduler.getTasksRunning().size() > 0);
        } catch (SchedulerException e) {
            fail(e.getMessage());
        }
    }

    //
    //
    // TaskScheduler.isTaskDone()
    //
    //

    @Test
    public void testIsTaskDone() {
        GroovyTask groovyTask = new GroovyTask("(1..10).sum()");
        try {
            Assert.assertFalse(taskScheduler.isTaskDone(groovyTask));
            taskScheduler.submitTask(groovyTask);
            while (!taskScheduler.isTaskDone(groovyTask)) {
            }
            Assert.assertTrue(taskScheduler.getTasksDone().size() > 0);
        } catch (SchedulerException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = SchedulerException.class)
    public void testIsTaskDoneNull() throws SchedulerException {
        taskScheduler.isTaskDone(null);
        fail("Should throw a SchedulerException");
    }

    //
    //
    // TaskScheduler.getTasksDone()
    //
    //

    @Test
    public void testGetTasksDone() {
        GroovyTask groovyTask = new GroovyTask("(1..10).sum()");
        try {
            taskScheduler.submitTask(groovyTask);
            while (!taskScheduler.isTaskDone(groovyTask)) {
            }
            Assert.assertTrue(taskScheduler.getTasksDone().size() > 0);
        } catch (SchedulerException e) {
            fail(e.getMessage());
        }
    }

    //
    //
    // TaskScheduler.getTaskResult()
    //
    //

    @Test
    public void testGetTaskResult() {
        GroovyTask groovyTask = groovyTaskList.get(0);
        try {
            taskScheduler.submitTask(groovyTask);
            while (!taskScheduler.isTaskDone(groovyTask)) {
            }
            Assert.assertEquals(55, taskScheduler.getTaskResult(groovyTask));
        } catch (SchedulerException | ExecutionException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = SchedulerException.class)
    public void testGetTaskResultNull() throws SchedulerException {
        try {
            taskScheduler.getTaskResult(null);
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }
        fail("Should throw a SchedulerException");
    }

    @Test(expected = ExecutionException.class)
    public void testGetTaskResultTaskError() throws ExecutionException {
        GroovyTask groovyTask = new GroovyTask("This is going to be an error");
        try {
            taskScheduler.submitTask(groovyTask);
            while (!taskScheduler.isTaskDone(groovyTask)) {
            }
            taskScheduler.getTaskResult(groovyTask);
        } catch (SchedulerException ie) {
            fail("Should throw an ExecutionException");
        }
        fail("Should throw an ExecutionException");
    }

    @Test
    public void testGetTaskResultTaskNotFinished() throws ExecutionException {
        GroovyTask groovyTask = new GroovyTask("sleep(100)");
        try {
            taskScheduler.submitTask(groovyTask);
            Assert.assertNull(taskScheduler.getTaskResult(groovyTask));
        } catch (SchedulerException ie) {
            fail(ie.getMessage());
        }
    }
}
