/*
 * DesfireFile.java
 *
 * Copyright (C) 2011 Eric Butler
 *
 * Authors:
 * Eric Butler <eric@codebutler.com>
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

package com.codebutler.farebot.card.desfire;

import org.apache.commons.lang3.ArrayUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.codebutler.farebot.card.desfire.DesfireFileSettings.RecordDesfireFileSettings;

public class DesfireFile implements Parcelable {
    private final int                 mId;
    private final DesfireFileSettings mSettings;
    private final byte[]              mData;

    public static DesfireFile create (int fileId, DesfireFileSettings fileSettings, byte[] fileData) {
        if (fileSettings instanceof RecordDesfireFileSettings)
            return new RecordDesfireFile(fileId, fileSettings, fileData);

		return new DesfireFile(fileId, fileSettings, fileData);
    }

    private DesfireFile (int fileId, DesfireFileSettings fileSettings, byte[] fileData) {
        mId       = fileId;
        mSettings = fileSettings;
        mData     = fileData;
    }

    public DesfireFileSettings getFileSettings () {
        return mSettings;
    }

    public int getId () {
        return mId;
    }

    public byte[] getData () {
        return mData;
    }

    public static final Parcelable.Creator<DesfireFile> CREATOR = new Parcelable.Creator<DesfireFile>() {
        public @NonNull DesfireFile createFromParcel(Parcel source) {
            int fileId = source.readInt();
            boolean isError = (source.readInt() == 1);

			if (isError)
				return new InvalidDesfireFile(fileId, source.readString());

            DesfireFileSettings fileSettings = (DesfireFileSettings) source.readParcelable(DesfireFileSettings.class.getClassLoader());
            int    dataLength = source.readInt();
            byte[] fileData   = new byte[dataLength];
            source.readByteArray(fileData);

            return DesfireFile.create(fileId, fileSettings, fileData);
        }

        public @NonNull DesfireFile[] newArray (int size) {
            return new DesfireFile[size];
        }
    };

    public void writeToParcel (Parcel parcel, int flags) {
        parcel.writeInt(mId);
        if (this instanceof InvalidDesfireFile) {
            parcel.writeInt(1);
            parcel.writeString(((InvalidDesfireFile)this).getErrorMessage());
			return;
        }

        parcel.writeInt(0);
        parcel.writeParcelable(mSettings, 0);
        parcel.writeInt(mData.length);
        parcel.writeByteArray(mData);
    }

    public int describeContents () {
        return 0;
    }

    public static class RecordDesfireFile extends DesfireFile {
        private final DesfireRecord[] mRecords;

        private RecordDesfireFile (int fileId, DesfireFileSettings fileSettings, byte[] fileData) {
            super(fileId, fileSettings, fileData);

            RecordDesfireFileSettings settings = (RecordDesfireFileSettings) fileSettings;

            DesfireRecord[] records = new DesfireRecord[settings.curRecords];
            for (int i = 0; i < settings.curRecords; i++) {
                int offset = settings.recordSize * i;
                records[i] = new DesfireRecord(ArrayUtils.subarray(getData(), offset, offset + settings.recordSize));
            }
            mRecords = records;
        }

        public DesfireRecord[] getRecords () {
            return mRecords;
        }
    }

    public static class InvalidDesfireFile extends DesfireFile {
        private final String mErrorMessage;

        public InvalidDesfireFile (int fileId, String errorMessage) {
            super(fileId, null, new byte[0]);
            mErrorMessage = errorMessage;
        }

        public String getErrorMessage () {
            return mErrorMessage;
        }
    }
}