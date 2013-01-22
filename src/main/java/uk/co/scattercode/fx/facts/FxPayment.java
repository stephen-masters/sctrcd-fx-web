package uk.co.scattercode.fx.facts;

import java.math.BigDecimal;

public class FxPayment {

	private String sellCurrency;
	private String buyCurrency;
	private BigDecimal sellAmount;
	private BigDecimal buyAmount;
	private Leg fixedLeg;
	
	public FxPayment() {
		
	}
	
	public FxPayment(String sellCurrency, String buyCurrency, BigDecimal sellAmount, BigDecimal buyAmount, Leg fixedLeg) {
		this.sellCurrency = sellCurrency;
		this.buyCurrency = buyCurrency;
		this.sellAmount = sellAmount;
		this.buyAmount = buyAmount;
		this.fixedLeg = fixedLeg;
	}
	
	public String getSellCurrency() {
		return sellCurrency;
	}
	public void setSellCurrency(String sellCurrency) {
		this.sellCurrency = sellCurrency;
	}
	public String getBuyCurrency() {
		return buyCurrency;
	}
	public void setBuyCurrency(String buyCurrency) {
		this.buyCurrency = buyCurrency;
	}
	public BigDecimal getSellAmount() {
		return sellAmount;
	}
	public void setSellAmount(BigDecimal sellAmount) {
		this.sellAmount = sellAmount;
	}
	public BigDecimal getBuyAmount() {
		return buyAmount;
	}
	public void setBuyAmount(BigDecimal buyAmount) {
		this.buyAmount = buyAmount;
	}
	
	public Leg getFixedLeg() {
		return fixedLeg;
	}

	public void setFixedLeg(Leg fixedLeg) {
		this.fixedLeg = fixedLeg;
	}

	public enum Leg {
		SELL,
		BUY;
	}
}
