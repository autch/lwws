package net.autch.webservice.lwws;

import java.util.Date;
import java.util.List;

/**
 * レスポンスとして得られた XML に対応するエンティティ
 * 
 * ここではコンテナだけを定義し、パーサは別クラスに置く。
 * 天気予報サービスのクラスも別にある。
 * 
 * @see ForecastService 
 */
public class Forecast {
	private String author;
	private String area, pref, city;
	
	private String title, link;
	private int forecastday;
	private String day;
	private Date forecastdate, publictime;
	private String telop, description;
	
	private List<PinpointLocation> pinpoint;
	private Image icon;
	private Temperature temperature;
	
	/**
	 * 画像情報
	 */
	private class Image {
		private String title, link, url;
		private int width, height;
	}
	
	/**
	 * ピンポイント予報の地点 
	 */
	private class PinpointLocation {
		private String title, link;
		private Date publictime;
	}

	/**
	 * 気温。最高・最低、摂氏・華氏。 
	 */
	private class Temperature {
		private double min_c, max_c; /// celsius
		private double min_f, max_f; /// fahrenheit
	}

	/**
	 * 著作権情報。 
	 */
	private class Copyright {
		private String title, link;
		private Image banner;
		private List<Provider> providers;
		
		private class Provider {
			private String name, link;
		}
	}
}
