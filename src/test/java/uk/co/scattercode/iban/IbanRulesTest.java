package uk.co.scattercode.iban;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.scattercode.drools.util.DroolsTestUtil.assertRuleFired;
import static uk.co.scattercode.drools.util.DroolsTestUtil.assertRuleNotFired;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.KnowledgeEnvironment;
import uk.co.scattercode.drools.util.ResourcePathType;
import uk.co.scattercode.iban.facts.IbanValidationRequest;

/**
 * Exercises {@code IbanRules.drl} directly in a stateful session, so the test
 * can see which rules fired.
 */
class IbanRulesTest {

    private static final String RULE = "Must begin with a country ISO code.";

    private KnowledgeEnvironment kenv;

    @BeforeEach
    void setUp() {
        kenv = new KnowledgeEnvironment(new DroolsResource[] {
            new DroolsResource("rules/sctrcd/iban/IbanRules.drl", ResourcePathType.CLASSPATH, ResourceType.DRL)
        });
        kenv.getKnowledgeSession().setGlobal("countryList", new RuleBasedIbanValidator().countries);
    }

    @Test
    void shouldNotFireForAKnownCountry() {
        IbanValidationRequest request = new IbanValidationRequest("GB29 NWBK 6016 1331 9268 19");
        kenv.insert(request);
        kenv.fireAllRules();

        assertRuleNotFired(kenv, RULE);
        assertThat(request.getAnnotations()).isEmpty();
    }

    @Test
    void shouldRejectAnUnknownCountry() {
        IbanValidationRequest request = new IbanValidationRequest("ZZ77NWBK60161331926819");
        kenv.insert(request);
        kenv.fireAllRules();

        assertRuleFired(kenv, RULE);
        assertThat(kenv.findActivations()).hasSize(1);
        assertThat(request.getAnnotations())
                .singleElement()
                .satisfies(annotation -> assertThat(annotation.isValid()).isFalse());
    }
}
