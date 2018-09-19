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

	public static Url getUrl(String input) {
		return SQLite.select().from(Url.class).where(Url_Table.url.eq(input)).querySingle();
	}

	public static Url checkExistingUrl(String input) {
		return checkExistingUrl(input, "MISC", "XX");
	}

	public static Url checkExistingUrl(String input, String categoryCode, String countryCode) {
		Url url =  Url.getUrl(input);
		if (url == null){
			url = new Url(input, categoryCode, countryCode);
			//TODO serve?
			url.save();
		}
		else if ((!url.category_code.equals(categoryCode) && !categoryCode.equals("MISC"))
				|| (!url.country_code.equals(countryCode) && !countryCode.equals("XX"))){
			url.category_code = categoryCode;
			url.country_code = countryCode;
			url.save();
		}
		return url;

	}

}
