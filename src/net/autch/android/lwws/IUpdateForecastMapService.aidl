package net.autch.android.lwws;

import net.autch.android.lwws.IUpdateForecastMapCallbackListener;

interface IUpdateForecastMapService {
	void addListener(IUpdateForecastMapCallbackListener listener);
	void removeListener(IUpdateForecastMapCallbackListener listener);
}
