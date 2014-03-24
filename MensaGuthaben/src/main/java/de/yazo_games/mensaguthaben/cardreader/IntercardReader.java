package de.yazo_games.mensaguthaben.cardreader;

import android.util.Log;

import com.codebutler.farebot.Utils;
import com.codebutler.farebot.card.desfire.DesfireException;
import com.codebutler.farebot.card.desfire.DesfireFileSettings;
import com.codebutler.farebot.card.desfire.DesfireProtocol;

/**
 * Created by Jakob Wenzel on 16.11.13.
 */
public class IntercardReader implements ICardReader {
	private static final String TAG = IntercardReader.class.getName();
	@Override
	public ValueData readCard(DesfireProtocol card) throws DesfireException {

		final int appId = 0x5F8415;
		final int fileId = 1;
		Log.i(TAG,"Selecting app and file");
		DesfireFileSettings settings = Utils.selectAppFile(card, appId, fileId);

		if (settings instanceof DesfireFileSettings.ValueDesfireFileSettings) {
			Log.i(TAG,"found value file");
			DesfireFileSettings.ValueDesfireFileSettings value = (DesfireFileSettings.ValueDesfireFileSettings) settings;

			Log.i(TAG, "Reading value");
			int data = 0;
			try {
				data = card.readValue(fileId);
				//Values are saved in thenths of cents, so divide by ten...
				return new ValueData(data/10,value.value/10);
			} catch (Exception e) {
				Log.w(TAG,"Exception while trying to read value",e);
				return null;
			}

		} else {
			Log.i(TAG,"File is not a value file, tag is incompatible.");
			return null;
		}
	}
}
