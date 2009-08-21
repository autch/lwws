package net.autch.android.lwws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import net.autch.webservice.lwws.ForecastMapParser;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

public class UpdateForecastMapService extends Service {
	private static final String URL_CITIES_RSS = "http://weather.livedoor.com/forecast/rss/forecastmap.xml"; 
	private ProgressDialog please_wait;
	private Handler handler;

	private final Runnable onParseComplete = new Runnable() {
		public void run() {
			try {
				FileInputStream in = UpdateForecastMapService.this.openFileInput("forecastmap.xml");
				ForecastMapDBHelper helper = new ForecastMapDBHelper(UpdateForecastMapService.this);
				SQLiteDatabase db = helper.getWritableDatabase();
				try {
					ForecastMapParser parser = new ForecastMapParser(db);
					parser.getDefinitionXML(in);
				} finally {
					db.close();
				}
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				System.err.println(e);
			} finally {
			}
		}
	};
	 

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO 自動生成されたメソッド・スタブ
		

		super.onStart(intent, startId);
        File f = this.getFileStreamPath("forecastmap.xml");
        if(!f.exists()) {
	        QuickFileDownloadThread dl = new QuickFileDownloadThread(this, handler, URL_CITIES_RSS, "forecastmap.xml");
	        dl.setOnComplete(onParseComplete);
	        dl.start();
        } else {
        	onParseComplete.run();
        }

	}
}
