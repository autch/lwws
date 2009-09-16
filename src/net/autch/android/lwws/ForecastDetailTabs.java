package net.autch.android.lwws;

import net.autch.webservice.lwws.Forecast;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class ForecastDetailTabs extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent caller = getIntent();
		String name;
		int city_id;
		name = caller.getStringExtra("name");
		city_id = caller.getIntExtra("city_id", -1);

		setTitle(name + "の天気");

		TabHost tabHost = getTabHost();
		TabSpec tab;
		Intent it, it_tmpl;
		it_tmpl = new Intent(this, ForecastDetail.class);
		it_tmpl.putExtra("name", name);
		it_tmpl.putExtra("city_id", city_id);

		tab = tabHost.newTabSpec("today");
		tab.setIndicator("きょう");
		it = new Intent(it_tmpl);
		it.putExtra("forecastday", Forecast.TODAY);
		tab.setContent(it);
		tabHost.addTab(tab);

		tab = tabHost.newTabSpec("tomorrow");
		tab.setIndicator("明日");
		it = new Intent(it_tmpl);
		it.putExtra("forecastday", Forecast.TOMORROW);
		tab.setContent(it);
		tabHost.addTab(tab);

		tab = tabHost.newTabSpec("dayaftertomorrow");
		tab.setIndicator("あさって");
		it = new Intent(it_tmpl);
		it.putExtra("forecastday", Forecast.DAY_AFTER_TOMORROW);
		tab.setContent(it);
		tabHost.addTab(tab);
	}
}
