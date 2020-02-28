package org.rspeer.script.provider;

import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;

import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public interface ScriptProvider<S> extends Predicate<Class<?>> {

    S[] load();

    void prepare(S source) throws Exception;

    @Override
    default boolean test(Class<?> c) {
        return !Modifier.isAbstract(c.getModifiers())
                && Script.class.isAssignableFrom(c)
                && c.isAnnotationPresent(ScriptMeta.class);
    }
}
