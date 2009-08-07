package net.autch.android.lwws;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.net.Uri;

// reference: http://www.adakoda.com/adakoda/2009/01/android-httpbasichttpparamshttpprotocolparams.html
// reference: http://www.adakoda.com/adakoda/2009/01/android-httpschemeregistryschemeplainsocketfactory.html
// reference: http://www.adakoda.com/adakoda/2009/01/android-httpdefaulthttpclienthttphosthttpgethttpresponsehttpentity.html
public class QnDHttpClient {
	private InputStream bodyStream;
	
	QnDHttpClient() {
		
	}
	
	int get(String url) throws ClientProtocolException, IOException {
		final Uri uri = Uri.parse(url);
		int port = uri.getPort() == -1 ? 80 : uri.getPort();

		final HttpParams httpParams = new BasicHttpParams();

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

		final SchemeRegistry schemeRegistryRegistry = new SchemeRegistry();

		schemeRegistryRegistry.register(
		    new Scheme(uri.getScheme(), PlainSocketFactory.getSocketFactory(), port));

		final HttpClient httpClient = new DefaultHttpClient(
			    new ThreadSafeClientConnManager(httpParams, schemeRegistryRegistry),
			    httpParams);

		HttpEntity httpEntity = null;

		final HttpResponse httpResponse = httpClient.execute(
		    new HttpHost(uri.getHost(), port),
		    new HttpGet(uri.getPath()));

		int status = httpResponse.getStatusLine().getStatusCode();
		
		if (status == HttpStatus.SC_OK) {
		    httpEntity = httpResponse.getEntity();
		    bodyStream = httpEntity.getContent();
		}
		
		return status;
	}
	
	InputStream getBody() {
		return bodyStream;
	}
}
