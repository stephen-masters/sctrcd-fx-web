package uk.co.scattercode.iban;

public class IbanValidationResult {

	String originalForm;
	String electronicForm;
	String displayForm;
	boolean isValid;
	
	public IbanValidationResult(String originalForm, boolean isValid, String electronicForm, String displayForm) {
		this.originalForm = originalForm;
		this.electronicForm = electronicForm;
		this.displayForm = displayForm;
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
	
	public String toString() {
	    return originalForm + " -> " + displayForm 
	            + " is " + (isValid ? "valid" : "not valid");
	} 
	
}
