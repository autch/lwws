package net.autch.android.lwws;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ForecastMapDBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "forecastmap.db";
	private static final String DB_TABLE = "forecastmap";
	private static final int DB_VERSION = 1;
	private static final String SQL_CREATE_TABLE = 
		  "CREATE TABLE IF NOT EXISTS " + DB_TABLE + " ("
		+ "  area_id INT NULL,"
		+ "  pref_id INT NULL,"
		+ "  city_id INT NULL,"
		+ "  name    TEXT NOT NULL"
		+ ")";
	private static final String SQL_CREATE_INDEX = 
		  "CREATE INDEX IF NOT EXISTS ids ON " + DB_TABLE + " ("
		+ "  area_id, pref_id, city_id"
		+ ")";
	
	public ForecastMapDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
		db.execSQL(SQL_CREATE_INDEX);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
		db.execSQL("DROP INDEX IF EXISTS ids");
		onCreate(db);
	}

	public static void insertCity(SQLiteDatabase db,
			int area_id, int pref_id, int city_id, String name) {
		ContentValues values = new ContentValues();
		values.put("area_id", area_id);
		values.put("pref_id", pref_id);
		values.put("city_id", city_id);
		values.put("name",    name);
		int rows = db.update(DB_TABLE, values, 
				"area_id = " + area_id + " AND pref_id = " + pref_id + " AND city_id = " + city_id, null);
		if(rows == 0)
			db.insert(DB_TABLE, "", values);
	}

	public static void insertPrefecture(SQLiteDatabase db,
			int area_id, int pref_id, String name) {
		ContentValues values = new ContentValues();
		values.put("area_id", area_id);
		values.put("pref_id", pref_id);
		values.putNull("city_id");
		values.put("name",    name);
		int rows = db.update(DB_TABLE, values, 
				"area_id = " + area_id + " AND pref_id = " + pref_id + " AND city_id IS NULL", null);
		if(rows == 0)
			db.insert(DB_TABLE, "", values);
	}

	public static void insertArea(SQLiteDatabase db,
			int area_id, String name) {
		ContentValues values = new ContentValues();
		values.put("area_id", area_id);
		values.putNull("pref_id");
		values.putNull("city_id");
		values.put("name",    name);
		int rows = db.update(DB_TABLE, values, 
				"area_id = " + area_id + " AND pref_id IS NULL AND city_id IS NULL", null);
		if(rows == 0)
			db.insert(DB_TABLE, "", values);
	}
}
