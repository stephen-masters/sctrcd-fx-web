package uk.co.scattercode.drools.util;

import java.util.ArrayList;
import java.util.Collection;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;
import uk.co.scattercode.beans.BeanMatcher;
import uk.co.scattercode.beans.BeanPropertyFilter;

public class FactFinder {

    BeanMatcher beanMatcher = new BeanMatcher();

    /**
     * Finds the facts of the expected class with specified properties in
     * working memory.
     *
     * @param kenv
     *            A {@link KnowledgeEnvironment} containing a
     *            {@link KieSession} in which we are looking for the fact.
     * @param factClass
     *            The simple name of the class of the fact we're looking for.
     * @param expectedProperties
     *            A sequence of expected property name/value pairs.
     */
    public Collection<Object> findFacts(
            final KnowledgeEnvironment kenv, final String factClass, final BeanPropertyFilter... expectedProperties) {
        return findFacts(kenv.getKnowledgeSession(), factClass, expectedProperties);
    }

    /**
     * Finds the facts of the expected class with specified properties in
     * working memory.
     *
     * @param session
     *            A {@link KieSession} in which we are looking for the
     *            fact.
     * @param factClass
     *            The simple name of the class of the fact we're looking for.
     * @param expectedProperties
     *            A sequence of expected property name/value pairs.
     */
    public Collection<Object> findFacts(
            final KieSession session, final String factClass, final BeanPropertyFilter... expectedProperties) {

        ObjectFilter filter = object -> object.getClass().getSimpleName().equals(factClass);

        Collection<FactHandle> factHandles = session.getFactHandles(filter);
        Collection<Object> facts = new ArrayList<>();
        for (FactHandle handle : factHandles) {
            Object fact = session.getObject(handle);
            if (beanMatcher.matches(fact, expectedProperties)) {
                facts.add(fact);
            }
        }
        return facts;
    }
}
