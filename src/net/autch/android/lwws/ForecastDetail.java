package net.autch.android.lwws;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ForecastDetail extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forecast);

		Intent it = getIntent();
		Bundle extras = it.getExtras();
		if(extras != null) {
			setTitle(extras.getString("name"));
		}

	}
}
