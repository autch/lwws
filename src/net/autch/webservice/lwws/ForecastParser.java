package net.autch.webservice.lwws;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/**
 * Livedoor Weather Web Service レスポンス XML のパーサ。
 * 
 *
 */
public class ForecastParser {
	private Forecast forecast;
	private XmlPullParser parser;
	
	public Forecast parse(InputStream in) {
		parser = Xml.newPullParser();
		forecast = new Forecast();

		return forecast;
	}
}
