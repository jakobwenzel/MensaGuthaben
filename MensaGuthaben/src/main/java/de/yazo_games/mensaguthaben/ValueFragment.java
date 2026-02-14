package de.yazo_games.mensaguthaben;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
	public @NonNull View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_value, container, false);

		tvCurrentValue = ((TextView) view.findViewById(R.id.current));
		tvLastValue = (TextView) view.findViewById(R.id.last);

		ViewCompat.setTransitionName(tvCurrentValue, "current");
		ViewCompat.setTransitionName(tvLastValue, "last");

		if (savedInstanceState!=null) {
			valueData = (ValueData) savedInstanceState.getSerializable(VALUE);
		}

		updateView();

		return view;
	}

	private @NonNull String moneyStr(int i) {
		Locale germany = Locale.GERMANY;
		String currencySymbol = Currency.getInstance(germany).getSymbol();

		float amount = ((float) i) / 1000;

		return String.format(germany, "%.2f%s", amount, currencySymbol);
	}


	public void updateView() {
		if (valueData==null) {
			tvCurrentValue.setText(getString(R.string.place_on_card));
			tvLastValue.setVisibility(View.GONE);
			return;
		}

		String current = moneyStr(valueData.value);
		tvCurrentValue.setText(current);

		if (valueData.lastTransaction == null) {
			tvLastValue.setVisibility(View.GONE);
			return;
		}

		String last = moneyStr(valueData.lastTransaction);
		tvLastValue.setText(getString(R.string.last_withdrawal) + " " + last);
		tvLastValue.setVisibility(View.VISIBLE);
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


	}
}
