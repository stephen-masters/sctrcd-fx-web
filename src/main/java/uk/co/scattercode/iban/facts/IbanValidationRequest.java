package uk.co.scattercode.iban.facts;

public class IbanValidationRequest {

    private String iban;

    public IbanValidationRequest() {

    }

    public IbanValidationRequest(String iban) {
        this.iban = iban;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

}
