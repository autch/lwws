package net.autch.android.lwws;

import java.io.File;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class ForecastList extends ListActivity {
	private static final String TAG = "ForecastList";
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private Cursor cursor;

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
		menu.add(0, 0, 0, "地点を追加").setIcon(android.R.drawable.ic_menu_add)
			.setIntent(new Intent(this, CityPicker.class));
		
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);
//		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自動生成されたメソッド・スタブ
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		db.close();
		dbHelper.close();
		
		super.onDestroy();
	}
}
