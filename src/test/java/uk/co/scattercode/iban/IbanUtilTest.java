package uk.co.scattercode.iban;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class IbanUtilTest {

    @Test
    void shouldSanitizeIban() {
        assertEquals("ANIBAN12340", IbanUtil.sanitize("an iban12340"));
        assertEquals("ANIBAN12340", IbanUtil.sanitize("an-ib-an-12340"));
        assertEquals("ANIBAN12340", IbanUtil.sanitize("an£-ib@-%a*n1^234(0!"));
    }

    @Test
    void shouldPrintInGroupsOfFour() {
        assertEquals("GB29 NWBK 6016 1331 9268 19", IbanFormatter.printFormat("gb29nwbk60161331926819"));
    }
}
