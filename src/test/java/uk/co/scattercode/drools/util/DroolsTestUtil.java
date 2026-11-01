package uk.co.scattercode.drools.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import uk.co.scattercode.beans.BeanPropertyFilter;

public class DroolsTestUtil {

    protected static final FactFinder factFinder = new FactFinder();

    /**
     * This class provides static methods, so we prevent instantiation.
     */
    private DroolsTestUtil() {}

    /**
     * The test will fail if any of the named rules could not be found in the
     * list of activations.
     *
     * @param ruleNames
     *            A list of names of rules to look for.
     */
    public static void assertRuleFired(KnowledgeEnvironment kenv, String... ruleNames) {
        for (String ruleName : ruleNames) {
            assertTrue(
                    DroolsUtil.ruleFired(kenv.findActivations(), ruleName),
                    "Rule [" + ruleName + "] should have fired.");
        }
    }

    /**
     * The test will fail if any of the named rules fired.
     *
     * @param ruleNames A list of names of rules to look for.
     */
    public static void assertRuleNotFired(KnowledgeEnvironment kenv, String... ruleNames) {
        for (String ruleName : ruleNames) {
            assertFalse(
                    DroolsUtil.ruleFired(kenv.findActivations(), ruleName),
                    "Rule [" + ruleName + "] should not have fired.");
        }
    }

    /**
     * A more complex assertion that a fact of the expected class with specified
     * properties is in working memory.
     *
     * @param factClass
     *            The simple name of the class of the fact we're looking for.
     * @param expectedProperties
     *            A sequence of expected property name/value pairs.
     */
    public static void assertFactIsInWorkingMemory(
            KnowledgeEnvironment kenv, final String factClass, BeanPropertyFilter... expectedProperties) {
        assertFalse(factFinder.findFacts(kenv, factClass, expectedProperties).isEmpty());
    }

    public static void assertFactNotInWorkingMemory(
            KnowledgeEnvironment kenv, final String factClass, BeanPropertyFilter... expectedProperties) {
        assertTrue(factFinder.findFacts(kenv, factClass, expectedProperties).isEmpty());
    }
}
