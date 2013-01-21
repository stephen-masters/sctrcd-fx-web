package uk.co.scattercode.iban.facts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IbanValidationRequest {

    private String iban;
    private List<IbanValidationAnnotation> annotations = new ArrayList<IbanValidationAnnotation>();
    
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

    public List<IbanValidationAnnotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public void addAnnotation(IbanValidationAnnotation annotation) {
        annotations.add(annotation);
    }

}
