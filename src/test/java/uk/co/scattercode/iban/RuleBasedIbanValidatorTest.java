package uk.co.scattercode.iban;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

class RuleBasedIbanValidatorTest {

    static final String[] validIbans = SimpleIbanValidatorTest.validIbans;
    static final String[] invalidIbans = SimpleIbanValidatorTest.invalidIbans;

    // Building the knowledge base compiles the DRL, so share one across tests.
    static final RuleBasedIbanValidator ruleBasedValidator = new RuleBasedIbanValidator();

    @ParameterizedTest
    @FieldSource("validIbans")
    void shouldAcceptValidIbans(String iban) {
        IbanValidationResult result = ruleBasedValidator.validate(iban);
        assertTrue(result.isValid());
        assertThat(result.getAnnotations()).isEmpty();
    }

    @ParameterizedTest
    @FieldSource("invalidIbans")
    void shouldRejectInvalidIbans(String iban) {
        IbanValidationResult result = ruleBasedValidator.validate(iban);
        assertFalse(result.isValid());
    }

    /**
     * The MOD-97 check passes, so this one reaches the rules, and the country
     * code rule rejects it.
     */
    @Test
    void shouldRejectAnUnknownCountryThroughTheRules() {
        IbanValidationResult result = ruleBasedValidator.validate(SimpleIbanValidatorTest.UNKNOWN_COUNTRY_IBAN);

        assertFalse(result.isValid());
        assertThat(result.getAnnotations()).singleElement().satisfies(annotation -> {
            assertFalse(annotation.isValid());
            assertThat(annotation.getMessage())
                    .isEqualTo("An IBAN must begin with a 2-character country code. 'ZZ' is not a country.");
        });
    }
}
