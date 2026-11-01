package uk.co.scattercode.drools.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.event.rule.ObjectInsertedEvent;

/**
 *
 * @author Stephen Masters
 */
class DroolsUtilTest {

    List<Activation> activations;
    List<ObjectInsertedEvent> insertions;

    @BeforeEach
    void setUp() {
        activations = List.of(new Activation("His Rule"), new Activation("121 Her Rule"), new Activation("54 My Rule"));

        insertions = List.of(
                new StubObjectInsertedEvent(new StubBean("happy", "larry")),
                new StubObjectInsertedEvent(new StubBean("aunt", "sally")),
                new StubObjectInsertedEvent(new StubBean("lame", "duck")));
    }

    /**
     * The rule name passed into the isRuleFired method should be matched
     * against the name of each rule in the list of activations. Decision
     * table rules prefix the rule name with a number, so exact matches
     * are not appropriate. So we check when the end of the rule name
     * matches the argument.
     * <p>
     * Note that for additional leniency (rule writers are not expected to
     * be ultra-strict with naming conventions) the match is case-insensitive.
     * </p>
     */
    @Test
    void shouldIdentifyIfRuleFired() {
        assertFalse(DroolsUtil.ruleFired(activations, "Hello me"));
        assertTrue(DroolsUtil.ruleFired(activations, "His Rule"));
        assertTrue(DroolsUtil.ruleFired(activations, "Her Rule"));
        assertTrue(DroolsUtil.ruleFired(activations, "121 Her Rule"));
        assertTrue(DroolsUtil.ruleFired(activations, "121 Her rule"));
        assertFalse(DroolsUtil.ruleFired(activations, "54"));
    }

    @Test
    void shouldFindFactWithSingleMatchingField() {
        assertNotNull(DroolsUtil.findInsertedFact(insertions, "StubBean", new String[] {"foo=happy"}));
    }

    @Test
    void shouldFindFactWithMultipleMatchingFields() {
        assertNotNull(DroolsUtil.findInsertedFact(insertions, "StubBean", new String[] {"foo=lame", "bar=duck"}));
    }

    @Test
    void shouldNotFindFactWithMatchingAndNonMatchingField() {
        assertNull(DroolsUtil.findInsertedFact(insertions, "StubBean", new String[] {"foo=aunt", "bar=duck"}));
    }

    @Test
    void shouldNotFindFactWithSingleNonMatchingField() {
        assertNull(DroolsUtil.findInsertedFact(insertions, "StubBean", new String[] {"foo=flippant"}));
    }

    @Test
    void shouldNotFindFactWithNonExistantField() {
        assertNull(DroolsUtil.findInsertedFact(insertions, "StubBean", new String[] {"fake=larry"}));
    }

    public static class StubBean {
        private final String foo;
        private final String bar;

        public StubBean(String foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }
    }
}
