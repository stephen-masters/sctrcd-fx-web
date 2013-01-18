package uk.co.scattercode.iban.facts;

public class IbanValidationAnnotation {

    private String iban;
    private boolean isValid;
    private String message;

    public IbanValidationAnnotation(String iban, boolean isValid) {
        this.iban = iban;
        this.isValid = isValid;
    }
    
    public IbanValidationAnnotation(String iban, boolean isValid, String message) {
        this(iban, isValid);
        this.message = message;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
