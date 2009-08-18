package net.autch.android.lwws;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.Handler;

public class QuickFileDownloadThread extends Thread {
	private Context context;
	private Handler handler;
	private String urlString, filename;
	private Runnable onComplete, onException;
	private Exception lastException;
	
	private static final String USER_AGENT = "LWWS/1.0 (QuickFileDownloaderThread)";
	
	QuickFileDownloadThread(Context ctx, Handler handler, String url, String filename) {
		this.context = ctx;
		this.handler = handler;
		this.urlString = url;
		this.filename = filename;
		this.onComplete = null;
		this.onException = null;
		this.lastException = null;
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
		            	byte[] buffer = new byte[1024];
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
        		handler.post(onException);
        	}
        } finally {
        	if(onComplete != null) {
        		handler.post(onComplete);
        	}
        }
	}
}
