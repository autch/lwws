package net.autch.android.lwws;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "DBHelper";

	public static final String DB_NAME = "lwws.db";
	private static final int DB_VERSION = 1;

	private static final String[] TABLES = { "cities", "forecastmap" };
	private static final String[] INDICES = { "cities_idx", "forecastmap_idx" };
	private static final String[] SQL_CREATE_TABLE = {
		  "CREATE TABLE IF NOT EXISTS cities ("
		+ "  _id     INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ "  city_id INT NOT NULL"
		+ ")",
		  "CREATE TABLE IF NOT EXISTS forecastmap ("
		+ "  _id     INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ "  area_id INT NULL,"
		+ "  pref_id INT NULL,"
		+ "  city_id INT NULL,"
		+ "  name    TEXT NOT NULL"
		+ ")"
	};
	private static final String[] SQL_CREATE_INDEX = { 
		  "CREATE INDEX IF NOT EXISTS cities_idx ON cities ("
		+ "  city_id"
		+ ")",
		  "CREATE INDEX IF NOT EXISTS forecastmap_idx ON forecastmap ("
		+ "  area_id, pref_id, city_id"
		+ ")"
	};

	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
			for(String sql : SQL_CREATE_TABLE) {
				db.execSQL(sql);
			}
			for(String sql : SQL_CREATE_INDEX) {
				db.execSQL(sql);
			}
			db.setTransactionSuccessful();
        } catch(Exception e) {
        	e.printStackTrace();
        	//throw e;
        } finally {
        	db.endTransaction();
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", without preserving old data");
        db.beginTransaction();
        try {
			for(String idx : INDICES) {
				db.execSQL("DROP INDEX IF EXISTS " + idx);
			}
			for(String tbl : TABLES) {
				db.execSQL("DROP TABLE IF EXISTS " + tbl);
			}
			onCreate(db);
			db.setTransactionSuccessful();
        } catch(Exception e) {
        	e.printStackTrace();
        	//throw e;
        } finally {
        	db.endTransaction();
        }
	}	
}
