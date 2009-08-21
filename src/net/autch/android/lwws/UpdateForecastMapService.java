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
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

public class UpdateForecastMapService extends Service {
	private static final String TAG = "UpdateForecastMapService";
	private static final String URL_CITIES_RSS = "http://weather.livedoor.com/forecast/rss/forecastmap.xml"; 
	private ProgressDialog please_wait;
	private Handler handler;
	private static int SUCCESS = 1;
	private static int FAILURE = 2;

	private RemoteCallbackList<IUpdateForecastMapCallbackListener> listeners =
		new RemoteCallbackList<IUpdateForecastMapCallbackListener>();
	private final IUpdateForecastMapService stub = new IUpdateForecastMapService.Stub() {
		public void removeListener(IUpdateForecastMapCallbackListener listener)
				throws RemoteException {
			listeners.register(listener);
		}
		
		public void addListener(IUpdateForecastMapCallbackListener listener)
				throws RemoteException {
			listeners.unregister(listener);
		}
	};
	
	
	private final Handler callbackHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if(msg.what == SUCCESS) {
				int numListeners = listeners.beginBroadcast();
				try {
					for(int i = 0; i < numListeners; i++) {
						try {
							// fire callback
							listeners.getBroadcastItem(i).receiveMessage("");
						} catch(RemoteException e) {
							Log.e(TAG, e.getMessage(), e);
						}
					}
				} finally {
					listeners.finishBroadcast();
				}
			} else {
				super.dispatchMessage(msg);
			}
		}
	};
	
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
	public void onCreate() {
		Log.d(TAG, "onCreate()");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		File f = this.getFileStreamPath("forecastmap.xml");
        if(!f.exists()) {
	        QuickFileDownloadThread dl = new QuickFileDownloadThread(this, handler, URL_CITIES_RSS, "forecastmap.xml");
	        dl.setOnComplete(onParseComplete);
	        dl.start();
        } else {
        	onParseComplete.run();
        	callbackHandler.sendEmptyMessage(SUCCESS);
        }

	}
}
