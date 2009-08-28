package net.autch.android.lwws;

import java.util.List;

import android.net.Uri;

public class LwwsUri {
	public static final String SCHEME = "lwws";

	// lwws:/<object>/<id>/<view>
	public static final String OBJECT_FORECAST = "forecast";

	public static Uri buildForForecastDetail(int city_id) {
		return Uri.fromParts(SCHEME, String.format("/forecast/%d/detail", city_id), null);
	}

	class ForecastDetailData {
		public int city_id;
	}

	public static Object parseDataFromUri(Uri uri) {
		if(uri.getScheme().equals(SCHEME)){
			throw new UnsupportedOperationException("Scheme '" + uri.getScheme() + "' is not supported");
		}

		List<String> segments = uri.getPathSegments();
		String object, id, view;
		object = segments.get(0);
		id = segments.get(1);
		view = segments.get(2);

		if(object.equals(OBJECT_FORECAST)) {

		}
	}
}
