package net.autch.webservice.lwws;

import java.net.MalformedURLException;

import android.net.Uri;

/**
 * Livedoor Weather Web Service クライアントの実装
 * 
 * リクエストに必要なデータを集め、HTTP でリクエストを実行し、パーサを起動してレスポンスを解析させ、
 * 結果を Forecast に入れて返す。
 *
 * @see ForecastParser
 * @see Forecast
 */
public class ForecastService {
	
	public class Request {
		private static final String BASE_URL = "http://weather.livedoor.com/forecast/webservice/rest/v1";
		private int city_id;
		private int day;
		
		public static final int TODAY = 1;
		public static final int TOMORROW = 2;
		public static final int DAY_AFTER_TOMORROW = 3;
		private final String[] DAY_STRING = { null, "today", "tomorrow", "dayaftertomorrow" };

		public Request(int cityId, int day) {
			this.city_id = cityId;
			this.day = day;
		}

		public int getCityId() {
			return city_id;
		}

		public void setCityId(int cityId) {
			city_id = cityId;
		}

		public int getDay() {
			return day;
		}

		public void setDay(int day) {
			this.day = day;
		}
		
		public Uri toUri() throws MalformedURLException {
			if(day < 1 || day > 3)
				throw new MalformedURLException("'day' should be one of TODAY, TOMORROW, DAY_AFTER_TOMORROW");
			
			Uri uri = Uri.parse(BASE_URL);
			Uri.Builder builder = uri.buildUpon();
			builder.appendQueryParameter("city", String.valueOf(city_id));
			builder.appendQueryParameter("day", DAY_STRING[day]);
			
			return builder.build();
		}
	}

	public class Response {
		
	}
}
