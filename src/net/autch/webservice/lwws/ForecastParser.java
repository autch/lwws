package net.autch.webservice.lwws;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
	private static final SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

	public ForecastParser() {
	}

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
					forecast.area = parser.getAttributeValue(null, "area");
					forecast.pref = parser.getAttributeValue(null, "pref");
					forecast.city = parser.getAttributeValue(null, "city");
				}
				if(tagName.equals("pinpoint")){
					forecast = parsePinpoint(parser, forecast);
				}
				if(tagName.equals("image")) {
					parseImage(parser, forecast.icon);
				}
				if(tagName.equals("temperature")) {
					parseTemperature(parser, forecast.temperature);
				}
				if(tagName.equals("copyright")) {
					parseCopyright(parser, forecast.copyright);
				}
				break;
			case XmlPullParser.TEXT:
				if(tagName.equals("author")) {
					forecast.author = parser.getText();
				}
				if(tagName.equals("title")) {
					forecast.title = parser.getText();
				}
				if(tagName.equals("forecastday")) {
					String day = parser.getText();
					if(day.equals("today")) {
						forecast.forecastday = Forecast.TODAY;
					}
					if(day.equals("tomorrow")) {
						forecast.forecastday = Forecast.TOMORROW;
					}
					if(day.equals("dayaftertomorrow")) {
						forecast.forecastday = Forecast.DAY_AFTER_TOMORROW;
					}
				}
				if(tagName.equals("forecastdate")) {
					try {
						Date date = df.parse(parser.getText());
						forecast.forecastdate = date;
					} catch (ParseException e) {
						e.printStackTrace();
						forecast.forecastdate = null;
					}
				}
				if(tagName.equals("publictime")) {
					try {
						Date date = df.parse(parser.getText());
						forecast.publictime = date;
					} catch (ParseException e) {
						forecast.publictime = null;
					}
				}
				if(tagName.equals("day")) {
					forecast.day = parser.getText();
				}
				if(tagName.equals("link")) {
					forecast.link = parser.getText();
				}
				if(tagName.equals("telop")) {
					forecast.telop = parser.getText();
				}
				if(tagName.equals("description")) {
					forecast.description = parser.getText();
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
						forecast.pinpoint.add(location);
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
					location.title = parser.getText();
				}
				if(tagName.equals("link")) {
					location.link = parser.getText();
				}
				if(tagName.equals("publictime")) {
					try {
						Date date = df.parse(parser.getText());
						location.publictime = date;
					} catch (ParseException e) {
						location.publictime = null;
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
					image.title = parser.getText();
				}
				if(tagName.equals("link")) {
					image.link = parser.getText();
				}
				if(tagName.equals("url")) {
					image.url = parser.getText();
				}
				if(tagName.equals("width")) {
					image.width = parseIntSafely(parser.getText());
				}
				if(tagName.equals("height")) {
					image.height = parseIntSafely(parser.getText());
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
						temp.max_c = parseDoubleSafely(parser.getText());
					if(which == MIN)
						temp.min_c = parseDoubleSafely(parser.getText());
				}
				if(tagName.equals("fahrenheit")) {
					if(which == MAX)
						temp.max_f = parseDoubleSafely(parser.getText());
					if(which == MIN)
						temp.min_f = parseDoubleSafely(parser.getText());
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
					provider.name = parser.getAttributeValue(null, "name");
					provider.link = parser.getAttributeValue(null, "link");
					copyright.providers.add(provider);
				}
				if(tagName.equals("image")) {
					parseImage(parser, copyright.banner);
				}
				break;
			case XmlPullParser.TEXT:
				if(tagName.equals("title")) {
					copyright.title = parser.getText();
				}
				if(tagName.equals("link")) {
					copyright.link = parser.getText();
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
