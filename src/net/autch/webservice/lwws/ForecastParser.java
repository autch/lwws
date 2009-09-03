package net.autch.webservice.lwws;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.autch.webservice.lwws.Forecast.Copyright;
import net.autch.webservice.lwws.Forecast.Image;
import net.autch.webservice.lwws.Forecast.PinpointLocation;
import net.autch.webservice.lwws.Forecast.Temperature;
import net.autch.webservice.lwws.Forecast.Copyright.Provider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * Livedoor Weather Web Service レスポンス XML のパーサ。
 * 
 *
 */
public class ForecastParser {
	private static final String TAG = "ForecastParser";

	public Forecast parse(InputStream in) throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser(); 
		Forecast forecast = new Forecast();

		parser.setInput(new InputStreamReader(in));

		forecast = parseLwws(parser, forecast);

		return forecast;
	}

	private Forecast parseLwws(XmlPullParser parser, Forecast forecast)
	throws XmlPullParserException, IOException {
		String tagName = "";
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch(eventType) {
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if(tagName.equals("location")) {
					forecast.setArea(parser.getAttributeValue(null, "area"));
					forecast.setPref(parser.getAttributeValue(null, "pref"));
					forecast.setCity(parser.getAttributeValue(null, "city"));
				}
				if(tagName.equals("pinpoint")){
					forecast = parsePinpoint(parser, forecast);
				}
				if(tagName.equals("image")) {
					parseImage(parser, forecast.getIcon());
				}
				if(tagName.equals("temperature")) {
					parseTemperature(parser, forecast.getTemperature());
				}
				if(tagName.equals("copyright")) {
					parseCopyright(parser, forecast.getCopyright());
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

	private Forecast parsePinpoint(XmlPullParser parser, Forecast forecast)
	throws XmlPullParserException, IOException {
		String tagName = "";
		int eventType = parser.next();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch(eventType) {
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if(tagName.equals("location")) {
					PinpointLocation location = forecast.new PinpointLocation();
					if(parsePinpointLocation(parser, location))
						forecast.addPinpoint(location);
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if(tagName.equals("pinpoint")){
					return forecast;
				}
				tagName = "";
				break;
			}
			eventType = parser.next();
		}
		return forecast;
	}

	private boolean parsePinpointLocation(XmlPullParser parser, PinpointLocation location)
	throws XmlPullParserException, IOException {
		String tagName = "";
		int eventType = parser.next();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch(eventType) {
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				break;
			case XmlPullParser.TEXT:
				if(tagName.equals("title")) {
					location.setTitle(parser.getText());
				}
				if(tagName.equals("link")) {
					location.setLink(parser.getText());
				}
				if(tagName.equals("publictime")) {
					SimpleDateFormat df = new SimpleDateFormat();
					try {
						Date date = df.parse(parser.getText());
						location.setPublictime(date);
					} catch (ParseException e) {
						location.setPublictime(null);
					}
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if(tagName.equals("location")){
					return true;
				}
				tagName = "";
				break;
			}
			eventType = parser.next();
		}
		return false;
	}

	private boolean parseImage(XmlPullParser parser, Image image)
	throws XmlPullParserException, IOException {
		String tagName = "";
		int eventType = parser.next();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch(eventType) {
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				break;
			case XmlPullParser.TEXT:
				if(tagName.equals("title")) {
					image.setTitle(parser.getText());
				}
				if(tagName.equals("link")) {
					image.setLink(parser.getText());
				}
				if(tagName.equals("url")) {
					image.setUrl(parser.getText());
				}
				if(tagName.equals("width")) {
					image.setWidth(parseIntSafely(parser.getText()));
				}
				if(tagName.equals("height")) {
					image.setHeight(parseIntSafely(parser.getText()));
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if(tagName.equals("image")){
					return true;
				}
				tagName = "";
				break;
			}
			eventType = parser.next();
		}
		return false;
	}

	private boolean parseTemperature(XmlPullParser parser, Temperature temp)
	throws XmlPullParserException, IOException {
		final int MAX = 1;
		final int MIN = 2;

		String tagName = "";
		int which = 0;
		int eventType = parser.next();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch(eventType) {
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if(tagName.equals("max")) {
					which = MAX;
				}
				if(tagName.equals("min")) {
					which = MIN;
				}
				break;
			case XmlPullParser.TEXT:
				if(tagName.equals("celsius")) {
					if(which == MAX)
						temp.setMaxC(parseDoubleSafely(parser.getText()));
					if(which == MIN)
						temp.setMinC(parseDoubleSafely(parser.getText()));
				}
				if(tagName.equals("fahrenheit")) {
					if(which == MAX)
						temp.setMaxF(parseDoubleSafely(parser.getText()));
					if(which == MIN)
						temp.setMinF(parseDoubleSafely(parser.getText()));
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if(tagName.equals("max") || tagName.equals("min")) {
					which = 0;
				}
				if(tagName.equals("temperature")){
					return true;
				}
				tagName = "";
				break;
			}
			eventType = parser.next();
		}
		return false;
	}

	private static double parseDoubleSafely(String s) {
		double d = Double.NaN;

		try {
			d = Double.parseDouble(s.trim());
		} catch(NumberFormatException nfe) {
			d = Double.NaN;
		}
		return d;
	}

	private static int parseIntSafely(String s) {
		return Integer.parseInt(s.trim());
	}

	private boolean parseCopyright(XmlPullParser parser, Copyright copyright) 
	throws XmlPullParserException, IOException {
		String tagName = "";
		int eventType = parser.next();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch(eventType) {
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if(tagName.equals("provider")) {
					Provider provider = copyright.new Provider();
					provider.setName(parser.getAttributeValue(null, "name"));
					provider.setLink(parser.getAttributeValue(null, "link"));
					copyright.addProvider(provider);
				}
				if(tagName.equals("image")) {
					parseImage(parser, copyright.getBanner());
				}
				break;
			case XmlPullParser.TEXT:
				if(tagName.equals("title")) {
					copyright.setTitle(parser.getText());
				}
				if(tagName.equals("link")) {
					copyright.setLink(parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				if(tagName.equals("copyright")){
					return true;
				}
				tagName = "";
				break;
			}
			eventType = parser.next();
		}
		return false;
	}
}
