package de.yazo_games.mensaguthaben.cardreader;

import com.codebutler.farebot.card.desfire.DesfireException;
import com.codebutler.farebot.card.desfire.DesfireProtocol;

/**
 * Created by Jakob Wenzel on 16.11.13.
 */
public class Readers implements ICardReader {
	private static Readers instance;
	private ICardReader[] readers = new ICardReader[]{
			new MagnaCartaReader(),
			new IntercardReader()};


	@Override
	public ValueData readCard(DesfireProtocol card) throws DesfireException {
		for (ICardReader reader : readers) {
			ValueData val = reader.readCard(card);
			if (val!=null)
				return val;
		}
		return null;
	}

	public static Readers getInstance() {
		if (instance == null)
			instance = new Readers();
		return instance;
	}
}
