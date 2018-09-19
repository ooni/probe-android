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

import java.io.Serializable;
import java.util.ArrayList;

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

	public static Network checkExistingNetwork(String networkName, String ip, String asn, String countryCode, String networkType) {
		Network network = SQLite.select().from(Network.class).where(Network_Table.network_name.eq(networkName), Network_Table.ip.eq(ip), Network_Table.asn.eq(asn), Network_Table.country_code.eq(countryCode), Network_Table.network_type.eq(networkType)).querySingle();
		if (network == null){
			network = new Network(networkName, ip, asn, countryCode, networkType);
			//TODO serve?
			network.save();
		}
		return network;
	}


	public String getLocalizedNetworkType(Context context) {
		switch (network_type) {
			case "wifi":
				return context.getString(R.string.TestResults_Summary_Hero_WiFi);
			case "mobile":
				return context.getString(R.string.TestResults_Summary_Hero_Mobile);
			case "no_internet":
				return context.getString(R.string.TestResults_Summary_Hero_NoInternet);
		}
		return "";
	}

	public String toString(Context c, int size) {
		ArrayList<String> parts = new ArrayList<>();
		if (network_name != null)
			parts.add(c.getString(R.string.bold, network_name));
		else if (asn != null)
			parts.add(c.getString(R.string.bold, asn));
		else
			parts.add(c.getString(R.string.TestResults_UnknownASN));
		if (size > 1 && network_type != null)
			parts.add(c.getString(R.string.brackets, getLocalizedNetworkType(c)));
		if (size > 2 && asn != null)
			parts.add(c.getString(R.string.seg, asn));
		return TextUtils.join(" ", parts);
	}

	public static String getAsn(Context context, Network network) {
		if (network != null && network.asn != null)
			return network.asn;
		return context.getString(R.string.TestResults_UnknownASN);
	}

	public static String getAsnName(Context context, Network network) {
		if (network != null && network.network_name != null)
			return network.network_name;
		return context.getString(R.string.TestResults_UnknownASN);
	}

	public static String getCountry(Context context, Network network) {
		if (network != null && network.country_code != null)
			return network.country_code;
		return context.getString(R.string.TestResults_UnknownASN);
	}
}
