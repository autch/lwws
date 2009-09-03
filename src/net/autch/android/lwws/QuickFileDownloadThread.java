package net.autch.android.lwws;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class QuickFileDownloadThread extends Thread {
	private static final String TAG = "QuickDLThread";

	private final Context context;
	private Handler handler;
	private final String urlString, filename;
	private Runnable onComplete, onException;
	private Exception lastException;

	private static final String USER_AGENT = "LWWS/1.0 (QuickFileDownloaderThread)";
	private static final int BUFFER_SIZE = 8192;

	QuickFileDownloadThread(Context ctx, String url, String filename) {
		this.context = ctx;
		this.urlString = url;
		this.filename = filename;
		this.handler = null;
		this.onComplete = null;
		this.onException = null;
		this.lastException = null;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setOnComplete(Runnable callback) {
		onComplete = callback;
	}

	public void setOnException(Runnable callback) {
		onException = callback;
	}

	public Exception getLastException() {
		return lastException;
	}

	@Override
	public void run() {
		Log.d(TAG, String.format("URL: '%s' => '%s'" , urlString, filename));
		try {
			URL url = new URL(this.urlString);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Host", url.getHost());
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setReadTimeout(60 * 1000);
			conn.setConnectTimeout(60 * 1000);
			conn.connect();
			try {
				if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream is = conn.getInputStream();
					FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
					try {
						byte[] buffer = new byte[BUFFER_SIZE];
						int bytesRead;
						while((bytesRead = is.read(buffer)) != -1) {
							fos.write(buffer, 0, bytesRead);
						}
						fos.flush();
					} finally {
						fos.close();
						is.close();
					}
				}
			} finally {
				conn.disconnect();
			}
		} catch(Exception e) {
			this.lastException = e;
			if(onException != null) {
				if(handler != null)
					handler.post(onException);
				else
					onException.run();
			}
		} finally {
			if(onComplete != null) {
				if(handler != null)
					handler.post(onComplete);
				else
					onComplete.run();
			}
		}
	}
}
