package net.autch.webservice.lwws;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class CityDefinitionParser {
	List<List<Map<Integer, String>>> areas;

	public void getDefinitionXML(InputStream bodyStream) {
		final XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(new InputStreamReader(bodyStream));
			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch(eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if(parser.getName().equals("area")
						|| parser.getName().equals("city")
						|| parser.getName().equals("pref")) {
						Log.d("CityDefParser","Tag " + parser.getName());
						int attrsCount = parser.getAttributeCount();
						for(int i = 0; i < attrsCount; i++) {
							Log.d("CityDefParser", "Attr[" + i + "]: " + parser.getAttributeNamespace(i)
									+ ":" + parser.getAttributeName(i) + "=" + parser.getAttributeValue(i));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.TEXT:
					break;
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
		}
	}

	private void onAreaTagStart(XmlPullParser parser) {

	}

	private void onPrefTagStart(XmlPullParser parser) {

	}

	private void onCityTagStart(XmlPullParser parser) {

	}
}
