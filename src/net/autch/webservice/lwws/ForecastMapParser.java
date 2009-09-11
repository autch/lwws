package net.autch.webservice.lwws;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.autch.android.lwws.ForecastMapTableHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.database.sqlite.SQLiteDatabase;
import android.util.Xml;

/**
 * Livedoor Weather Web Service で使う、地点番号の一覧を解析して SQLite データベースへ保存する。  
 * 
 * TODO: 用が済んだらさっさと return する
 * FIXME: XML が壊れてたら？
 */
public class ForecastMapParser {
	private int area_id, pref_id;
	private final ForecastMapTableHelper helper;

	public ForecastMapParser(SQLiteDatabase db) {
		area_id = 0;
		pref_id = 0;
		helper = new ForecastMapTableHelper(db);
	}

	public void getDefinitionXML(InputStream bodyStream) {
		final XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(new InputStreamReader(bodyStream));
			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagName = parser.getName();
				switch(eventType) {
				case XmlPullParser.START_TAG:
					if(tagName.equals("area")) {
						area_id++;
						onAreaTagStart(parser);
					}
					break;
				case XmlPullParser.END_TAG:
					if(tagName.equals("source")) {
						return;
					}
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onAreaTagStart(XmlPullParser parser) throws XmlPullParserException, IOException {
		String title, source;

		title = parser.getAttributeValue(null, "title");
		source = parser.getAttributeValue(null, "source");
		helper.insertArea(area_id, title);

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
		helper.insertPrefecture(area_id, pref_id, title);

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
		helper.insertCity(area_id, pref_id, id, title);

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
