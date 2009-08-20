package net.autch.webservice.lwws;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * Livedoor Weather Web Service レスポンス XML のパーサ。
 * 
 *
 */
public class ForecastParser {
	private Forecast forecast;
	private XmlPullParser parser;
	
	public Forecast parse(InputStream in) throws XmlPullParserException, IOException {
		parser = Xml.newPullParser();
		forecast = new Forecast();

		parser.setInput(new InputStreamReader(in));
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();
			switch(eventType) {
			case XmlPullParser.START_TAG:
				if(tagName.equals("area")) {
				}
				break;
			case XmlPullParser.END_TAG:
				if(tagName.equals("source")) {
				}
			}
			eventType = parser.next();
		}

		return forecast;
	}
}
