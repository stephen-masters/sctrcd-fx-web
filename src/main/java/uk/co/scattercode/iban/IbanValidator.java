package uk.co.scattercode.iban;

public interface IbanValidator {

    IbanValidationResult validate(String iban);
}
