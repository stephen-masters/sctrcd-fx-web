package uk.co.scattercode.iban;

public interface IbanValidator {
	
	public IbanValidationResult validate(String iban);
    
}
