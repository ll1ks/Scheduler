package client.thread.impl;

import client.VeryMultithreadClient;
import client.thread.GroovyThread;

/**
 * A GroovyVeryMultithread is a GroovyThread which updates a
 * VeryMultithreadClient
 */
public class GroovyVeryMultithread extends GroovyThread {

    /**
     * Constructs a new GroovyVeryMultithread
     * 
     * @param nbTasks
     *            Number of tasks to generate
     * @param value
     *            Value used for generating the dummy GroovyTasks
     */
    public GroovyVeryMultithread(int nbTasks, int value) {
        super(nbTasks, value);
    }

    @Override
    public void updateResults(int value) {
        VeryMultithreadClient.addTotal(value);

    }

}
