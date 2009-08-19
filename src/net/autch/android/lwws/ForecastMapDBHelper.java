package net.autch.android.lwws;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ForecastMapDBHelper extends SQLiteOpenHelper {
	private static final String TAG = "ForecastMapDBHelper";
	public static final String DB_NAME = "forecastmap.db";
	public static final String DB_TABLE = "forecastmap";
	private static final int DB_VERSION = 3;
	private static final String SQL_CREATE_TABLE = 
		  "CREATE TABLE IF NOT EXISTS " + DB_TABLE + " ("
		+ "  _id     INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ "  area_id INT NULL,"
		+ "  pref_id INT NULL,"
		+ "  city_id INT NULL,"
		+ "  name    TEXT NOT NULL"
		+ ")";
	private static final String SQL_CREATE_INDEX = 
		  "CREATE INDEX IF NOT EXISTS ids ON " + DB_TABLE + " ("
		+ "  area_id, pref_id, city_id"
		+ ")";
	private static final String ORDER = "area_id ASC, pref_id ASC, city_id ASC";
	private static final String[] COLUMNS = { "_id", "area_id", "pref_id", "city_id", "name" };
	
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
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
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
	
	public static Cursor query(SQLiteDatabase db, Integer area_id, Integer pref_id, Integer city_id) {
		String selection = "";
		
		selection += " area_id "+ ((area_id == null) ? "IS NULL" : ("= " + area_id.toString()));
		selection += " AND pref_id "+ ((pref_id == null) ? "IS NULL" : ("= " + pref_id.toString()));
		selection += " AND city_id "+ ((city_id == null) ? "IS NULL" : ("= " + city_id.toString()));
		
		Cursor c = db.query(DB_TABLE, COLUMNS, selection, null, null, null, ORDER);	
		if(c != null) c.moveToFirst();
		return c;
	}

	private static Cursor queryAll(SQLiteDatabase db, String sql) {
		return db.query(ForecastMapDBHelper.DB_TABLE, COLUMNS, sql, null, null, null, ORDER);
	}
	
	public static Cursor queryAllArea(SQLiteDatabase db) {
		return queryAll(db, "pref_id IS NULL AND city_id IS NULL");
	}

	public static Cursor queryAllPref(SQLiteDatabase db, int area_id) {
		return queryAll(db, "area_id = " + area_id + " AND pref_id IS NOT NULL AND city_id IS NULL");
	}

	public static Cursor queryAllCity(SQLiteDatabase db, int pref_id) {
		return queryAll(db, "area_id IS NOT NULL AND pref_id = " + pref_id + " AND city_id IS NOT NULL");
	}
}
