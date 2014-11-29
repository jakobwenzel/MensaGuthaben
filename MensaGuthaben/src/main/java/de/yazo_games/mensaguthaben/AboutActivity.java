/*
 * AboutActivity.java
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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class AboutActivity extends ActionBarActivity {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		supportRequestWindowFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_about);
		// Show the Up button in the action bar.
		setupActionBar();

		showVersion();


        makeLinkClickable(R.id.tvCopyright);
        makeLinkClickable(R.id.tvFarebot);
        makeLinkClickable(R.id.tvSource);
        makeLinkClickable(R.id.tvWebsite);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
