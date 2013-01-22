package uk.co.scattercode.iban;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.scattercode.iban.facts.IbanValidationAnnotation;

public class IbanValidationResult {

    private String originalForm;
    private String electronicForm;
    private String displayForm;
    private boolean isValid = true;
    private List<IbanValidationAnnotation> annotations = new ArrayList<IbanValidationAnnotation>();

    public IbanValidationResult(String iban) {
        this.originalForm = iban;
        this.electronicForm = IbanUtil.sanitize(iban);
        this.displayForm = IbanFormatter.printFormat(iban);
    }
            
    public IbanValidationResult(String iban, boolean isValid) {
        this(iban);
        this.isValid = isValid;
    }

    public String getOriginalForm() {
        return originalForm;
    }

    public void setOriginalForm(String originalForm) {
        this.originalForm = originalForm;
    }

    public String getElectronicForm() {
        return electronicForm;
    }

    public void setElectronicForm(String electronicForm) {
        this.electronicForm = electronicForm;
    }

    public String getDisplayForm() {
        return displayForm;
    }

    public void setDisplayForm(String displayForm) {
        this.displayForm = displayForm;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public List<IbanValidationAnnotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public void setAnnotations(List<IbanValidationAnnotation> annotations) {
        this.annotations = new ArrayList<IbanValidationAnnotation>(annotations);
        for (IbanValidationAnnotation annotation : annotations) {
            rejectIfAnnotationRejects(annotation);
        }
    }

    public void addAnnotation(IbanValidationAnnotation annotation) {
        annotations.add(annotation);
        rejectIfAnnotationRejects(annotation);
    }

    /**
     * For every annotation added to the result, 
     * @param annotation
     */
    private void rejectIfAnnotationRejects(IbanValidationAnnotation annotation) {
        if (!annotation.isValid()) {
            this.isValid = false;
        }
    }

    public String toString() {
        return originalForm + " -> " + displayForm + " is "
                + (isValid ? "valid" : "not valid");
    }

}
