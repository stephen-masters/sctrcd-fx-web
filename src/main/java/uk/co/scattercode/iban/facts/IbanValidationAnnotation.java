package uk.co.scattercode.iban.facts;

public class IbanValidationAnnotation {

    private boolean isValid;
    private String message;

    public IbanValidationAnnotation(boolean isValid) {
        this.isValid = isValid;
    }
    
    public IbanValidationAnnotation(boolean isValid, String message) {
        this(isValid);
        this.message = message;
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
    
    public String toString() {
        return (isValid ? "Valid: " : "Not valid: ") + message;
    }
    
}
