package net.autch.android.lwws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import net.autch.webservice.lwws.Forecast;
import net.autch.webservice.lwws.ForecastParser;
import net.autch.webservice.lwws.Forecast.PinpointLocation;
import net.autch.webservice.lwws.ForecastService.Request;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ForecastDetail extends Activity {
	private static final String TAG = "ForecastDetail";
	private static final String URL_FORECAST_V1 = "http://weather.livedoor.com/forecast/webservice/rest/v1";

	private final Handler handler = new Handler();
	private Forecast detail;
	private Request request;
	private String filename;

	public final void setTextViewById(int id, String string) {
		TextView tv = (TextView) findViewById(id);
		tv.setText(string);
	}

	private final Runnable setUIContents = new Runnable() {
		public void run() {
			String forecastday;

			setContentView(R.layout.forecast);

			switch (detail.getForecastday()) {
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

			GregorianCalendar cal = new GregorianCalendar(TimeZone.getDefault(), Locale.getDefault());
			cal.setTime(detail.getForecastdate());
			setTextViewById(R.id.city_and_day, detail.getCity() + " - "
					+ forecastday + ": " + cal.get(GregorianCalendar.DAY_OF_MONTH) + "日");
			setTextViewById(R.id.prefecture, detail.getPref() + " - "
					+ detail.getArea());
			setTextViewById(R.id.icon_telop, detail.getIcon().getTitle());
			if (!Double.isNaN(detail.getTemperature().getMaxC())) {
				setTextViewById(R.id.temp_max, detail.getTemperature()
						.getMaxC()
						+ "℃");
			} else {
				setTextViewById(R.id.temp_max, "--.-℃");
			}
			if (!Double.isNaN(detail.getTemperature().getMinC())) {
				setTextViewById(R.id.temp_min, detail.getTemperature()
						.getMinC()
						+ "℃");
			} else {
				setTextViewById(R.id.temp_min, "--.-℃");
			}

			ImageView iv = (ImageView) findViewById(R.id.icon);
			iv.setImageURI(Uri.parse(detail.getIcon().getUrl()));

			// ListView lv = (ListView)findViewById(R.id.pinpoint_list);
			// lv.setAdapter(new SimpleAdapter(ForecastDetail.this,
			// detail.getPinpointAsMap(), android.R.layout.simple_list_item_1,
			// new String[]{ "title" }, new int[]{ android.R.id.text1 }));

			WebView wv = (WebView) findViewById(R.id.description);
			wv.loadDataWithBaseURL("about:blank", detail.getDescription(),
					"text/html", "utf-8", "about:blank");

			Log.d(TAG, "onParseComplete(): success");
		}
	};

	private final Runnable onParseComplete = new Runnable() {
		public void run() {
			try {
				FileInputStream in = ForecastDetail.this
				.openFileInput(filename);
				// DBHelper dbHelper = new DBHelper(ForecastDetail.this);
				// SQLiteDatabase db = dbHelper.getWritableDatabase();
				ForecastParser parser = new ForecastParser();
				try {
					detail = parser.parse(in);
					final Uri uri = Uri.parse(detail.getIcon().getUrl());

					QuickFileDownloadThread dl = new QuickFileDownloadThread(
							ForecastDetail.this, uri.toString(), uri
							.getLastPathSegment());
					dl.setHandler(handler);
					dl.setOnComplete(new Runnable() {
						public void run() {
							try {
								FileInputStream is = ForecastDetail.this
								.openFileInput(uri.getLastPathSegment());
								try {
									ImageView iv = (ImageView) findViewById(R.id.icon);
									Drawable image = Drawable.createFromStream(is, uri
											.getLastPathSegment());
									iv.setImageDrawable(image);
									iv.setMinimumWidth(image.getIntrinsicWidth() * 2);
									iv.setMinimumHeight(image.getIntrinsicHeight() * 2);
								} finally {
									try {
										is.close();
									} catch (IOException e) {
									}
								}
							} catch (FileNotFoundException fe) {
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
			} catch (XmlPullParserException ioe) {
				Log.d(TAG, ioe.getMessage());
				ioe.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				System.err.println(e);
				e.printStackTrace();
				Log.d(TAG, "onParseComplete(): failed");
				setResult(RESULT_CANCELED);
			} catch (IOException ioe) {
				Log.d(TAG, ioe.getMessage());
				ioe.printStackTrace();
			} finally {
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.forecast);
		setContentView(R.layout.wait);
		setTextViewById(R.id.text_wait, "予報を取得しています...");

		int city_id = -1;
		int forecastday = Forecast.TOMORROW;

		Intent it = getIntent();
		Bundle extras = it.getExtras();
		setTitle(it.getStringExtra("name"));
		city_id = it.getIntExtra("city_id", -1);
		forecastday = it.getIntExtra("forecastday", Forecast.TOMORROW);

		request = new Request(city_id, forecastday);
		filename = String.format("forecast_%03d_%d.xml", city_id, forecastday);

		Log.d(TAG, "onCreate()");

		final Runnable updateThread = new Runnable() {
			public void run() {
				File f = ForecastDetail.this.getFileStreamPath(filename);
				if (!f.exists() || true) {
					QuickFileDownloadThread dl = new QuickFileDownloadThread(
							ForecastDetail.this, request.toString(), filename);
					dl.setOnComplete(onParseComplete);
					dl.start();
				} else {
					onParseComplete.run();
				}
			}
		};
		new Thread(updateThread).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add(Menu.NONE, MID_ADD_CITY, Menu.NONE,
		// "地点を追加").setIcon(android.R.drawable.ic_menu_add);
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 1, Menu.NONE, "ピンポイント予報");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO 自動生成されたメソッド・スタブ
		super.onMenuItemSelected(featureId, item);

		if (item.getItemId() == 1) {
			SimpleAdapter adapter = new SimpleAdapter(this, detail
					.getPinpointAsMap(), android.R.layout.select_dialog_item,
					new String[] { PinpointLocation.KEY_TITLE },
					new int[] { android.R.id.text1 });

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("ピンポイント予報を見る");
			builder.setCancelable(true);

			builder.setSingleChoiceItems(adapter, -1, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					PinpointLocation location = detail.getPinpoint().get(which);
					Intent it = new Intent("android.intent.action.VIEW",
							Uri.parse(location.getLink()));
					startActivity(it);
				}
			});
			builder.create();
			builder.show();
		}
		return true;
	}
}
