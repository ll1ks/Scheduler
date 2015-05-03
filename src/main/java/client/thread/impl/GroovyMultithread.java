package client.thread.impl;

import client.MultithreadClient;
import client.thread.GroovyThread;

/**
 * GroovyMultithread class
 *
 * A GroovyMultithread is a GroovyThread which updates a MultithreadClient
 */
public class GroovyMultithread extends GroovyThread {

    /**
     * Constructs a new GroovyMultithread
     * 
     * @param nbTasks
     *            Number of tasks to generate
     * @param value
     *            Value used for generating the dummy GroovyTasks
     */
    public GroovyMultithread(int nbTasks, int value) {
        super(nbTasks, value);
    }

    @Override
    public void updateResults(int value) {
        MultithreadClient.addTotal(value);

    }

}
