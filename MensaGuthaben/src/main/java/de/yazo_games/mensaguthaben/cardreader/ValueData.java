/*
 * ValueData.java
 *
 * Copyright (C) 2014 Jakob Wenzel
 *
 * Authors:
 * Jakob Wenzel <jakobwenzel92@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
