package uk.co.scattercode.drools.test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

import uk.co.scattercode.beans.BeanPropertyFilter;
import uk.co.scattercode.drools.util.AbstractStatefulRulesService;
import uk.co.scattercode.drools.util.DroolsUtil;
import uk.co.scattercode.drools.util.FactFinder;

/**
 * Extend this class within your unit tests so that you can gain simplified
 * access to Drools rules evaluations.
 * 
 * @author Stephen Masters
 */
public abstract class AbstractStatefulRulesServiceTest extends AbstractStatefulRulesService {

	protected static final FactFinder factFinder = new FactFinder();

	/**
	 * The test will fail if any of the named rules could not be found in the
	 * list of activations.
	 * 
	 * @param ruleNames
	 *            A list of names of rules to look for.
	 */
    protected void assertRuleFired(String... ruleNames) {
    	for (String ruleName : ruleNames) {
	        assertTrue("Rule [" + ruleName + "] should have fired.", 
	                DroolsUtil.ruleFired(
	                        this.knowledgeEnvironment.agendaEventListener.getActivationList(), 
	                        ruleName));
    	}
    }

    /**
     * The test will fail if any of the named rules fired.
     * 
     * @param ruleNames A list of names of rules to look for.
     */
    protected void assertRuleNotFired(String... ruleNames) {
    	for (String ruleName : ruleNames) {
	        assertFalse("Rule [" + ruleName + "] should not have fired.", 
	                DroolsUtil.ruleFired(
	                        this.knowledgeEnvironment.agendaEventListener.getActivationList(), 
	                        ruleName));
    	}
    }

    /**
	 * A more complex assertion that a fact of the expected class with specified
	 * properties is in working memory.
	 * 
	 * @param factName
	 *            The simple name of the class of the fact we're looking for.
	 * @param expectedProperties
	 *            A sequence of expected property name/value pairs.
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
    protected void assertFactIsInWorkingMemory(final String factClass, BeanPropertyFilter... expectedProperties) {
    	assertTrue(factFinder.findFact(
    			this.knowledgeEnvironment.knowledgeSession, 
    			factClass, 
    			expectedProperties).size() > 0);
    }
    
    protected void assertFactNotInWorkingMemory(final String factClass, BeanPropertyFilter... expectedProperties) {
    	assertTrue(factFinder.findFact(
    			this.knowledgeEnvironment.knowledgeSession, 
    			factClass, 
    			expectedProperties).size() == 0);
    }

}
