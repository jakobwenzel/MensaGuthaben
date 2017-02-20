package de.yazo_games.mensaguthaben;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.Currency;

import de.yazo_games.mensaguthaben.cardreader.ValueData;

public class ValueFragment extends Fragment {
	public static final String VALUE = "value";
	private ValueData valueData;
	private TextView tvCurrentValue;
	private TextView tvLastValue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_value, container, false);

		tvCurrentValue = ((TextView) v.findViewById(R.id.current));
		tvLastValue = (TextView) v.findViewById(R.id.last);

		ViewCompat.setTransitionName(tvCurrentValue, "current");
		ViewCompat.setTransitionName(tvLastValue, "last");

		if (savedInstanceState!=null) {
			valueData = (ValueData) savedInstanceState.getSerializable(VALUE);
		}

		updateView();

		return v;
	}

	private String moneyStr(int i) {
		Locale germany = Locale.GERMANY;
		String currencySymbol = Currency.getInstance(germany).getSymbol();

		float amount = ((float) i) / 1000;

		return String.format(germany, "%.2f%s", amount, currencySymbol);
	}


	private void updateView() {
		if (valueData==null) {
			tvCurrentValue.setText(getString(R.string.place_on_card));
			tvLastValue.setVisibility(View.GONE);
		} else {

			String current = moneyStr(valueData.value);
			tvCurrentValue.setText(current);
			if (valueData.lastTransaction != null) {
				String last = moneyStr(valueData.lastTransaction);
				tvLastValue.setText(getString(R.string.last_withdrawal) + " " + last);
				tvLastValue.setVisibility(View.VISIBLE);
			} else {
				tvLastValue.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putSerializable(VALUE, valueData);
	}

	public ValueData getValueData() {
		return valueData;
	}

	public void setValueData(ValueData valueData) {
		this.valueData = valueData;

		if (tvCurrentValue !=null)
			updateView();
	}
}
