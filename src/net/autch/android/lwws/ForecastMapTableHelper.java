package net.autch.android.lwws;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ForecastMapTableHelper {
	private static final String TAG = "ForecastMapTableHelper";
	public static final String DB_TABLE = "forecastmap";
	private static final String ORDER = "area_id ASC, pref_id ASC, city_id ASC";
	private static final String[] COLUMNS = { "_id", "area_id", "pref_id", "city_id", "name" };
	
	private final SQLiteDatabase sqlite;
	
	public ForecastMapTableHelper(SQLiteDatabase db) {
		sqlite = db;
	}

	public long insertCity(int area_id, int pref_id, int city_id, String name) {
		ContentValues values = new ContentValues();
		values.put("area_id", area_id);
		values.put("pref_id", pref_id);
		values.put("city_id", city_id);
		values.put("name",    name);
		long rows = sqlite.update(DB_TABLE, values, 
				"area_id = " + area_id + " AND pref_id = " + pref_id + " AND city_id = " + city_id, null);
		if(rows == 0)
			rows = sqlite.insert(DB_TABLE, "", values);
		return rows;
	}

	public long insertPrefecture(int area_id, int pref_id, String name) {
		ContentValues values = new ContentValues();
		values.put("area_id", area_id);
		values.put("pref_id", pref_id);
		values.putNull("city_id");
		values.put("name",    name);
		long rows = sqlite.update(DB_TABLE, values, 
				"area_id = " + area_id + " AND pref_id = " + pref_id + " AND city_id IS NULL", null);
		if(rows == 0)
			rows = sqlite.insert(DB_TABLE, "", values);
		return rows;
	}

	public long insertArea(int area_id, String name) {
		ContentValues values = new ContentValues();
		values.put("area_id", area_id);
		values.putNull("pref_id");
		values.putNull("city_id");
		values.put("name",    name);
		long rows = sqlite.update(DB_TABLE, values, 
				"area_id = " + area_id + " AND pref_id IS NULL AND city_id IS NULL", null);
		if(rows == 0)
			rows = sqlite.insert(DB_TABLE, "", values);
		return rows;
	}
	
	private Cursor queryAll(String sql) {
		return sqlite.query(DB_TABLE, COLUMNS, sql, null, null, null, ORDER);
	}
	
	public Cursor queryAllArea() {
		return queryAll("pref_id IS NULL AND city_id IS NULL");
	}

	public Cursor queryAllPref(int area_id) {
		return queryAll("area_id = " + area_id + " AND pref_id IS NOT NULL AND city_id IS NULL");
	}

	public Cursor queryAllCity(int pref_id) {
		return queryAll("area_id IS NOT NULL AND pref_id = " + pref_id + " AND city_id IS NOT NULL");
	}
}
