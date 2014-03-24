package de.yazo_games.mensaguthaben;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class AboutActivity extends Activity {

    private void makeLinkClickable(int id) {

        TextView tv = (TextView) findViewById(id);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

	private void showVersion() {
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			TextView tv = (TextView) findViewById(R.id.tvVersion);
			tv.setText(getString(R.string.version)+" "+pInfo.versionName+" ("+pInfo.versionCode+")");
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	private void toast(String text) {

		Toast toast = Toast.makeText(getApplicationContext(), text,
				Toast.LENGTH_LONG);
		toast.show();
		System.out.println(text);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		// Show the Up button in the action bar.
		setupActionBar();

		showVersion();


        makeLinkClickable(R.id.tvCopyright);
        makeLinkClickable(R.id.tvFarebot);
        makeLinkClickable(R.id.tvSource);
        makeLinkClickable(R.id.tvWebsite);

		final Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				try {
					File filename = new File(Environment.getExternalStorageDirectory()+"/mensaguthaben.log");
					filename.createNewFile();
					String cmd = "logcat -d -f "+filename.getAbsolutePath();
					Runtime.getRuntime().exec(cmd).waitFor();

					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					emailIntent.setType("application/octet-stream");
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"jakobwenzel92@gmail.com"});
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Mensa-Guthaben Log");
					emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+filename.getAbsolutePath()));
					startActivity(emailIntent);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
