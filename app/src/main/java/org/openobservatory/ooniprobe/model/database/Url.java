package org.openobservatory.ooniprobe.model.database;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.AppDatabase;

import java.io.Serializable;
import java.util.Arrays;

@Table(database = AppDatabase.class)
public class Url extends BaseModel implements Serializable {
	@PrimaryKey(autoincrement = true) public int id;
	@Column public String url;
	@Column public String category_code;
	@Column public String country_code;
	private transient Integer categoryIcon;

	public Url() {
	}

	private Url(String url, String categoryCode, String countryCode) {
		this.url = url;
		this.category_code = categoryCode;
		this.country_code = countryCode;
	}

	public static Url getUrl(String input) {
		return SQLite.select().from(Url.class).where(Url_Table.url.eq(input)).querySingle();
	}

	public static Url checkExistingUrl(String input) {
		return checkExistingUrl(input, "MISC", "XX");
	}

	public static Url checkExistingUrl(String input, String categoryCode, String countryCode) {
		Url url = Url.getUrl(input);
		if (url == null) {
			url = new Url(input, categoryCode, countryCode);
			url.save();
		} else if ((!url.category_code.equals(categoryCode) && !categoryCode.equals("MISC"))
				|| (!url.country_code.equals(countryCode) && !countryCode.equals("XX"))) {
			url.category_code = categoryCode;
			url.country_code = countryCode;
			url.save();
		}
		return url;
	}

	public int getCategoryIcon(Context c) {
		if (categoryIcon == null) {
			TypedArray categoryIcons = c.getResources().obtainTypedArray(R.array.CategoryIcons);
			categoryIcon = categoryIcons.getResourceId(Arrays.asList(c.getResources().getStringArray(R.array.CategoryCodes)).indexOf(category_code), -1);
			categoryIcons.recycle();
		}
		return categoryIcon;
	}

	@NonNull @Override public String toString() {
		return url;
	}
}
