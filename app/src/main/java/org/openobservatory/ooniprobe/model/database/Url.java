package org.openobservatory.ooniprobe.model.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.common.Application;

import java.io.Serializable;

@Table(database = Application.class)
public class Url extends BaseModel implements Serializable {
	@PrimaryKey(autoincrement = true) public int id;
	@Column public String url;
	@Column public String category_code;
	@Column public String country_code;

	public Url() {
	}

	public Url(String url, String categoryCode, String countryCode) {
		this.url = url;
		this.category_code = categoryCode;
		this.country_code = countryCode;
	}

	public Url(String url) {
		this.url = url;
		this.category_code = "MISC";
		this.country_code = "XX";
	}

	public void update(String categoryCode, String countryCode){
		this.category_code = categoryCode;
		this.country_code = countryCode;
	}

	public static Url getUrl(String url) {
		return SQLite.select().from(Url.class).where(Url_Table.url.eq(url)).querySingle();
	}

}
