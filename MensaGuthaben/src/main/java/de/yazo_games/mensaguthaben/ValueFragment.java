package de.yazo_games.mensaguthaben;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.totschnig.myexpenses.TransactionsContract;

import java.util.Locale;
import java.util.Currency;

import de.yazo_games.mensaguthaben.cardreader.ValueData;

public class ValueFragment extends Fragment {
	public static final String VALUE = "value";
	private ValueData valueData;
	private TextView tvCurrentValue;
	private TextView tvLastValue;
	private Button button;

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
		button = (Button) v.findViewById(R.id.button);


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
				button.setOnClickListener( new ValueDataOnClickListener(valueData));
				button.setVisibility(View.VISIBLE);
			} else {
				tvLastValue.setVisibility(View.GONE);
				button.setVisibility(View.GONE);
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

	public class ValueDataOnClickListener implements View.OnClickListener
	{

		ValueData valueData;

		public ValueDataOnClickListener(ValueData valueData) {
			this.valueData = valueData;
		}

		@Override
		public void onClick(View v) {
			Long amount = (long) (valueData.lastTransaction * -1000);
			Intent intent = new Intent(Intent.ACTION_INSERT);
			intent.setData(Uri.parse("content://org.totschnig.myexpenses/transactions"));

			intent.putExtra(TransactionsContract.Transactions.AMOUNT_MICROS, amount);
			intent.putExtra(TransactionsContract.Transactions.PAYEE_NAME, "Mensa");
			intent.putExtra(TransactionsContract.Transactions.CATEGORY_LABEL, "Food:Canteen");
			Log.i("onClick",amount.toString());
			startActivity(intent);


		}

	}
}
