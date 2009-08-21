package net.autch.webservice.lwws;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		
		forecast = parseLwws(parser, forecast);

		return forecast;
	}
	
	private Forecast parseLwws(XmlPullParser parser, Forecast forecast)
		throws XmlPullParserException, IOException {

		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagName = "";
			switch(eventType) {
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if(tagName.equals("location")) {
					forecast.setArea(parser.getAttributeValue(null, "area"));
					forecast.setPref(parser.getAttributeValue(null, "pref"));
					forecast.setArea(parser.getAttributeValue(null, "city"));
				}
				break;
			case XmlPullParser.TEXT:
				if(tagName.equals("author")) {
					forecast.setAuthor(parser.getText());
				}
				if(tagName.equals("title")) {
					forecast.setTitle(parser.getText());
				}
				if(tagName.equals("forecastday")) {
					String day = parser.getText();
					if(day.equals("today")) {
						forecast.setForecastday(Forecast.TODAY);
					}
					if(day.equals("tomorrow")) {
						forecast.setForecastday(Forecast.TOMORROW);
					}
					if(day.equals("dayaftertomorrow")) {
						forecast.setForecastday(Forecast.DAY_AFTER_TOMORROW);
					}
				}
				if(tagName.equals("forecastdate")) {
					SimpleDateFormat df = new SimpleDateFormat();
					try {
						Date date = df.parse(parser.getText());
						forecast.setForecastdate(date);
					} catch (ParseException e) {
						forecast.setForecastdate(null);
					}
				}
				if(tagName.equals("publictime")) {
					SimpleDateFormat df = new SimpleDateFormat();
					try {
						Date date = df.parse(parser.getText());
						forecast.setPublictime(date);
					} catch (ParseException e) {
						forecast.setPublictime(null);
					}
				}
				if(tagName.equals("day")) {
					forecast.setDay(parser.getText());
				}
				if(tagName.equals("link")) {
					forecast.setLink(parser.getText());
				}
				if(tagName.equals("telop")) {
					forecast.setTelop(parser.getText());
				}
				if(tagName.equals("telop")) {
					forecast.setTelop(parser.getText());
				}
				if(tagName.equals("description")) {
					forecast.setDescription(parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = "";
				break;
			}
			eventType = parser.next();
		}

		return forecast;
	}
}
