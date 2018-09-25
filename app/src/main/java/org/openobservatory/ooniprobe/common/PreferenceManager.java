package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;

import org.openobservatory.ooniprobe.R;

import java.util.ArrayList;

public class PreferenceManager {
	private static final String SHOW_INTRO = "first_run";
	public static final String GEO_VER = "geo_ver";
	private SharedPreferences sp;
	private Resources r;

	PreferenceManager(Context context) {
		android.support.v7.preference.PreferenceManager.setDefaultValues(context, R.xml.preferences_global, true);
		android.support.v7.preference.PreferenceManager.setDefaultValues(context, R.xml.preferences_instant_messaging, true);
		android.support.v7.preference.PreferenceManager.setDefaultValues(context, R.xml.preferences_middleboxes, true);
		android.support.v7.preference.PreferenceManager.setDefaultValues(context, R.xml.preferences_performance, true);
		android.support.v7.preference.PreferenceManager.setDefaultValues(context, R.xml.preferences_websites, true);
		sp = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(context);
		r = context.getResources();
	}

	public int getGeoVer() {
		return sp.getInt(GEO_VER, 0);
	}

	public void setGeoVer(int geoVer) {
		sp.edit().putInt(GEO_VER, geoVer).apply();
	}

	public String getndtServer() {
		return sp.getString(r.getString(R.string.ndt_server), null);
	}

	public Integer getndtServerPort() {
		return sp.getInt(r.getString(R.string.ndt_server_port), 3001);
	}

	public String getdashServer() {
		return sp.getString(r.getString(R.string.dash_server), null);
	}

	public Integer getdashServerPort() {
		return sp.getInt(r.getString(R.string.dash_server_port), 80);
	}

	public Float getMaxRuntime() {
		try {
			return sp.getFloat(r.getString(R.string.max_runtime), 90);
		} catch (ClassCastException e) {
			return 90f;
		}
	}

	public boolean isSendCrash() {
		return sp.getBoolean(r.getString(R.string.send_crash), true);
	}

	public boolean isShowIntro() {
		return sp.getBoolean(SHOW_INTRO, true);
	}

	public void setShowIntro(boolean showIntro) {
		sp.edit().putBoolean(SHOW_INTRO, showIntro).apply();
	}

	public boolean isNotifications() {
		return sp.getBoolean(r.getString(R.string.notifications_enabled), true);
	}

	public boolean isNotificationsCompletion() {
		return sp.getBoolean(r.getString(R.string.notifications_completion), true);
	}

	public boolean isNotificationsNews() {
		return sp.getBoolean(r.getString(R.string.notifications_news), true);
	}

	public boolean isUploadResults() {
		return sp.getBoolean(r.getString(R.string.upload_results), true);
	}

	public Integer getNoUploadResults() {
		return isUploadResults() ? 0 : 1;
	}

	public boolean isIncludeIp() {
		return sp.getBoolean(r.getString(R.string.include_ip), true);
	}

	public Integer getIncludeIp() {
		return isIncludeIp() ? 1 : 0;
	}

	public boolean isIncludeAsn() {
		return sp.getBoolean(r.getString(R.string.include_asn), true);
	}

	public Integer getIncludeAsn() {
		return isIncludeAsn() ? 1 : 0;
	}

	public boolean isKeepScreenOn() {
		return sp.getBoolean(r.getString(R.string.keep_screen_on), true);
	}

	public boolean isDebugLogs() {
		return sp.getBoolean(r.getString(R.string.debugLogs), true);
	}

	public boolean isIncludeCc() {
		return sp.getBoolean(r.getString(R.string.include_cc), true);
	}

	public Integer getIncludeCc() {
		return isIncludeCc() ? 1 : 0;
	}

	public boolean isTestWhatsapp() {
		return sp.getBoolean(r.getString(R.string.test_whatsapp), true);
	}

	public boolean isTestWhatsappExtensive() {
		return sp.getBoolean(r.getString(R.string.test_whatsapp_extensive), true);
	}

	public boolean isTestTelegram() {
		return sp.getBoolean(r.getString(R.string.test_telegram), true);
	}

