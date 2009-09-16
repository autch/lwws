package net.autch.android.lwws;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ForecastList extends ListActivity {
	private static final String TAG = "ForecastList";
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private Cursor cursor;

	private static final int MID_ADD_CITY = 0x1001;
	private static final int MID_SHOW_DETAIL = 0x2001;
	private static final int MID_DELETE_CITY = 0x2002;

	private static final int MID_ABOUT_APP = 0x1010;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_list);
		setTitle("予報地点一覧");

		TextView tv = (TextView)findViewById(android.R.id.empty);
		tv.setText("地点が登録されていません。メニューから地点の登録を行ってください。");

		dbHelper = new DBHelper(this);
		db = dbHelper.getWritableDatabase();

		CitiesTableHelper helper = new CitiesTableHelper(db);
		cursor = helper.queryAll();
		if(cursor.getCount() == 0) {
			// cities not registered, navigate to CityPicker
			Intent i = new Intent(this, CityPicker.class);
			startActivityForResult(i, RequestCodes.PICK_CITY);
		}

		startManagingCursor(cursor);

		ListAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, new String[] { "name" }, new int[] { android.R.id.text1 });

		// Bind to our new adapter.
		setListAdapter(adapter);
		setProgressBarVisibility(false);
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MID_ADD_CITY, Menu.NONE, "地点を追加").setIcon(android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, MID_ABOUT_APP, Menu.NONE, "LWWS について");
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo mnuInfo = (AdapterContextMenuInfo)menuInfo;
		Cursor c = (Cursor)getListView().getItemAtPosition(mnuInfo.position);
		final int city_id = c.getInt(3);
		final String name = c.getString(4);

		menu.setHeaderTitle(name);
		menu.add(Menu.NONE, MID_SHOW_DETAIL, Menu.NONE, "詳細を見る").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				Intent it = new Intent(ForecastList.this, ForecastDetailTabs.class);
				it.setData(LwwsUri.buildForForecastDetail(city_id));
				it.putExtra("name", name);
				it.putExtra("city_id", city_id);

				startActivity(it);
				return true;
			}
		});
		menu.add(Menu.NONE, MID_DELETE_CITY, Menu.NONE, "削除");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO 自動生成されたメソッド・スタブ
		super.onListItemClick(l, v, position, id);

		Cursor c = (Cursor)getListView().getItemAtPosition(position);
		int city_id = c.getInt(3);

		Intent it = new Intent(this, ForecastDetailTabs.class);
		it.setData(LwwsUri.buildForForecastDetail(city_id));
		it.putExtra("name", c.getString(4));
		it.putExtra("city_id", city_id);

		startActivity(it);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case MID_ADD_CITY:
			Intent it = new Intent(ForecastList.this, CityPicker.class);
			startActivityForResult(it, RequestCodes.PICK_CITY);
			break;
		default:
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RequestCodes.PICK_CITY
				&& resultCode == RESULT_OK) {
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
