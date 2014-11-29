/*
 * Readers.java
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

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;

import com.codebutler.farebot.card.desfire.DesfireException;
import com.codebutler.farebot.card.desfire.DesfireProtocol;

import java.io.IOException;

public class Readers implements ICardReader {
	private static final String TAG = Readers.class.getName();
	private static Readers instance;
	private ICardReader[] readers = new ICardReader[]{
			new MagnaCartaReader(),
			new IntercardReader()};


	@Override
	public ValueData readCard(DesfireProtocol card) throws DesfireException {
		Log.i(TAG,"Trying all readers");
		for (ICardReader reader : readers) {
			Log.i(TAG,"Trying "+reader.getClass().getSimpleName());
			ValueData val = reader.readCard(card);
			if (val!=null)
				return val;
		}
		return null;
	}


	public ValueData readTag(Tag tag) throws DesfireException {
		Log.i(TAG,"Loading tag");
		IsoDep tech = IsoDep.get(tag);

		try {
			tech.connect();
		} catch (IOException e) {
			//Tag was removed. We fail silently.
			e.printStackTrace();
			return null;
		}

		try {
			DesfireProtocol desfireTag = new DesfireProtocol(tech);


			//Android has a Bug on Devices using a Broadcom NFC chip. See
			// http://code.google.com/p/android/issues/detail?id=58773
			//A Workaround is to connected to the tag, issue a dummy operation and then reconnect...
			try {
				desfireTag.selectApp(0);
			}catch (ArrayIndexOutOfBoundsException e) {
				//Exception occurs because the actual response is shorter than the error response
				Log.i(TAG, "Broadcom workaround was needed");
			}

			tech.close();
			tech.connect();

			return Readers.getInstance().readCard(desfireTag);


		} catch (IOException e) {
			//This can only happen on tag close. we ignore this.
			e.printStackTrace();
			return null;
		} finally {
			if (tech.isConnected())
				try {
					tech.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	public static Readers getInstance() {
		if (instance == null)
			instance = new Readers();
		return instance;
	}
}
