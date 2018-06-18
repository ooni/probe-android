package org.openobservatory.ooniprobe.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import org.openobservatory.ooniprobe.R;

public class PreferenceManager {
	private static final String SHOW_INTRO = "first_run";
	private SharedPreferences sp;
	private Resources r;

	PreferenceManager(Context context) {
		sp = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
		r = context.getResources();
	}

	public String getndtServer() {
		return sp.getString(r.getString(R.string.ndt_server), null);
	}

	public String getndtServerPort() {
		return sp.getString(r.getString(R.string.ndt_server_port), null);
	}

	public String getdashServer() {
		return sp.getString(r.getString(R.string.dash_server), null);
	}

	public String getdashServerPort() {
		return sp.getString(r.getString(R.string.dash_server_port), null);
	}

	public String getmaxRuntime() {
		return sp.getString(r.getString(R.string.max_runtime), null);
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

	public boolean isIncludeIp() {
		return sp.getBoolean(r.getString(R.string.include_ip), true);
	}

	public boolean isIncludeAsn() {
		return sp.getBoolean(r.getString(R.string.include_asn), true);
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


	//TODO All these are not needed, I will only need a method that returns array of enabled (or disabled) categories
	public boolean isALDR() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_ALDR_Key), true);
	}

	public boolean isREL() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_REL_Key), true);
	}

	public boolean isPORN() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_PORN_Key), true);
	}

	public boolean isPROV() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_PROV_Key), true);
	}

	public boolean isPOLR() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_POLR_Key), true);
	}

	public boolean isHUMR() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_HUMR_Key), true);
	}

	public boolean isENV() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_ENV_Key), true);
	}

	public boolean isMILX() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_MILX_Key), true);
	}

	public boolean isHATE() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_HATE_Key), true);
	}

	public boolean isNEWS() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_NEWS_Key), true);
	}

	public boolean isXED() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_XED_Key), true);
	}

	public boolean isPUBH() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_PUBH_Key), true);
	}

	public boolean isGMB() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_GMB_Key), true);
	}

	public boolean isANON() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_ANON_Key), true);
	}

	public boolean isDATE() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_DATE_Key), true);
	}

	public boolean isGRP() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_GRP_Key), true);
	}

	public boolean isLGBT() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_LGBT_Key), true);
	}

	public boolean isFILE() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_FILE_Key), true);
	}

	public boolean isHACK() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_HACK_Key), true);
	}

	public boolean isCOMT() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_COMT_Key), true);
	}

	public boolean isMMED() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_MMED_Key), true);
	}

	public boolean isHOST() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_HOST_Key), true);
	}

	public boolean isSRCH() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_SRCH_Key), true);
	}

	public boolean isGAME() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_GAME_Key), true);
	}

	public boolean isCULTR() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_CULTR_Key), true);
	}

	public boolean isECON() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_ECON_Key), true);
	}

	public boolean isGOVT() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_GOVT_Key), true);
	}

	public boolean isCOMM() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_COMM_Key), true);
	}

	public boolean isCTRL() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_CTRL_Key), true);
	}

	public boolean isIGO() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_IGO_Key), true);
	}

	public boolean isMISC() {
		return sp.getBoolean(r.getString(R.string.CategoryCode_MISC_Key), true);
	}
}