	public boolean isTestFacebookMessenger() {
		return sp.getBoolean(r.getString(R.string.test_facebook_messenger), true);
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

	public boolean isNdtServerAuto() {
		return sp.getBoolean(r.getString(R.string.ndt_server_auto), true);
	}

	public boolean isRunDash() {
		return sp.getBoolean(r.getString(R.string.run_dash), true);
	}

	public boolean isDashServerAuto() {
		return sp.getBoolean(r.getString(R.string.dash_server_auto), true);
	}

	public boolean isAllCategoryEnabled() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_ALDR_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_REL_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_PORN_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_PROV_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_POLR_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_HUMR_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_ENV_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_MILX_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_HATE_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_NEWS_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_XED_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_PUBH_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_GMB_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_ANON_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_DATE_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_GRP_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_LGBT_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_FILE_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_HACK_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_COMT_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_MMED_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_HOST_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_SRCH_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_GAME_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_CULTR_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_ECON_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_GOVT_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_COMM_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_CTRL_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_IGO_Key), true) &&
				sp.getBoolean(r.getString(R.string.CategoryCode_MISC_Key), true);
	}

	public String getEnabledCategory() {
		ArrayList<String> list = new ArrayList<>(31);
		String aldr = r.getString(R.string.CategoryCode_ALDR_Key);
		if (sp.getBoolean(aldr, true))
			list.add(aldr);
		String rel = r.getString(R.string.CategoryCode_REL_Key);
		if (sp.getBoolean(rel, true))
			list.add(rel);
		String porn = r.getString(R.string.CategoryCode_PORN_Key);
		if (sp.getBoolean(porn, true))
			list.add(porn);
		String prov = r.getString(R.string.CategoryCode_PROV_Key);
		if (sp.getBoolean(prov, true))
			list.add(prov);
		String polr = r.getString(R.string.CategoryCode_POLR_Key);
		if (sp.getBoolean(polr, true))
			list.add(polr);
		String humr = r.getString(R.string.CategoryCode_HUMR_Key);
		if (sp.getBoolean(humr, true))
			list.add(humr);
		String env = r.getString(R.string.CategoryCode_ENV_Key);
		if (sp.getBoolean(env, true))
			list.add(env);
		String milx = r.getString(R.string.CategoryCode_MILX_Key);
		if (sp.getBoolean(milx, true))
			list.add(milx);
		String hate = r.getString(R.string.CategoryCode_HATE_Key);
		if (sp.getBoolean(hate, true))
			list.add(hate);
		String news = r.getString(R.string.CategoryCode_NEWS_Key);
		if (sp.getBoolean(news, true))
			list.add(news);
		String xed = r.getString(R.string.CategoryCode_XED_Key);
		if (sp.getBoolean(xed, true))
			list.add(xed);
		String pubh = r.getString(R.string.CategoryCode_PUBH_Key);
		if (sp.getBoolean(pubh, true))
			list.add(pubh);
		String gmb = r.getString(R.string.CategoryCode_GMB_Key);
		if (sp.getBoolean(gmb, true))
			list.add(gmb);
		String anon = r.getString(R.string.CategoryCode_ANON_Key);
		if (sp.getBoolean(anon, true))
			list.add(anon);
		String date = r.getString(R.string.CategoryCode_DATE_Key);
		if (sp.getBoolean(date, true))
			list.add(date);
		String grp = r.getString(R.string.CategoryCode_GRP_Key);
		if (sp.getBoolean(grp, true))
			list.add(grp);
		String lgbt = r.getString(R.string.CategoryCode_LGBT_Key);
		if (sp.getBoolean(lgbt, true))
			list.add(lgbt);
		String file = r.getString(R.string.CategoryCode_FILE_Key);
		if (sp.getBoolean(file, true))
			list.add(file);
		String hack = r.getString(R.string.CategoryCode_HACK_Key);
		if (sp.getBoolean(hack, true))
			list.add(hack);
		String comt = r.getString(R.string.CategoryCode_COMT_Key);
		if (sp.getBoolean(comt, true))
			list.add(comt);
		String mmed = r.getString(R.string.CategoryCode_MMED_Key);
		if (sp.getBoolean(mmed, true))
			list.add(mmed);
		String host = r.getString(R.string.CategoryCode_HOST_Key);
		if (sp.getBoolean(host, true))
			list.add(host);
		String srch = r.getString(R.string.CategoryCode_SRCH_Key);
		if (sp.getBoolean(srch, true))
			list.add(srch);
		String game = r.getString(R.string.CategoryCode_GAME_Key);
		if (sp.getBoolean(game, true))
			list.add(game);
		String cultr = r.getString(R.string.CategoryCode_CULTR_Key);
		if (sp.getBoolean(cultr, true))
			list.add(cultr);
		String econ = r.getString(R.string.CategoryCode_ECON_Key);
		if (sp.getBoolean(econ, true))
			list.add(econ);
		String govt = r.getString(R.string.CategoryCode_GOVT_Key);
		if (sp.getBoolean(govt, true))
			list.add(govt);
		String comm = r.getString(R.string.CategoryCode_COMM_Key);
		if (sp.getBoolean(comm, true))
			list.add(comm);
		String ctrl = r.getString(R.string.CategoryCode_CTRL_Key);
		if (sp.getBoolean(ctrl, true))
			list.add(ctrl);
		String igo = r.getString(R.string.CategoryCode_IGO_Key);
		if (sp.getBoolean(igo, true))
			list.add(igo);
		String misc = r.getString(R.string.CategoryCode_MISC_Key);
		if (sp.getBoolean(misc, true))
			list.add(misc);
		return TextUtils.join(",", list);
	}
}
