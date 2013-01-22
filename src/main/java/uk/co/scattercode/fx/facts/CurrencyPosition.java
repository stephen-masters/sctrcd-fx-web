package uk.co.scattercode.fx.facts;

import java.math.BigDecimal;

public class CurrencyPosition {

	private String currency;
	private BigDecimal position;

	public CurrencyPosition() {
	}

	public CurrencyPosition(String currency, BigDecimal position) {
		this.currency = currency;
		this.position = position;
	}
	
	public CurrencyPosition(String currency, String position) {
		this.currency = currency;
		this.setPosition(position);
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getPosition() {
		return position;
	}

	public void setPosition(BigDecimal position) {
		this.position = position;
	}
	
	public void setPosition(String position) {
		setPosition(new BigDecimal(position));
	}

	public void add(BigDecimal amount) {
		if (position == null) setPosition(BigDecimal.ZERO);
		setPosition(position.add(amount));
	}
	
	public void add(Number amount) {
		add(new BigDecimal(amount.doubleValue()));
	}
	
	public void add(String amount) {
		add(new BigDecimal(amount));
	}
	
}
