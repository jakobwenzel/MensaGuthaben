package de.yazo_games.mensaguthaben;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codebutler.farebot.card.desfire.DesfireException;

import de.yazo_games.mensaguthaben.cardreader.Readers;
import de.yazo_games.mensaguthaben.cardreader.ValueData;

/**
 * Created by wenzel on 28.11.14.
 */
public class PopupActivity extends FragmentActivity {

	private static String TAG = PopupActivity.class.getSimpleName();

	private static final String VALUE_TAG = "value";
	private ValueFragment valueFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.popup_main);


		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.app_name);
		toolbar.getMenu().add(Menu.NONE,R.id.fullscreen,Menu.NONE,R.string.fullscreen).setIcon(R.drawable.ic_action_full_screen).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				switch (menuItem.getItemId()) {
					case R.id.fullscreen:
						Intent intent = new Intent(PopupActivity.this, MainActivity.class);
						intent.setAction(MainActivity.ACTION_FULLSCREEN);
						intent.putExtra(MainActivity.EXTRA_VALUE, valueFragment.getValueData());

						Log.w(TAG,findViewById(R.id.current).toString());
						ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(PopupActivity.this,
								Pair.create(findViewById(R.id.current), "current"),
								Pair.create(findViewById(R.id.last), "last"),
								Pair.create(findViewById(R.id.toolbar), "toolbar")
						);

						ActivityCompat.startActivityForResult(PopupActivity.this, intent,0, options.toBundle());
						//ActivityCompat.startActivity(PopupActivity.this,intent,
						//		ActivityOptionsCompat.makeSceneTransitionAnimation(PopupActivity.this).toBundle());
						finish();
						return true;
					default:
						return false;
				}
			}
		});

		Log.i(TAG, "activity started");


		FragmentManager fm = getSupportFragmentManager();

		valueFragment = (ValueFragment) fm.findFragmentByTag(VALUE_TAG);
		if (valueFragment ==null) {
			valueFragment = new ValueFragment();
			fm.beginTransaction().replace(R.id.main, valueFragment,VALUE_TAG).commit();
		}



		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
			Log.i(TAG, "Started by tag discovery");
			onNewIntent(getIntent());
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Log.i(TAG,"Discovered tag with intent: " + intent);
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


			try {
				ValueData val = Readers.getInstance().readTag(tag);

				valueFragment.setValueData(val);


			} catch (DesfireException e) {
				Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		valueFragment = null;
	}
}
