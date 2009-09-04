package net.autch.android.lwws;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class ForecastDetailTabs extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TabHost tabHost = getTabHost();

		TabSpec tab = tabHost.newTabSpec("today");
		tab.setIndicator("きょう");
		tab.setContent(new Intent(this, ForecastDetail.class));
		tabHost.addTab(tab);

		tab = tabHost.newTabSpec("tomorrow");
		tab.setIndicator("明日");
		tab.setContent(new Intent(this, ForecastDetail.class));
		tabHost.addTab(tab);

		tab = tabHost.newTabSpec("dayaftertomorrow");
		tab.setIndicator("あさって");
		tab.setContent(new Intent(this, ForecastDetail.class));
		tabHost.addTab(tab);
	}
}
