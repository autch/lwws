package net.autch.android.lwws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import net.autch.webservice.lwws.ForecastMapParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SelectCity extends Activity {
	private static final String TAG = "SelectCity";
	private static final String URL_CITIES_RSS = "http://weather.livedoor.com/forecast/rss/forecastmap.xml"; 
	private ProgressDialog please_wait;
	private Handler handler;
	private final Runnable onParseComplete = new Runnable() {
		public void run() {
	        TextView textView = (TextView)findViewById(R.id.helloText);
	        textView.setText("Ok");
			
			try {
				FileInputStream in = SelectCity.this.openFileInput("forecastmap.xml");
				ForecastMapDBHelper helper = new ForecastMapDBHelper(SelectCity.this);
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
				please_wait.dismiss();
			}
			Intent i = new Intent(SelectCity.this, CityPicker.class);
			startActivityForResult(i, 0);
		}
	};
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button b = (Button)findViewById(R.id.btnPickCity);
        b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(SelectCity.this, CityPicker.class);
				startActivityForResult(i, 0);
			}
		});
        
        handler = new Handler();

        please_wait = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        //please_wait.setTitle("LWWS");
        please_wait.setMessage("地点情報を取得しています...");
        please_wait.setIndeterminate(true);
        please_wait.setCancelable(false);
        please_wait.show();
        
        File f = this.getFileStreamPath("forecastmap.xml");
        if(!f.exists()) {
	        QuickFileDownloadThread dl = new QuickFileDownloadThread(this, handler, URL_CITIES_RSS, "forecastmap.xml");
	        dl.setOnComplete(onParseComplete);
	        dl.start();
        } else {
        	onParseComplete.run();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO 自動生成されたメソッド・スタブ
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode == RESULT_OK) {
    		TextView textView = (TextView)findViewById(R.id.helloText);
    		textView.setText(data.getExtras().getString("name"));
    	}
    }
}