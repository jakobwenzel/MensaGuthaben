package de.yazo_games.mensaguthaben;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codebutler.farebot.card.desfire.DesfireException;

import de.yazo_games.mensaguthaben.cardreader.Readers;
import de.yazo_games.mensaguthaben.cardreader.ValueData;

/**
 * Created by wenzel on 28.11.14.
 */
public class PopupActivity extends ActionBarActivity {

	private static String TAG = PopupActivity.class.getSimpleName();

	private static final String VALUE_TAG = "value";
	private ValueFragment valueFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.popup_main);


		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.app_name);
		setSupportActionBar(toolbar);

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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        finish();
//    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, R.id.fullscreen, Menu.NONE, R.string.fullscreen).setIcon(R.drawable.ic_action_full_screen).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.fullscreen:
				Intent intent = new Intent(PopupActivity.this, MainActivity.class);
				intent.setAction(MainActivity.ACTION_FULLSCREEN);
				intent.putExtra(MainActivity.EXTRA_VALUE, valueFragment.getValueData());

				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
					animateActivity21(intent);
				} else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
					animateActivity16(intent);
				} else {
					startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@TargetApi(21)
	private void animateActivity21(Intent intent) {
		ActivityOptions options;
		if (valueFragment.getValueData()!=null) {
			options = ActivityOptions.makeSceneTransitionAnimation(PopupActivity.this,
					Pair.create(findViewById(R.id.current), "current"),
					Pair.create(findViewById(R.id.last), "last"),
					Pair.create(findViewById(R.id.toolbar), "toolbar")
			);
		} else {
			options = ActivityOptions.makeSceneTransitionAnimation(PopupActivity.this,
					Pair.create(findViewById(R.id.current), "current"),
					Pair.create(findViewById(R.id.toolbar), "toolbar")
			);
		}

		startActivity(intent, options.toBundle());
		//ActivityCompat.startActivity(PopupActivity.this,intent,
		//		ActivityOptionsCompat.makeSceneTransitionAnimation(PopupActivity.this).toBundle());
		//finish();
	}

	@TargetApi(16)
	private void animateActivity16(Intent intent) {
		View root = findViewById(R.id.popupRoot);

		ActivityOptions options = ActivityOptions.makeScaleUpAnimation(root,root.getLeft(), root.getTop(), root.getWidth(), root.getHeight());
		startActivity(intent, options.toBundle());
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
