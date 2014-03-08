package de.yazo_games.mensaguthaben.cardreader;

import java.io.Serializable;

/**
 * Stores Data read from a card
 */
public class ValueData implements Serializable {
	/**
	 * Current value on card, in Euro cents.
	 */
	public int value;
	/**
	 * Last transaction, in Euro cents. null if not supported by card.
	 */
	public Integer lastTransaction;

	public ValueData(int value, Integer lastTransaction) {
		this.value = value;
		this.lastTransaction = lastTransaction;
	}
	public ValueData() {
	}
}
