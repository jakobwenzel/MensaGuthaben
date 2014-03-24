package de.yazo_games.mensaguthaben;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codebutler.farebot.Utils;
import com.codebutler.farebot.card.desfire.DesfireException;
import com.codebutler.farebot.card.desfire.DesfireFileSettings;
import com.codebutler.farebot.card.desfire.DesfireProtocol;

import java.io.IOException;


import de.yazo_games.mensaguthaben.cardreader.Readers;
import de.yazo_games.mensaguthaben.cardreader.ValueData;


public class MainActivity extends Activity {
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
    private IntentFilter mIntentFilter;

	private static final String TAG = MainActivity.class.getName();

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	String action = intent.getAction();
            if ("android.nfc.action.ADAPTER_STATE_CHANGED".equals(action)) {
            	updateNfcState();
            }
        }
    };
	boolean wasDisabled;
	
	boolean lastNfcState = true;

	private boolean cardLoaded = false;
	private ValueData value;

	public void updateNfcState() {
		//Do nothing if no change
		if (mAdapter.isEnabled()==lastNfcState) return;
		lastNfcState = mAdapter.isEnabled();

		TextView currentTv = (TextView) findViewById(R.id.current);
		TextView lastTv = (TextView) findViewById(R.id.last);
		if (mAdapter.isEnabled()) {
			currentTv.setText(R.string.auf_mensakarte_legen_);
			lastTv.setText("");
		} else {
			currentTv.setText(R.string.turn_nfc_on);
			lastTv.setText("");
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.i(TAG,"activity started");


        //Register or unregister for autostart (in case is has never been done)
        Boolean autostart = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("autostart",true);
        AutostartRegister.register(getPackageManager(),autostart);


		mAdapter = NfcAdapter.getDefaultAdapter(this);
        mIntentFilter = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");
		
		Utils.killDialog();


		wasDisabled = !mAdapter.isEnabled();
		// Create a generic PendingIntent that will be deliver to this activity.
		// The NFC stack
		// will fill in the intent with the details of the discovered tag before
		// delivering to
		// this activity.
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Setup an intent filter
		IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		mFilters = new IntentFilter[] { tech, };
		mTechLists = new String[][] { new String[] { IsoDep.class.getName(),
				NfcA.class.getName() } };

		if (savedInstanceState != null) {
			cardLoaded = savedInstanceState.getBoolean("cardLoaded");
			if (cardLoaded) {
				value = (ValueData) savedInstanceState.getSerializable("value");
				updateView(value);
			}
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.i(TAG,"Foreground dispatch");
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Log.i(TAG,"Discovered tag with intent: " + intent);
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			loadCard(tag);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void toast(String text) {

		Toast toast = Toast.makeText(getApplicationContext(), text,
				Toast.LENGTH_LONG);
		toast.show();
		Log.i(TAG,"Showing toast: "+text);
	}

	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	private String moneyStr(int i) {
		int euros = i / 100;
		int cents = i % 100;

		String centsStr = Integer.toString(cents);
		if (cents<10) centsStr = "0"+centsStr;
		return euros + "," + centsStr + "\u20AC"; //Last one is euro sign
	}


	private void updateView(ValueData value) {
		this.cardLoaded = true;

		String current = moneyStr(value.value);
		TextView currentTv = (TextView) findViewById(R.id.current);
		currentTv.setText(current);

		TextView lastTv = (TextView) findViewById(R.id.last);
		if (value.lastTransaction!=null) {
			String last = moneyStr(value.lastTransaction);
			lastTv.setText(getString(R.string.last_withdrawal)+" " + last);
		} else {
			lastTv.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putBoolean("cardLoaded", cardLoaded);
		bundle.putSerializable("value", value);
	}

	private void  loadCard(Tag tag) {
		try {
			Log.i(TAG,"Loading tag");
			IsoDep tech = IsoDep.get(tag);

			tech.connect();

			DesfireProtocol desfireTag = new DesfireProtocol(tech);

			value = Readers.getInstance().readCard(desfireTag);
			if (value!=null)
				updateView(value);
			else toast(getString(R.string.card_not_supported));

		} catch (DesfireException ex) {
			ex.printStackTrace();
			toast(getString(R.string.communication_fail));
		} catch (IOException e) {
			e.printStackTrace();
			toast(getString(R.string.communication_fail) + " ioexception");
		}

	}

	@Override
	public void onResume() {

		super.onResume();
        getApplicationContext().registerReceiver(mReceiver, mIntentFilter);

		Utils.checkNfcEnabled(this,mAdapter);
		
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
				mTechLists);
		
		

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
			Log.i(TAG,"Started by tag discovery");
			onNewIntent(getIntent());

		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==R.id.action_about) {
            Intent myIntent = new Intent(this, AboutActivity.class);
            startActivityForResult(myIntent, 0);
			return true;
		}

        if (item.getItemId()==R.id.action_settings) {
            Intent myIntent = new Intent(this, SettingsActivity.class);
            startActivityForResult(myIntent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
	}

}
