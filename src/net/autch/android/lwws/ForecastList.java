package net.autch.android.lwws;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ForecastList extends ListActivity {
	private static final String TAG = "ForecastList";
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private Cursor cursor;

	private static final int MID_ADD_CITY = 0x1001;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_list);

		dbHelper = new DBHelper(this);
		db = dbHelper.getWritableDatabase();

		CitiesTableHelper helper = new CitiesTableHelper(db);
		cursor = helper.queryAll();
		if(cursor.getCount() == 0) {
			// cities not registered, navigate to CityPicker
			Intent i = new Intent(this, CityPicker.class);
			startActivityForResult(i, 0);
		}

		startManagingCursor(cursor);

		ListAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, new String[] { "name" }, new int[] { android.R.id.text1 });

		// Bind to our new adapter.
		setListAdapter(adapter);
		setProgressBarVisibility(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MID_ADD_CITY, Menu.NONE, "地点を追加").setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO 自動生成されたメソッド・スタブ
		super.onListItemClick(l, v, position, id);

		Cursor c = (Cursor)getListView().getItemAtPosition(position);
		int city_id = c.getInt(3);

		Intent it = new Intent(this, ForecastDetail.class);
		it.setData(LwwsUri.buildForForecastDetail(city_id));
		it.putExtra("name", c.getString(4));

		startActivity(it);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case MID_ADD_CITY:
			Intent it = new Intent(ForecastList.this, CityPicker.class);
			startActivityForResult(it, MID_ADD_CITY);
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(/*requestCode == MID_ADD_CITY && */ resultCode == RESULT_OK) {
			int city_id = data.getExtras().getInt("city_id");

			CitiesTableHelper helper = new CitiesTableHelper(db);
			helper.insertCity(city_id);
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		db.close();
		dbHelper.close();

		super.onDestroy();
	}
}
