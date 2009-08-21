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

        /*
        handler = new Handler();

        please_wait = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        //please_wait.setTitle("LWWS");
        please_wait.setMessage("地点情報を取得しています...");
        please_wait.setIndeterminate(true);
        please_wait.setCancelable(false);
        please_wait.show();
        */
    }
}