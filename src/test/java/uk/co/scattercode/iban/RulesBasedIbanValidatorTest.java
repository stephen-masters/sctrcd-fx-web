package uk.co.scattercode.iban;

import static org.junit.Assert.*;

import org.junit.Test;

public class RulesBasedIbanValidatorTest {

    RuleBasedIbanValidator ruleBasedValidator = new RuleBasedIbanValidator();
    
    @Test
    public final void shouldAcceptValidIbans() {
        for (String iban : SimpleIbanValidatorTest.validIbans) {
            IbanValidationResult result = ruleBasedValidator.validate(iban);
            assertTrue(result.isValid());
        }
    }
    
    @Test
    public final void shouldRejectInvalidIbans() {
        for (String iban : SimpleIbanValidatorTest.invalidIbans) {
            IbanValidationResult result = ruleBasedValidator.validate(iban);
            assertFalse(result.isValid());
        }
    }

}
