package de.yazo_games.mensaguthaben.cardreader;

import com.codebutler.farebot.Utils;
import com.codebutler.farebot.card.desfire.DesfireException;
import com.codebutler.farebot.card.desfire.DesfireFileSettings;
import com.codebutler.farebot.card.desfire.DesfireProtocol;

/**
 * Created by Jakob Wenzel on 16.11.13.
 */
public class IntercardReader implements ICardReader {
	@Override
	public ValueData readCard(DesfireProtocol card) throws DesfireException {

		final int appId = 0x5F8415;
		final int fileId = 1;
		DesfireFileSettings settings = Utils.selectAppFile(card, appId, fileId);

		if (settings instanceof DesfireFileSettings.ValueDesfireFileSettings) {
			DesfireFileSettings.ValueDesfireFileSettings value = (DesfireFileSettings.ValueDesfireFileSettings) settings;

			System.out.println("Reading value");
			int data = 0;
			try {
				data = card.readValue(fileId);
				//Values are saved in thenths of cents, so divide by ten...
				return new ValueData(data/10,value.value/10);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		} else {
			return null;
		}
	}
}
