package uk.co.scattercode.drools.util;

import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.FactHandle;

/**
 * An insertion of a fact outside any session, for testing the listeners and
 * the utilities that read insertions. Only the fact itself is set.
 */
record StubObjectInsertedEvent(Object fact) implements ObjectInsertedEvent {

    @Override
    public Object getObject() {
        return fact;
    }

    @Override
    public FactHandle getFactHandle() {
        return null;
    }

    @Override
    public Rule getRule() {
        return null;
    }

    @Override
    public KieRuntime getKieRuntime() {
        return null;
    }
}
