package de.yazo_games.mensaguthaben.cardreader;

import com.codebutler.farebot.Utils;
import com.codebutler.farebot.card.desfire.DesfireException;
import com.codebutler.farebot.card.desfire.DesfireFileSettings;
import com.codebutler.farebot.card.desfire.DesfireProtocol;

/**
 * Created by Jakob Wenzel on 16.11.13.
 */
public class MagnaCartaReader implements ICardReader {
	@Override
	public ValueData readCard(DesfireProtocol card) {
		final int appId = 0xF080F3;
		final int fileId = 2;

		//We don't want to use getFileSettings as they are doing some weird stuff with the fileType
		if (Utils.containsAppFile(card,appId,fileId)) {
			try {
				byte[] data = card.readFile(fileId);

				int value = data[6]<<8 | data[7];
				return new ValueData(value,null);

			} catch (DesfireException e) {
				return null;
			}
		} else
			return null;
	}
}
