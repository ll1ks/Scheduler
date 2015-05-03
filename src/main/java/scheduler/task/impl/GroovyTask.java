package scheduler.task.impl;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import scheduler.task.AbstractScriptTask;

/**
 * Defines a task for computing Groovy script through ScriptEngine
 *
 */
public class GroovyTask extends AbstractScriptTask {

    /**
     * Script engine
     */
    private static ScriptEngine engine;

    // Set the groovy engine
    static {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        engine = scriptEngineManager.getEngineByName("groovy");
    }

    /**
     * Constructs a Groovy task
     * 
     * @param groovyScript
     *            Groovy script to be executed
     */
    public GroovyTask(String groovyScript) {
        super(groovyScript);
    }

    @Override
    public Object call() throws Exception {
        return engine.eval(getScript());
    }

    @Override
    public String toString() {
        return "[GroovyTask=" + getId() + "]";
    }

}
