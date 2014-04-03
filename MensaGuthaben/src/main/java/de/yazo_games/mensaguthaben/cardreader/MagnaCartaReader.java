package de.yazo_games.mensaguthaben.cardreader;

import android.util.Log;

import com.codebutler.farebot.Utils;
import com.codebutler.farebot.card.desfire.DesfireException;
import com.codebutler.farebot.card.desfire.DesfireFileSettings;
import com.codebutler.farebot.card.desfire.DesfireProtocol;

/**
 * Created by Jakob Wenzel on 16.11.13.
 */
public class MagnaCartaReader implements ICardReader {
	private static final String TAG = MagnaCartaReader.class.getName();
	@Override
	public ValueData readCard(DesfireProtocol card) {
		final int appId = 0xF080F3;
		final int fileId = 2;

		//We don't want to use getFileSettings as they are doing some weird stuff with the fileType
		try {
			card.selectApp(appId);
		} catch (DesfireException e) {
			Log.w(TAG,"App not found");
			Log.w(TAG, e);
			return null;
		}

		//For some reason we can't use getFileList either, because the card answers with an
		//authentication error

		try {
			byte[] data = card.readFile(fileId);

			int value = data[6]<<8 | data[7];
			return new ValueData(value,null);

		} catch (DesfireException e) {
			Log.w(TAG,"Exception while reading tag",e);
			Log.w(TAG, e);
			return null;
		}
	}
}
