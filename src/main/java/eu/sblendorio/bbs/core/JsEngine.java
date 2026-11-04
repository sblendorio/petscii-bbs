package eu.sblendorio.bbs.core;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;

import javax.script.ScriptException;

/**
 * GraalJS engine for one BBS session, to be closed when the session ends.
 * It owns its polyglot Engine: since GraalJS 24 the Engine shared by ScriptEngineManager
 * retains memory of every session, even after the script engine is closed.
 */
public class JsEngine implements AutoCloseable {

    private final Engine polyglotEngine;
    private final GraalJSScriptEngine engine;

    public JsEngine() {
        polyglotEngine = Engine.newBuilder().option("engine.WarnInterpreterOnly", "false").build();
        engine = GraalJSScriptEngine.create(polyglotEngine, Context.newBuilder("js").allowAllAccess(true));
    }

    public void put(String name, Object value) {
        engine.put(name, value);
    }

    public Object eval(String script) throws ScriptException {
        return engine.eval(script);
    }

    @Override
    public void close() {
        try {
            engine.close();
        } finally {
            polyglotEngine.close();
        }
    }
}
