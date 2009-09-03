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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class ForecastDetail extends Activity {
	private static final String TAG = "ForecastDetail";
	private static final String URL_FORECAST_V1 = "http://weather.livedoor.com/forecast/webservice/rest/v1";

	private final Handler handler = new Handler();
	private ProgressDialog please_wait;
	private Forecast detail;

	public final void setTextViewById(int id, String string) {
		TextView tv = (TextView)findViewById(id);
		tv.setText(string);
	}

	private final Runnable setUIContents = new Runnable() {
		public void run() {
			String forecastday;

			switch(detail.getForecastday()) {
			case Forecast.TODAY:
				forecastday = "きょう";
				break;
			case Forecast.TOMORROW:
				forecastday = "明日";
				break;
			case Forecast.DAY_AFTER_TOMORROW: 
				forecastday = "あさって";
				break;
			default:
				forecastday = "";
			}

			setTextViewById(R.id.city_and_day, detail.getCity() + " - " + forecastday);
			setTextViewById(R.id.prefecture, detail.getPref() + " - " + detail.getArea());
			setTextViewById(R.id.icon_telop, detail.getIcon().getTitle());
			setTextViewById(R.id.temp_max, detail.getTemperature().getMaxC() + "℃");
			setTextViewById(R.id.temp_min, detail.getTemperature().getMinC() + "℃");

			ImageView iv = (ImageView)findViewById(R.id.icon);
			iv.setImageURI(Uri.parse(detail.getIcon().getUrl()));

			//ListView lv = (ListView)findViewById(R.id.pinpoint_list);
			//lv.setAdapter(new SimpleAdapter(ForecastDetail.this, detail.getPinpointAsMap(), android.R.layout.simple_list_item_1, new String[]{ "title" }, new int[]{ android.R.id.text1 }));					

			WebView wv = (WebView)findViewById(R.id.description);
			wv.loadDataWithBaseURL("about:blank", detail.getDescription(), "text/html", "utf-8", "about:blank");

			Log.d(TAG, "onParseComplete(): success");
		}
	};

	private final Runnable onParseComplete = new Runnable() {
		public void run() {
			try {
				FileInputStream in = ForecastDetail.this.openFileInput("4.xml");
				//DBHelper dbHelper = new DBHelper(ForecastDetail.this);
				//SQLiteDatabase db = dbHelper.getWritableDatabase();
				ForecastParser parser = new ForecastParser(); 
				try {
					detail = parser.parse(in);
					final Uri uri = Uri.parse(detail.getIcon().getUrl());

					QuickFileDownloadThread dl = new QuickFileDownloadThread(
							ForecastDetail.this, uri.toString(), uri.getLastPathSegment());
					dl.setHandler(handler);
					dl.setOnComplete(new Runnable() {
						public void run() {
							try {
								FileInputStream is = ForecastDetail.this.openFileInput(uri.getLastPathSegment());
								try {
									ImageView iv = (ImageView)findViewById(R.id.icon);
									iv.setImageDrawable(Drawable.createFromStream(is, uri.getLastPathSegment()));
								} finally {
									try {
										is.close();
									} catch (IOException e) {
									}
								}
							} catch(FileNotFoundException fe) {
								fe.printStackTrace();
							}
						}
					});
					dl.start();

					handler.post(setUIContents);
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

		please_wait = ProgressDialog.show(this, null, "予報を取得しています...", true, false);

		final Runnable updateThread = new Runnable() {
			public void run() {
				File f = ForecastDetail.this.getFileStreamPath("4.xml");
				if(!f.exists() || true) {
					Uri uri = Uri.parse(URL_FORECAST_V1);
					Uri.Builder builder = uri.buildUpon();
					builder.appendQueryParameter("day", "tomorrow");
					builder.appendQueryParameter("city", "4"); // city_id

					QuickFileDownloadThread dl = new QuickFileDownloadThread(
							ForecastDetail.this, builder.toString(), "4.xml");
					dl.setOnComplete(onParseComplete);
					dl.start();
				} else {
					onParseComplete.run();
				}
			}
		};
		new Thread(updateThread).start();
	}
}
