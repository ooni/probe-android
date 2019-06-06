package org.openobservatory.ooniprobe.model.database;

import android.content.Context;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.OrchestraTask;

import java.io.Serializable;

@Table(database = Application.class)
public class Network extends BaseModel implements Serializable {
	@PrimaryKey(autoincrement = true) public int id;
	@Column public String network_name;
	@Column public String ip;
	@Column public String asn;
	@Column public String country_code;
	@Column public String network_type;

	public Network() {
	}

	public Network(String networkName, String ip, String asn, String countryCode, String networkType) {
		this.network_name = networkName;
		this.ip = ip;
		this.asn = asn;
		this.country_code = countryCode;
		this.network_type = networkType;
	}

	public static Network getNetwork(String networkName, String ip, String asn, String countryCode, String networkType) {
		Network network = SQLite.select().from(Network.class).where(Network_Table.network_name.eq(networkName), Network_Table.ip.eq(ip), Network_Table.asn.eq(asn), Network_Table.country_code.eq(countryCode), Network_Table.network_type.eq(networkType)).querySingle();
		if (network == null)
			network = new Network(networkName, ip, asn, countryCode, networkType);
		return network;
	}

	public static String getAsn(Context c, Network network) {
		if (network != null && !TextUtils.isEmpty(network.asn))
			return network.asn;
		return c.getString(R.string.TestResults_UnknownASN);
	}

	public static String getName(Context c, Network network) {
		if (network != null && !TextUtils.isEmpty(network.network_name))
			return network.network_name;
		return c.getString(R.string.TestResults_UnknownASN);
	}

	public static String getCountry(Context c, Network network) {
		if (network != null && !TextUtils.isEmpty(network.country_code))
			return network.country_code;
		return c.getString(R.string.TestResults_UnknownASN);
	}

	public static String toString(Context c, Network n) {
		return getAsn(c, n) + " - " + getName(c, n);
	}

	public static String getLocalizedNetworkType(Context c, Network n) {
		if (n == null)
			return c.getString(R.string.TestResults_UnknownASN);
		else
			switch (n.network_type) {
				case OrchestraTask.WIFI:
					return c.getString(R.string.TestResults_Summary_Hero_WiFi);
				case OrchestraTask.MOBILE:
					return c.getString(R.string.TestResults_Summary_Hero_Mobile);
				case OrchestraTask.NO_INTERNET:
					return c.getString(R.string.TestResults_Summary_Hero_NoInternet);
				default:
					return c.getString(R.string.TestResults_UnknownASN);
			}
	}

	@Override public boolean delete() {
		//Delete Network only if it's used in one or less Result
		return SQLite.selectCountOf().from(Result.class).where(Result_Table.network_id.eq(id)).longValue() > 1 && super.delete();
	}
}
