package uk.co.scattercode.fx.facts;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A group of currencies with an arbitrary name. Keeps track of the sum of
 * positions to member and non-member currencies.
 */
public class CurrencyGroup {

	/** The name of the group. */
	private String name;

	/** The currencies in this group. */
	private Set<String> currencies = new HashSet<String>();

	/** Sum of positions for currencies in this group. */
	private BigDecimal position = BigDecimal.ZERO;

	/** Sum of positions for currencies which are not members of this group. */
	private BigDecimal nonMemberPosition = BigDecimal.ZERO;

	/**
	 * Default void constructor.
	 */
	public CurrencyGroup() {
	}

	/**
	 * Constructor initialises the group with a name.
	 * 
	 * @param name
	 *            The name given to the group.
	 */
	public CurrencyGroup(String name) {
		this.name = name;
	}

	/**
	 * @return The name given to the group.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name given to the group.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The list of currencies in this group as an unmodifiable set.
	 */
	public Set<String> getCurrencies() {
		return Collections.unmodifiableSet(currencies);
	}

	/**
	 * Reinitialises the set of currencies with the values provided. Note that a
	 * new {@link Set} is created containing the values, so subsequent
	 * modifications to the original collection will have no effect on its
	 * contents. If you wish to modify the list, you should make use of the
	 * add/remove methods.
	 * 
	 * @param currencies
	 *            A collection of currencies.
	 */
	public void setCurrencies(Collection<String> currencies) {
		this.currencies = new HashSet<String>();
		this.addCurrencies(currencies);
	}

	/**
	 * Adds currencies to the existing {@link Set}.
	 * 
	 * @param currencies
	 *            A {@link Collection} of currencies.
	 */
	public void addCurrencies(Collection<String> currencies) {
		this.currencies.addAll(currencies);
	}

	/**
	 * @param currency
	 *            The currency to add to this group.
	 */
	public void addCurrency(String currency) {
		this.currencies.add(currency);
	}

	/**
	 * @param currency
	 *            The currency to remove from this group.
	 */
	public void removeCurrency(String currency) {
		this.currencies.remove(currency);
	}

	/**
	 * Sum of positions for currencies in this group.
	 */
	public BigDecimal getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            Sum of positions for currencies in this group.
	 */
	public void setPosition(BigDecimal position) {
		this.position = position;
	}

	/**
	 * Sum of positions for currencies which are not members of this group.
	 */
	public BigDecimal getNonMemberPosition() {
		return nonMemberPosition;
	}

	/**
	 * @param position
	 *            Sum of positions for currencies which are not members of this
	 *            group.
	 */
	public void setNonMemberPosition(BigDecimal position) {
		this.nonMemberPosition = position;
	}

}
