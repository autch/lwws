package net.autch.android.lwws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.autch.webservice.lwws.Forecast;
import net.autch.webservice.lwws.ForecastParser;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

public class ForecastDetail extends Activity {
	private static final String TAG = "ForecastDetail";
	private static final String URL_FORECAST_V1 = "http://weather.livedoor.com/forecast/webservice/rest/v1";

	private Handler handler;
	private ProgressDialog please_wait;

	private final Runnable onParseComplete = new Runnable() {
		public void run() {
			try {
				FileInputStream in = ForecastDetail.this.openFileInput("4.xml");
				//DBHelper dbHelper = new DBHelper(ForecastDetail.this);
				//SQLiteDatabase db = dbHelper.getWritableDatabase();
				ForecastParser parser = new ForecastParser(); 
				try {
					Forecast detail = parser.parse(in);
					TextView tv;

					tv = (TextView)findViewById(R.id.city_and_day);
					tv.setText(detail.getCity() + " - " + detail.getDay());
					tv = (TextView)findViewById(R.id.prefecture);
					tv.setText(detail.getPref() + " - " + detail.getArea());

					tv = (TextView)findViewById(R.id.telop);
					tv.setText(detail.getTelop());
					//tv = (TextView)findViewById(R.id.icon_telop);
					//tv.setText(detail.getTelop());

					//tv = (TextView)findViewById(R.id.description);
					//tv.setText(detail.getDescription());
					StringBuffer html = new StringBuffer();
					html.append("<html><head>\n");
					html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
					html.append("</head><body>\n");
					html.append(detail.getDescription());
					html.append("\n</body></html>");

					WebView wv = (WebView)findViewById(R.id.description);
					wv.loadDataWithBaseURL("about:blank", html.toString(), "text/html", "utf-8", "about:blank");

					Log.d(TAG, "onParseComplete(): success");
				} finally {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			} catch(XmlPullParserException ioe) {
				Log.d(TAG, ioe.getMessage());
				ioe.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				System.err.println(e);
				e.printStackTrace();
				Log.d(TAG, "onParseComplete(): failed");
				setResult(RESULT_CANCELED);
			} catch(IOException ioe) {
				Log.d(TAG, ioe.getMessage());
				ioe.printStackTrace();
			} finally {
				please_wait.dismiss();
				//				finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forecast);

		Intent it = getIntent();
		Bundle extras = it.getExtras();
		if(extras != null) {
			setTitle(extras.getString("name"));
		}

		Log.d(TAG, "onCreate()");

		handler = new Handler();
		please_wait = ProgressDialog.show(this, null, "地点情報を取得しています...", true, false);

		Runnable updateThread = new Runnable() {
			public void run() {
				File f = ForecastDetail.this.getFileStreamPath("4.xml");
				if(!f.exists() || true) {
					Uri uri = Uri.parse(URL_FORECAST_V1);
					Uri.Builder builder = uri.buildUpon();
					builder.appendQueryParameter("day", "tomorrow");
					builder.appendQueryParameter("city", "4"); // city_id
					Log.d(TAG, "URL: " + builder.toString());

					QuickFileDownloadThread dl = new QuickFileDownloadThread(
							ForecastDetail.this, handler, builder.toString(), "4.xml");
					dl.setOnComplete(onParseComplete);
					dl.start();
				} else {
					handler.post(onParseComplete);
				}
			}
		};
		new Thread(updateThread).start();
	}
}
