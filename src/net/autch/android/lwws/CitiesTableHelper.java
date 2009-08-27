package net.autch.android.lwws;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CitiesTableHelper {
	private static final String TAG = "CitiesMapDBHelper";
	public static final String DB_TABLE = "cities";
	private static final String ORDER = "city_id ASC";
	private static final String[] COLUMNS = { "_id", "city_id" };
	
	private final SQLiteDatabase sqlite;
	
	public CitiesTableHelper(SQLiteDatabase db) {
		sqlite = db;
	}
	
	public long insertCity(int city_id) {
		ContentValues values = new ContentValues();
		values.put("city_id", city_id);
		long rows = sqlite.update(DB_TABLE, values, "city_id = " + city_id, null);
		if(rows == 0)
			rows = sqlite.insert(DB_TABLE, "", values);
		return rows;
	}

	public Cursor queryAll() {
		return sqlite.rawQuery("SELECT c._id _id, area_id, pref_id, c.city_id, name FROM cities c LEFT JOIN forecastmap f ON c.city_id = f.city_id", null);
	}

}
