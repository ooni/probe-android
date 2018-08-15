package org.openobservatory.ooniprobe.model;

import android.content.Context;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;

import java.io.Serializable;

@Table(database = Application.class)
public class Network extends BaseModel implements Serializable {
	@PrimaryKey(autoincrement = true) public int id;
	@Column @Unique(uniqueGroups = 1) public String network_name;
	@Column @Unique(uniqueGroups = 1) public String ip;
	@Column @Unique(uniqueGroups = 1) public String asn;
	@Column @Unique(uniqueGroups = 1) public String country_code;
	@Column public String networkType; // TODO check

	public static Network querySingle(Network network) {
		// TODO add ip to checklist (it can be null)
		return SQLite.select().from(Network.class).where(Network_Table.network_name.eq(network.network_name), Network_Table.asn.eq(network.asn), Network_Table.country_code.eq(network.country_code)).querySingle();
	}

	public String getLocalizedNetworkType(Context context) {
		if (networkType.equals("wifi"))
			return context.getString(R.string.TestResults_Summary_Hero_WiFi);
		else if (networkType.equals("mobile"))
			return context.getString(R.string.TestResults_Summary_Hero_Mobile);
		else if (networkType.equals("no_internet"))
			return context.getString(R.string.TestResults_Summary_Hero_NoInternet);
		return "";
	}

	public String getAsn(Context context) {
		if (this.asn != null)
			return this.asn;
		return context.getString(R.string.TestResults_UnknownASN);
	}

	public String getAsnName(Context context) {
		if (this.network_name != null)
			return this.network_name;
		return context.getString(R.string.TestResults_UnknownASN);
	}

	public String getCountry(Context context) {
		if (this.country_code != null)
			return this.country_code;
		return context.getString(R.string.TestResults_UnknownASN);
	}
}
