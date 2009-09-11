package net.autch.android.lwws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.autch.webservice.lwws.ForecastMapParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class UpdateForecastMap extends Activity {
	public static final String TAG = "UpdateForecastMap";
	private static final String URL_CITIES_RSS = "http://weather.livedoor.com/forecast/rss/forecastmap.xml"; 
	private ProgressDialog please_wait;
	private Handler handler;

	private final Runnable onParseComplete = new Runnable() {
		public void run() {
			try {
				FileInputStream in = UpdateForecastMap.this.openFileInput("forecastmap.xml");
				DBHelper dbHelper = new DBHelper(UpdateForecastMap.this);
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				try {
					ForecastMapParser parser = new ForecastMapParser(db);
					parser.getDefinitionXML(in);
				} finally {
					db.close();
					try {
						in.close();
					} catch (IOException e) {
					}
				}
				Log.d(TAG, "onParseComplete(): success");
				setResult(RESULT_OK);
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				Log.d(TAG, "onParseComplete(): failed");
				setResult(RESULT_CANCELED);
			} finally {
				please_wait.dismiss();
				finish();
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		final boolean force = (extras == null) ? false : extras.getBoolean("force", false);        

		setContentView(R.layout.main);
		Log.d(TAG, "onCreate()");

		handler = new Handler();
		please_wait = ProgressDialog.show(this, null, "地点情報を取得しています...", true, false);

		Runnable updateThread = new Runnable() {
			public void run() {
				File f = UpdateForecastMap.this.getFileStreamPath("forecastmap.xml");
				if(!f.exists() || force) {
					QuickFileDownloadThread dl = new QuickFileDownloadThread(
							UpdateForecastMap.this, URL_CITIES_RSS, "forecastmap.xml");
					//dl.setHandler(handler);
					dl.setOnComplete(onParseComplete);
					dl.start();
				} else {
					//handler.post(onParseComplete);
					onParseComplete.run();
				}
			}
		};
		new Thread(updateThread).start();
	}
}
