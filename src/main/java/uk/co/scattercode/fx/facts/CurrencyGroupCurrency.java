package uk.co.scattercode.fx.facts;

public class CurrencyGroupCurrency {

	private String group;
	private String currency;
	
	public CurrencyGroupCurrency() {
	}

	public CurrencyGroupCurrency(String group, String currency) {
		this.group = group;
		this.currency = currency;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	public String toString() {
		return "CurrencyGroupCurrency:{ group=" + group + ", ccy=" + currency + "}";
	}
}
