package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.DateFormat;

import org.openobservatory.engine.Engine;
import org.openobservatory.ooniprobe.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PreferenceManager {
	static final String GEO_VER = "geo_ver";
	public static final Integer MAX_RUNTIME_DISABLED = -1;
	private static final String IS_NOTIFICATION_DIALOG = "isNotificationDialog";
	public static final int NOTIFICATION_DIALOG_COUNT = 5;
	private static final String NOTIFICATION_DIALOG_DISABLE = "isNotificationDialogDisabled";
	private static final String TOKEN = "token";
	private static final String SHOW_ONBOARDING = "first_run";
	//This is in ms, set to one day
	public static final Integer DELETE_JSON_DELAY = 86400000;
	private static final String DELETE_JSON_KEY = "deleteUploadedJsons";
	private static final String UUID4 = "uuid4";
	public static final String AUTORUN_COUNT = "autorun_count";
	public static final String AUTORUN_DATE = "autorun_last_date";

	private final SharedPreferences sp;
	private final Resources r;

	PreferenceManager(Context context) {
		androidx.preference.PreferenceManager.setDefaultValues(context, R.xml.preferences_global, true);
		sp = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
		r = context.getResources();
	}

	public String getToken() {
		return sp.getString(TOKEN, null);
	}

	public void setToken(String token) {
		sp.edit().putString(TOKEN, token).apply();
	}

	public int getGeoVer() {
		return sp.getInt(GEO_VER, 0);
	}

	public void setGeoVer(int geoVer) {
		sp.edit().putInt(GEO_VER, geoVer).apply();
	}

	public Integer getMaxRuntime() {
		if (!isMaxRuntimeEnabled())
			return MAX_RUNTIME_DISABLED;
		try {
			return Integer.parseInt(sp.getString(r.getString(R.string.max_runtime), "90"));
		} catch (Exception e) {
			return 90;
		}
	}

	public boolean isMaxRuntimeEnabled(){
		return sp.getBoolean(r.getString(R.string.max_runtime_enabled), true);
	}

	public boolean isSendCrash() {
		return sp.getBoolean(r.getString(R.string.send_crash), false);
	}

	public boolean isShowOnboarding() {
		return sp.getBoolean(SHOW_ONBOARDING, true);
	}

	public void setShowOnboarding(boolean showIntro) {
		sp.edit().putBoolean(SHOW_ONBOARDING, showIntro)
				.apply();
	}

	public void setSendCrash(boolean sendCrash) {
		sp.edit().putBoolean(r.getString(R.string.send_crash), sendCrash)
				.apply();
	}

	/*
	 * This method is used to ask user to enable push notifications.
	 */
	public boolean isAskNotificationDialogDisabled() {
		return sp.getBoolean(NOTIFICATION_DIALOG_DISABLE, false);
	}

	public void disableAskNotificationDialog() {
		sp.edit().putBoolean(NOTIFICATION_DIALOG_DISABLE, true)
				.apply();
	}


	public boolean isNotifications() {
		return sp.getBoolean(r.getString(R.string.notifications_enabled), false);
	}


	public boolean isDarkTheme() {
		return sp.getBoolean(r.getString(R.string.theme_enabled), false);
	}


	public void setNotificationsFromDialog(boolean notifications) {
		//set notification value and increment app open
		sp.edit()
				.putLong(IS_NOTIFICATION_DIALOG, getAppOpenCount()+1)
				.putBoolean(r.getString(R.string.notifications_enabled), notifications)
				.apply();
	}

	public boolean isUploadResults() {
		return sp.getBoolean(r.getString(R.string.upload_results), true);
	}

	public boolean isDebugLogs() {
		return sp.getBoolean(r.getString(R.string.debugLogs), false);
	}

	public boolean isEnableProxyPsiphon() {
		return sp.getString(r.getString(R.string.proxy_enabled), "none").equals("psiphon");
	}

	public boolean isEnableProxyCustom() {
		return sp.getString(r.getString(R.string.proxy_enabled), "none").equals("custom");
	}

	public String getProxySelected() {
		return sp.getString(r.getString(R.string.proxy_enabled), "none");
	}

	public void setProxySelected(String selected) {
		 sp.edit()
			 .putString(r.getString(R.string.proxy_enabled), selected)
			 .apply();
	}

	public String getProxyURL() {
		if (isEnableProxyPsiphon()) {
			return "psiphon:///";
		}
		else if (isEnableProxyCustom()) {
			return sp.getString(r.getString(R.string.proxy_custom_value), "");
		}
		return "";
	}

	public void setProxyURL(String URL) {
		sp.edit()
			.putString(r.getString(R.string.proxy_custom_value), URL)
			.apply();
	}

	public String getProxyCustomProtocol() {
		return sp.getString(r.getString(R.string.proxy_custom_protocol), "SOCKS5");
	}

	public void setProxyCustomProtocol(String selected) {
		sp.edit()
			.putString(r.getString(R.string.proxy_custom_protocol), selected)
			.apply();
	}

	public String getProxyCustomHostname() {
		return sp.getString(r.getString(R.string.proxy_custom_hostname), "");
	}

	public void setProxyCustomHostname(String value) {
		sp.edit()
			.putString(r.getString(R.string.proxy_custom_hostname), value)
			.apply();
	}

	public String getProxyCustomPort() {
		return sp.getString(r.getString(R.string.proxy_custom_port), "");
	}

	public void setProxyCustomPort(String value) {
		sp.edit()
			.putString(r.getString(R.string.proxy_custom_port), value)
			.apply();
	}

	public String getProxyCustomUsername() {
		return sp.getString(r.getString(R.string.proxy_custom_username), "");
	}

	public void setProxyCustomUsername(String value) {
		sp.edit()
			.putString(r.getString(R.string.proxy_custom_username), value)
			.apply();
	}

	public String getProxyCustomPassword() {
		return sp.getString(r.getString(R.string.proxy_custom_password), "");
	}

	public void setProxyCustomPassword(String value) {
		sp.edit()
			.putString(r.getString(R.string.proxy_custom_password), value)
			.apply();
	}

	public boolean isEnableProxyPsiphonOverCustom() {
		return sp.getBoolean(r.getString(R.string.proxy_psiphon_over_custom), false);
	}

	public void seEnabledProxyPsiphonOverCustom(boolean isChecked) {
		sp.edit()
				.putBoolean(r.getString(R.string.proxy_psiphon_over_custom), isChecked)
				.apply();
	}

	public boolean isTestWhatsapp() {
		return sp.getBoolean(r.getString(R.string.test_whatsapp), true);
	}

	public boolean isTestTelegram() {
		return sp.getBoolean(r.getString(R.string.test_telegram), true);
	}

	public boolean isTestFacebookMessenger() {
		return sp.getBoolean(r.getString(R.string.test_facebook_messenger), true);
	}

	public boolean isTestSignal() {
		return sp.getBoolean(r.getString(R.string.test_signal), true);
	}

	public boolean isTestPsiphon() {
		return sp.getBoolean(r.getString(R.string.test_psiphon), true);
	}

	public boolean isTestTor() {
		return sp.getBoolean(r.getString(R.string.test_tor), true);
	}

	public boolean isTestRiseupVPN() {
		boolean isRiseupVPN = sp.getBoolean(r.getString(R.string.test_riseupvpn), true);
		return isRiseupVPN;
	}

	public boolean isRunHttpInvalidRequestLine() {
		return sp.getBoolean(r.getString(R.string.run_http_invalid_request_line), true);
	}

	public boolean isRunHttpHeaderFieldManipulation() {
		return sp.getBoolean(r.getString(R.string.run_http_header_field_manipulation), true);
	}

	public boolean isRunNdt() {
		return sp.getBoolean(r.getString(R.string.run_ndt), true);
	}

	public boolean isRunDash() {
		return sp.getBoolean(r.getString(R.string.run_dash), true);
	}

	public boolean isAllCategoryEnabled() {
		boolean out = true;
		for (String key : r.getStringArray(R.array.CategoryCodes))
			out = out && sp.getBoolean(key, true);
		return out;
	}

	public String getEnabledCategory() {
		if (isAllCategoryEnabled())
			return null;
		else {
			ArrayList<String> list = new ArrayList<>(31);
			for (String key : r.getStringArray(R.array.CategoryCodes))
				if (sp.getBoolean(key, true))
					list.add(key);
			return TextUtils.join(",", list);
		}
	}

	public ArrayList<String> getEnabledCategoryArr() {
		ArrayList<String> list = new ArrayList<>(31);
		for (String key : r.getStringArray(R.array.CategoryCodes)) {
			if (sp.getBoolean(key, true)) {
				list.add(key);
			}
		}
		return list;
	}


	public Integer countEnabledCategory() {
		int count = 0;
		for (String key : r.getStringArray(R.array.CategoryCodes))
			if (sp.getBoolean(key, true))
				count++;
		return count;
	}

	public boolean canCallDeleteJson(){
		long lastCalled = sp.getLong(DELETE_JSON_KEY, 0);

		if (lastCalled == 0)
			return true;

		if (System.currentTimeMillis() - lastCalled > DELETE_JSON_DELAY){
			return true;
		}
		return false;
	}

	public void setLastCalled(){
		sp.edit().putLong(DELETE_JSON_KEY, System.currentTimeMillis()).apply();
	}

	public String getOrGenerateUUID4() {
		String uuid = sp.getString(UUID4, Engine.newUUID4());
		sp.edit().putString(UUID4, uuid).apply();
		return uuid;
	}

	public void incrementAppOpenCount(){
		sp.edit().putLong(IS_NOTIFICATION_DIALOG, getAppOpenCount()+1)
				.apply();
	}

	public long getAppOpenCount(){
		return sp.getLong(IS_NOTIFICATION_DIALOG, 0);
	}

	public boolean isAutomaticTestEnabled() {
		return sp.getBoolean(r.getString(R.string.automated_testing_enabled), false);
	}

	public void disableAutomaticTest() {
		sp.edit().putBoolean(r.getString(R.string.automated_testing_enabled), false)
				.apply();
	}

	public boolean testWifiOnly() {
		return sp.getBoolean(r.getString(R.string.automated_testing_wifionly), true);
	}

	public boolean testChargingOnly() {
		return sp.getBoolean(r.getString(R.string.automated_testing_charging), true);
	}

	public void incrementAutorun(){
		sp.edit().putLong(AUTORUN_COUNT, getAutorun()+1)
				.apply();
	}

	public long getAutorun(){
		return sp.getLong(AUTORUN_COUNT, 0);
	}

	public void updateAutorunDate(){
		sp.edit().putLong(AUTORUN_DATE, System.currentTimeMillis())
				.apply();
	}

	public String getAutorunDate(){
		long timestamp = sp.getLong(AUTORUN_DATE, 0);
		if (timestamp == 0)
			return r.getString(R.string.Dashboard_Overview_LastRun_Never);
		Date date = new Date(timestamp);
		return DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), date).toString();
	}

}
