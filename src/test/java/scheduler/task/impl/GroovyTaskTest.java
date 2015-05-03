package scheduler.task.impl;

import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GroovyTaskTest {

    private String groovyScript = "(1..10).sum()";
    private String anotherGroovyScript = "Another groovy script";
    private GroovyTask groovyTask;
    private GroovyTask groovyTaskNull;
    private GroovyTask anotherGroovyTask;

    @Before
    public void setUp() throws Exception {
        groovyTask = new GroovyTask(groovyScript);
        groovyTaskNull = new GroovyTask(null);
        anotherGroovyTask = new GroovyTask(anotherGroovyScript);
    }

    @Test
    public void testGroovyTask() {
        Assert.assertNotNull(groovyTask);
        Assert.assertNotNull(groovyTaskNull);
        Assert.assertNotNull(anotherGroovyTask);
    }

    @Test
    public void testCall() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Object result = null;
        try {
            result = executorService.submit(groovyTask).get();
        } catch (InterruptedException | ExecutionException e) {
            fail(e.getMessage());
        } finally {
            executorService.shutdown();
        }
        Assert.assertEquals(55, result);

    }

    @Test(expected = ExecutionException.class)
    public void testCallTaskError() throws ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Object result = null;
        try {
            result = executorService.submit(groovyTaskNull).get();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } finally {
            executorService.shutdown();
        }
        Assert.assertEquals(55, result);

    }

    @Test
    public void testToString() {
        String groovyTaskExpected = "[GroovyTask=" + groovyTask.getId() + "]";
        String anotherGroovyTaskExpected = "[GroovyTask=" + anotherGroovyTask.getId() + "]";
        Assert.assertEquals(groovyTaskExpected, groovyTaskExpected.toString());
        Assert.assertEquals(anotherGroovyTaskExpected, anotherGroovyTaskExpected.toString());
    }

    @Test
    public void testGetId() {
        Assert.assertTrue(groovyTask.getId() >= 0);
        Assert.assertTrue(anotherGroovyTask.getId() > 0);
        Assert.assertNotEquals(groovyTask.getId(), anotherGroovyTask.getId());
    }

    @Test
    public void testGetScript() {
        Assert.assertEquals(groovyScript, groovyTask.getScript());
        Assert.assertEquals(anotherGroovyScript, anotherGroovyTask.getScript());
    }

}
