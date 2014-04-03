/*
 * ICardReader.java
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

import com.codebutler.farebot.card.desfire.DesfireException;
import com.codebutler.farebot.card.desfire.DesfireProtocol;

public interface ICardReader {
	/**
	 * Try to read data from a card.
	 *
	 * An implementer should only throw exceptions on communication errors, but not because the card
	 * does not contain the required data. In that case, null should be returned.
	 *
	 * @param card The card to read
	 * @return Card's data, null if unsupported.
	 * @throws DesfireException Communication error
	 */
	public ValueData readCard(DesfireProtocol card) throws DesfireException;
}
