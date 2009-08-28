package net.autch.android.lwws;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CityPicker extends ListActivity {
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true);
		setContentView(R.layout.simple_list);

		Bundle extras = getIntent().getExtras();
		int area_id = -1, pref_id = -1;

		if(extras != null) {
			area_id = extras.getInt("area_id", -1);
			pref_id = extras.getInt("pref_id", -1);
		}

		dbHelper = new DBHelper(this);
		db = dbHelper.getReadableDatabase();
		ForecastMapTableHelper helper = new ForecastMapTableHelper(db);

		cursor = helper.queryAllArea();
		if(cursor.getCount() == 0) {
			// forecastmap not loaded, navigate to UpdateForecastMap
			Intent i = new Intent(this, UpdateForecastMap.class);
			i.putExtra("force", true);
			startActivityForResult(i, 0);
		}
		cursor.close();

		if(area_id == -1 && pref_id == -1)
		{
			cursor = helper.queryAllArea();
			setTitle("地点の追加: 地域を選択");
		}
		else if(area_id != -1 && pref_id == -1)
		{
			cursor = helper.queryAllPref(area_id);
			setTitle("地点の追加: 都道府県を選択");
		}
		else
		{
			cursor = helper.queryAllCity(pref_id);
			setTitle("地点の追加: 都市を選択");
		}
		startManagingCursor(cursor);

		ListAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, new String[] { "name" }, new int[] { android.R.id.text1 });

		// Bind to our new adapter.
		setListAdapter(adapter);
		setProgressBarVisibility(false);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent intent = new Intent(getIntent());
		Cursor c = (Cursor)getListView().getItemAtPosition(position);

		intent.putExtra("name", c.getString(4));
		intent.putExtra("area_id", c.getInt(1));
		if(!c.isNull(2)) intent.putExtra("pref_id", c.getInt(2)); 
		if(!c.isNull(3)) {
			intent.putExtra("city_id", c.getInt(3));
			setResult(RESULT_OK, intent);
			finish();
		} else {
			startActivityForResult(intent, 0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK) {
			setResult(RESULT_OK, data);
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		db.close();
		dbHelper.close();

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "地点情報を更新").setIcon(android.R.drawable.ic_menu_rotate)
		.setIntent(new Intent(this, UpdateForecastMap.class));

		return true;
	}
}
