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

	private String author;
	private String area, pref, city;

	private String title, link;
	private int forecastday;
	private String day;
	private Date forecastdate, publictime;
	private String telop, description;

	private final ArrayList<PinpointLocation> pinpoint;
	private final Image icon;
	private final Temperature temperature;
	private final Copyright copyright;

	/**
	 * 画像情報
	 */
	public class Image {
		private String title, link, url;
		private int width, height;

		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getLink() {
			return link;
		}
		public void setLink(String link) {
			this.link = link;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public int getHeight() {
			return height;
		}
		public void setHeight(int height) {
			this.height = height;
		}
	}

	/**
	 * ピンポイント予報の地点 
	 */
	public class PinpointLocation {
		public static final String KEY_TITLE = "title";
		public static final String KEY_LINK = "link";
		public static final String KEY_PUBLICTIME = "publictime";

		private String title, link;
		private Date publictime;

		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getLink() {
			return link;
		}
		public void setLink(String link) {
			this.link = link;
		}
		public Date getPublictime() {
			return publictime;
		}
		public void setPublictime(Date publictime) {
			this.publictime = publictime;
		}

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
		private double min_c, max_c; /// celsius
		private double min_f, max_f; /// fahrenheit

		public Temperature() {
			min_c = max_c = min_f = max_f = Double.NaN;
		}
		public double getMinC() {
			return min_c;
		}
		public void setMinC(double minC) {
			min_c = minC;
		}
		public double getMaxC() {
			return max_c;
		}
		public void setMaxC(double maxC) {
			max_c = maxC;
		}
		public double getMinF() {
			return min_f;
		}
		public void setMinF(double minF) {
			min_f = minF;
		}
		public double getMaxF() {
			return max_f;
		}
		public void setMaxF(double maxF) {
			max_f = maxF;
		}
	}

	/**
	 * 著作権情報。 
	 */
	public class Copyright {
		private String title, link;
		private final Image banner;
		private final ArrayList<Provider> providers;

		public Copyright() {
			providers = new ArrayList<Provider>();
			banner = new Image();
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public Image getBanner() {
			return banner;
		}

		public ArrayList<Provider> getProviders() {
			return providers;
		}

		public void addProvider(Provider provider) {
			this.providers.add(provider);
		}

		public class Provider {
			private String name, link;

			public String getName() {
				return name;
			}

			public String getLink() {
				return link;
			}

			public void setName(String name) {
				this.name = name;
			}

			public void setLink(String link) {
				this.link = link;
			}
		}
	}

	public Forecast() {
		pinpoint = new ArrayList<PinpointLocation>();
		icon = new Image();
		temperature = new Temperature();
		copyright = new Copyright();
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPref() {
		return pref;
	}

	public void setPref(String pref) {
		this.pref = pref;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getForecastday() {
		return forecastday;
	}

	public void setForecastday(int forecastday) {
		this.forecastday = forecastday;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public Date getForecastdate() {
		return forecastdate;
	}

	public void setForecastdate(Date forecastdate) {
		this.forecastdate = forecastdate;
	}

	public Date getPublictime() {
		return publictime;
	}

	public void setPublictime(Date publictime) {
		this.publictime = publictime;
	}

	public String getTelop() {
		return telop;
	}

	public void setTelop(String telop) {
		this.telop = telop;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Image getIcon() {
		return icon;
	}

	public Temperature getTemperature() {
		return temperature;
	}

	public ArrayList<PinpointLocation> getPinpoint() {
		return pinpoint;
	}

	public ArrayList<Map<String, Object>> getPinpointAsMap() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for(PinpointLocation location : pinpoint) {
			list.add(location.toMap());
		}
		return list;
	}

	public void addPinpoint(PinpointLocation pinpoint) {
		this.pinpoint.add(pinpoint);
	}

	public Copyright getCopyright() {
		return copyright;
	}
}
