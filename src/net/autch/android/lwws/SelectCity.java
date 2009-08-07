package net.autch.android.lwws;

import java.io.IOException;

import net.autch.webservice.lwws.CityDefinitionParser;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
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
			please_wait.dismiss();
		}
	};
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        handler = new Handler();

        please_wait = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        please_wait.setTitle("JWWS");
        please_wait.setMessage("地点情報を取得しています...");
        please_wait.setIndeterminate(true);
        please_wait.setCancelable(false);
        please_wait.show();
        new Thread() {
        	public void run() {
                TextView textView = (TextView)findViewById(R.id.helloText);
                CityDefinitionParser parser = new CityDefinitionParser();
                QnDHttpClient client = new QnDHttpClient();
                try {
        	        if(client.get(URL_CITIES_RSS) == HttpStatus.SC_OK) {
        	        	parser.getDefinitionXML(client.getBody());
        	        }
                } catch(IOException e) {
                	textView.setText(e.getMessage());
                } finally {
                	handler.post(onParseComplete);
                }
        	}
        }.start();
    }
}