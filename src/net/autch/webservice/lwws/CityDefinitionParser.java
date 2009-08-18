package net.autch.webservice.lwws;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import net.autch.android.lwws.ForecastMapDBHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Xml;

public class CityDefinitionParser {
	List<Map<String, List<Map<String, Map<String, Integer>>>>> areas;
	private int area_id, pref_id;
	private final SQLiteDatabase db;

	public CityDefinitionParser(SQLiteDatabase db) {
		area_id = 0;
		pref_id = 0;
		this.db = db;
	}
	
	public void getDefinitionXML(InputStream bodyStream) {
		final XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(new InputStreamReader(bodyStream));
			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String tagName = parser.getName();
					if(tagName.equals("area")) {
						area_id++;
						onAreaTagStart(parser);
					}
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void onAreaTagStart(XmlPullParser parser) throws XmlPullParserException, IOException {
		String title, source;
		
		title = parser.getAttributeValue(null, "title");
		source = parser.getAttributeValue(null, "source");
		ForecastMapDBHelper.insertArea(db, area_id, title);
		
		// 続き
		int eventType = parser.next();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();

			switch(eventType) {
			case XmlPullParser.START_TAG:
				if(tagName.equals("pref")) {
					pref_id++;
					onPrefTagStart(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				if(tagName.equals("area")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	private void onPrefTagStart(XmlPullParser parser) throws XmlPullParserException, IOException {
		String title, source;
		
		title = parser.getAttributeValue(null, "title");
		ForecastMapDBHelper.insertPrefecture(db, area_id, pref_id, title);

		int eventType = parser.next();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();

			switch(eventType) {
			case XmlPullParser.START_TAG:
				if(tagName.equals("city")) {
					onCityTagStart(parser);
				}
				break;
			case XmlPullParser.END_TAG:
				if(tagName.equals("pref")) {
					return;
				}
				break;
			case XmlPullParser.TEXT:
				break;
			}
			eventType = parser.next();
		}
	}

	private void onCityTagStart(XmlPullParser parser) throws XmlPullParserException, IOException {
		String title, source;
		int id;
		
		title = parser.getAttributeValue(null, "title");
		id = Integer.parseInt(parser.getAttributeValue(null, "id"));
		source = parser.getAttributeValue(null, "source");
		ForecastMapDBHelper.insertCity(db, area_id, pref_id, id, title);

		int eventType = parser.next();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();

			switch(eventType) {
			case XmlPullParser.END_TAG:
				if(tagName.equals("city")) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}
}
