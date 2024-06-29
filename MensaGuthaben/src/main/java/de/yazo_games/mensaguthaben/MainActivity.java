/*
 * MainActivity.java
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

package de.yazo_games.mensaguthaben;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codebutler.farebot.NfcOffFragment;
import com.codebutler.farebot.card.desfire.DesfireException;

import de.yazo_games.mensaguthaben.cardreader.Readers;
import de.yazo_games.mensaguthaben.cardreader.ValueData;


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {
    private static final String VALUE_TAG = "Value Fragment";
    public static final String EXTRA_VALUE = "valueData";
    public static final String ACTION_FULLSCREEN = "de.yazo_games.mensaguthaben.Fullscreen";

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private IntentFilter mIntentFilter;

    boolean mResumed = false;

    private static final String TAG = MainActivity.class.getName();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED.equals(action)) {
                updateNfcState();
            }
        }
    };

    private ValueFragment valueFragment;

    public void updateNfcState() {

        if (!mAdapter.isEnabled() && mResumed) {
            NfcOffFragment f = new NfcOffFragment();
            f.show(getSupportFragmentManager(), NfcOffFragment.TAG);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.i(TAG, "activity started");

        FragmentManager fm = getSupportFragmentManager();


        valueFragment = (ValueFragment) fm.findFragmentByTag(VALUE_TAG);
        if (valueFragment == null) {
            valueFragment = new ValueFragment();
        }
        fm.beginTransaction().replace(R.id.main, valueFragment, VALUE_TAG).commit();

        if (getIntent().getAction().equals(ACTION_FULLSCREEN)) {
            ValueData valueData = (ValueData) getIntent().getSerializableExtra(EXTRA_VALUE);
            valueFragment.setValueData(valueData);

            setResult(0);


        }

        Boolean autostart = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("autostart", true);
        AutostartRegister.register(getPackageManager(), autostart);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mIntentFilter = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");


        // Create a generic PendingIntent that will be deliver to this activity.
        // The NFC stack
        // will fill in the intent with the details of the discovered tag before
        // delivering to
        // this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);


        // Setup an intent filter
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{tech,};
        mTechLists = new String[][]{new String[]{IsoDep.class.getName(),
                NfcA.class.getName()}};

        if (getIntent().getAction().equals(ACTION_FULLSCREEN) && !hasNewData) {
            ValueData valueData = (ValueData) getIntent().getSerializableExtra(EXTRA_VALUE);
            Log.w(TAG, "restoring data for fullscreen");
            valueFragment.setValueData(valueData);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAdapter.enableReaderMode(this, this,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE |
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, null);
        }
    }

    boolean hasNewData = false;

    @Override
    public void onNewIntent(Intent intent) {
        Log.i(TAG, "Foreground dispatch");
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Log.i(TAG, "Discovered tag with intent: " + intent);
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


            try {
                ValueData val = Readers.getInstance().readTag(tag);
                Log.w(TAG, "Setting read data");
                valueFragment.setValueData(val);
                hasNewData = true;

            } catch (DesfireException e) {
                Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show();
            }
        } else if (getIntent().getAction().equals(ACTION_FULLSCREEN)) {
            ValueData valueData = (ValueData) getIntent().getSerializableExtra(EXTRA_VALUE);
            valueFragment.setValueData(valueData);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onResume() {

        super.onResume();
        mResumed = true;
        getApplicationContext().registerReceiver(mReceiver, mIntentFilter);

        updateNfcState();

        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);


		/*if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
			Log.i(TAG,"Started by tag discovery");
			onNewIntent(getIntent());
		} else */
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResumed = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Intent myIntent = new Intent(this, AboutActivity.class);
            startActivity(myIntent);
            return true;
        }

        if (item.getItemId() == R.id.action_settings) {
            Intent myIntent = new Intent(this, SettingsActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        valueFragment = null;
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        try {
            ValueData val = Readers.getInstance().readTag(tag);
            valueFragment.setValueData(val);
            this.runOnUiThread(() -> valueFragment.updateView());
            hasNewData = true;
        } catch (DesfireException e) {
            throw new RuntimeException(e);
        }
    }

}
