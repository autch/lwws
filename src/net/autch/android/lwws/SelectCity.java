package net.autch.android.lwws;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SelectCity extends Activity {
	private static final String TAG = "SelectCity";
	
	private IUpdateForecastMapCallbackListener listener = new IUpdateForecastMapCallbackListener.Stub() {
		public void receiveMessage(String message) throws RemoteException {
			// UpdateForecastMapService done.
			// use message to work
		}
	};
	
	private ServiceConnection conn = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
		}
		
		public void onServiceConnected(ComponentName name, IBinder binder) {
			IUpdateForecastMapService service = IUpdateForecastMapService.Stub.asInterface(binder);
			try {
				service.addListener(listener);
				
			} catch(RemoteException e) {
				Log.e(TAG, e.getMessage(), e);
			}
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