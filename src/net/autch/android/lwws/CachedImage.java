package net.autch.android.lwws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;


/**
 * ネットワークからダウンロードされ、ファイルとしてキャッシュされる画像ファイルを管理する。
 */
public class CachedImage {
	private static final String TAG = "CachedImage";
	private long cacheLife; // in milliseconds
	private final Context context;
	private final String prefix, filename;
	private Handler handler;
	private final Uri uri;
	private QuickFileDownloadThread thread;
	private Runnable onImageAvail;

	public CachedImage(Context context, String prefix, String url, String filename) {
		this.context = context;
		this.handler = null;
		this.prefix = prefix;
		this.filename = filename;
		this.uri = Uri.parse(url);
		this.onImageAvail = null;
		this.cacheLife = -1;
	}

	private void checkCache() {
		File f = getFile();
		if(f.exists() && checkCacheAge(f)) {
			if(handler != null) {
				handler.post(onImageAvail);
			} else {
				onImageAvail.run();
			}
		} else {
			download();
		}
	}

	public void start() {
		checkCache();
	}

	/**
	 * 
	 * @param f File to check its life, -1 to avoid timestamp checking, always return true.
	 * @return true if cache is okay to use, false otherwise
	 */
	private boolean checkCacheAge(File f) {
		if(cacheLife == -1)
			return true;

		return (System.currentTimeMillis() - f.lastModified()) < cacheLife;
	}

	private void download() {
		thread = new QuickFileDownloadThread(context, uri.toString(), getCacheFilename());
		thread.setHandler(handler);
		thread.setOnComplete(onImageAvail);
		thread.start();
	}

	public File getFile() {
		return context.getFileStreamPath(getCacheFilename());
	}

	public FileInputStream openFileInput() throws FileNotFoundException {
		return context.openFileInput(getCacheFilename());
	}

	private String getCacheFilename() {
		String basename;

		if(this.filename == null || this.filename.length() == 0){
			basename = uri.getLastPathSegment();
		} else {
			basename = filename;
		}

		if(prefix == null || prefix.length() == 0) {
			return String.format("%s", basename);
		} else {
			return String.format("%s_%s", prefix, basename);
		}
	}

	public void setOnImageAvail(Runnable onImageAvail) {
		this.onImageAvail = onImageAvail;
	}

	public void setCacheLife(long milliSeconds) {
		this.cacheLife = milliSeconds;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}