/*
 * DesfireApplication.java
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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DesfireApplication implements Parcelable {
    private int           mId;
    private DesfireFile[] mFiles;

    public DesfireApplication (int id, DesfireFile[] files) {
        mId    = id;
        mFiles = files;
    }

    public int getId () {
        return mId;
    }

    public DesfireFile[] getFiles () {
        return mFiles;
    }

	///
	/// Returns the [DesfireFile] with the given [ID][DesfireFile#getId()] from the [#mFiles].
	///
	/// @param fileId The ID of the file looked for.
	/// @return The file with the given ID. Null if no such file is present.
	///
    public @Nullable DesfireFile getFile (int fileId) {
        for (DesfireFile file : mFiles) {
            if (file.getId() == fileId)
                return file;
        }
        return null;
    }

    public static final Parcelable.Creator<DesfireApplication> CREATOR = new Parcelable.Creator<DesfireApplication>() {
        public @NonNull DesfireApplication createFromParcel(Parcel source) {
            int id = source.readInt();

            DesfireFile[] files = new DesfireFile[source.readInt()];
            source.readTypedArray(files, DesfireFile.CREATOR);

            return new DesfireApplication(id, files);
        }

        public @NonNull DesfireApplication[] newArray (int size) {
            return new DesfireApplication[size];
        }
    };

    public void writeToParcel (Parcel parcel, int flags) {
        parcel.writeInt(mId);
        parcel.writeInt(mFiles.length);
        parcel.writeTypedArray(mFiles, flags);
    }

    public int describeContents () {
        return 0;
    }    
}