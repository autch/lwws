package net.autch.webservice.lwws;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * レスポンスとして得られた XML に対応するエンティティ。
 * 
 * ここではコンテナだけを定義し、パーサは別クラスに置く。
 * 天気予報サービスのクラスも別にある。
 * 
 * @see ForecastService 
 */
public class Forecast {
	public static final int TODAY = 1;
	public static final int TOMORROW = 2;
	public static final int DAY_AFTER_TOMORROW = 3;

	public String author;
	public String area, pref, city;

	public String title, link;
	public int forecastday;
	public String day;
	public Date forecastdate, publictime;
	public String telop, description;

	public final ArrayList<PinpointLocation> pinpoint;
	public final Image icon;
	public final Temperature temperature;
	public final Copyright copyright;

	/**
	 * 画像情報
	 */
	public class Image {
		public String title, link, url;
		public int width, height;
	}

	/**
	 * ピンポイント予報の地点 
	 */
	public class PinpointLocation {
		public static final String KEY_TITLE = "title";
		public static final String KEY_LINK = "link";
		public static final String KEY_PUBLICTIME = "publictime";

		public String title, link;
		public Date publictime;

		public Map<String, Object> toMap() {
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put(KEY_TITLE, title);
			map.put(KEY_LINK, link);
			map.put(KEY_PUBLICTIME, publictime);
			return map;
		}
	}

	/**
	 * 気温。最高・最低、摂氏・華氏。 
	 */
	public class Temperature {
		public double min_c, max_c; /// celsius
		public double min_f, max_f; /// fahrenheit

		public Temperature() {
			min_c = max_c = min_f = max_f = Double.NaN;
		}
	}

	/**
	 * 著作権情報。 
	 */
	public class Copyright {
		public String title, link;
		public final Image banner;
		public final ArrayList<Provider> providers;

		public Copyright() {
			providers = new ArrayList<Provider>();
			banner = new Image();
		}

		public class Provider {
			public static final String KEY_NAME = "name";
			public static final String KEY_LINK = "link";

			public String name, link;

			public Map<String, Object> toMap() {
				HashMap<String, Object> map = new HashMap<String, Object>();

				map.put(KEY_NAME, name);
				map.put(KEY_LINK, link);
				return map;
			}
		}

		public ArrayList<Map<String, Object>> getProvidersAsMap() {
			ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			for(Provider provider : providers) {
				list.add(provider.toMap());
			}
			return list;
		}
	}

	public Forecast() {
		pinpoint = new ArrayList<PinpointLocation>();
		icon = new Image();
		temperature = new Temperature();
		copyright = new Copyright();
	}

	public ArrayList<Map<String, Object>> getPinpointAsMap() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for(PinpointLocation location : pinpoint) {
			list.add(location.toMap());
		}
		return list;
	}
}
