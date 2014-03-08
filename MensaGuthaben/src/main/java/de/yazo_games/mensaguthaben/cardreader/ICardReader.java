package de.yazo_games.mensaguthaben.cardreader;

import com.codebutler.farebot.card.desfire.DesfireException;
import com.codebutler.farebot.card.desfire.DesfireProtocol;

/**
 * Created by Jakob Wenzel on 16.11.13.
 */
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
