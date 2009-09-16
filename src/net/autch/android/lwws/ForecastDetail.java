package net.autch.android.lwws;

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
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ForecastDetail extends Activity {
	private static final String TAG = "ForecastDetail";

	private static final int MID_PINPOINT = 0x1001;
	private static final int MID_ABOUT_PROVIDERS = 0x1011;

	private final Handler handler = new Handler();
	private CachedImage xmlfile;
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

			switch (detail.forecastday) {
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

			GregorianCalendar cal = new GregorianCalendar(
					TimeZone.getDefault(), Locale.getDefault());
			cal.setTime(detail.forecastdate);
			setTextViewById(R.id.city_and_day, detail.city + " - "
					+ forecastday + ": "
					+ cal.get(GregorianCalendar.DAY_OF_MONTH) + "日");
			setTextViewById(R.id.prefecture, detail.pref + " - " + detail.area);
			setTextViewById(R.id.icon_telop, detail.icon.title);
			if (!Double.isNaN(detail.temperature.max_c)) {
				setTextViewById(R.id.temp_max, detail.temperature.max_c + "℃");
			} else {
				setTextViewById(R.id.temp_max, "--.-℃");
			}
			if (!Double.isNaN(detail.temperature.min_c)) {
				setTextViewById(R.id.temp_min, detail.temperature.min_c + "℃");
			} else {
				setTextViewById(R.id.temp_min, "--.-℃");
			}

			final ImageView iv = (ImageView) findViewById(R.id.icon);
			final CachedImage icon = new CachedImage(ForecastDetail.this,
					"icon", detail.icon.url, null);
			icon.setHandler(handler);
			icon.setOnImageAvail(new Runnable() {
				public void run() {
					Drawable image;
					try {
						FileInputStream is = icon.openFileInput();
						try {
							image = Drawable.createFromStream(is,
									detail.icon.title);
							iv.setImageDrawable(image);
							iv.setMinimumWidth(image.getIntrinsicWidth() * 2);
							iv.setMinimumHeight(image.getIntrinsicHeight() * 2);
						} finally {
							is.close();
						}
					} catch (FileNotFoundException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					} catch (IOException ioe) {
						// TODO 自動生成された catch ブロック
						ioe.printStackTrace();
					}
				}
			});

			icon.start();

			// ListView lv = (ListView)findViewById(R.id.pinpoint_list);
			// lv.setAdapter(new SimpleAdapter(ForecastDetail.this,
			// detail.getPinpointAsMap(), android.R.layout.simple_list_item_1,
			// new String[]{ "title" }, new int[]{ android.R.id.text1 }));

			WebView wv = (WebView) findViewById(R.id.description);
			wv.loadDataWithBaseURL("about:blank", detail.description,
					"text/html", "utf-8", "about:blank");

			Log.d(TAG, "onParseComplete(): success");
		}
	};

	private final Runnable onParseComplete = new Runnable() {
		public void run() {
			try {
				FileInputStream in = xmlfile.openFileInput();
				// DBHelper dbHelper = new DBHelper(ForecastDetail.this);
				// SQLiteDatabase db = dbHelper.getWritableDatabase();
				ForecastParser parser = new ForecastParser();
				try {
					detail = parser.parse(in);
					handler.post(setUIContents);
				} finally {
					in.close();
				}
			} catch (XmlPullParserException ioe) {
				Log.d(TAG, ioe.getMessage());
				ioe.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
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
		setContentView(R.layout.wait);
		setTextViewById(R.id.text_wait, "予報を取得しています...");

		int city_id = -1;
		int forecastday = Forecast.TOMORROW;

		Intent it = getIntent();
		setTitle(it.getStringExtra("name"));
		city_id = it.getIntExtra("city_id", -1);
		forecastday = it.getIntExtra("forecastday", Forecast.TOMORROW);

		request = new Request(city_id, forecastday);
		filename = String.format("%03d_%d.xml", city_id, forecastday);

		Log.d(TAG, "onCreate()");

		final Runnable updateThread = new Runnable() {
			public void run() {
				xmlfile = new CachedImage(ForecastDetail.this, "forecast",
						request.toString(), filename);
				xmlfile.setHandler(null);
				xmlfile.setCacheLife(60 * 60 * 1000); // an hour
				xmlfile.setOnImageAvail(onParseComplete);
				xmlfile.start();
			}
		};
		new Thread(updateThread).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MID_PINPOINT, Menu.NONE, "ピンポイント予報");
		menu.add(Menu.NONE, MID_ABOUT_PROVIDERS, Menu.NONE, "情報提供者について");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AlertDialog.Builder builder;

		switch (item.getItemId()) {
		case MID_PINPOINT:
			SimpleAdapter adapter = new SimpleAdapter(this, detail
					.getPinpointAsMap(), android.R.layout.select_dialog_item,
					new String[] { PinpointLocation.KEY_TITLE },
					new int[] { android.R.id.text1 });

			builder = new AlertDialog.Builder(this);
			builder.setTitle("ピンポイント予報を見る");
			builder.setCancelable(true);

			builder.setSingleChoiceItems(adapter, -1, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					PinpointLocation location = detail.pinpoint.get(which);
					Intent it = new Intent("android.intent.action.VIEW", Uri
							.parse(location.link));
					startActivity(it);
				}
			});
			builder.create();
			builder.show();
			break;
		case MID_ABOUT_PROVIDERS:
			Uri uri = Uri.parse(detail.copyright.banner.url);

			final CachedImage icon = new CachedImage(this, "copyright", uri.toString(), uri.getLastPathSegment());
			icon.setHandler(handler);
			icon.setOnImageAvail(new Runnable() {
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(ForecastDetail.this);

					builder.setTitle(detail.copyright.banner.title);
					builder.setIcon(Drawable.createFromPath(icon.getFile().toString()));
					builder.setMessage(detail.copyright.title);

					final SimpleAdapter adapter = new SimpleAdapter(ForecastDetail.this, detail.copyright.getProvidersAsMap(),
							android.R.layout.simple_list_item_1, new String[] { "name" }, new int[] { android.R.id.text1 });

					LinearLayout ll = new LinearLayout(ForecastDetail.this);
					ll.setOrientation(LinearLayout.VERTICAL);
					ListView lv = new ListView(ForecastDetail.this);
					lv.setAdapter(adapter);
					lv.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Intent it = new Intent("android.intent.action.VIEW", Uri
									.parse(detail.copyright.providers.get(position).link));
							startActivity(it);
						}
					});
					ll.addView(lv);

					builder.setView(ll);

					builder.create();
					builder.show();
				}
			});
			icon.start();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
