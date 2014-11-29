package com.codebutler.farebot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import de.yazo_games.mensaguthaben.R;

/**
 * Created by wenzel on 28.11.14.
 */
public class NfcOffFragment extends DialogFragment {
	public static final String TAG = "NfcOffFragment";

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.nfc_off)
			.setMessage(R.string.turn_nfc_on)
			.setCancelable(true)
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			})
			.setNeutralButton(R.string.goto_settings, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
						getActivity().startActivity(intent);
					} else {
						Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
						getActivity().startActivity(intent);
					}
				}
			}).create();
	}
}
