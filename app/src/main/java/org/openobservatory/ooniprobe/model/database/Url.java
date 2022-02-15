package org.openobservatory.ooniprobe.model.database;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.AppDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Table(database = AppDatabase.class)
public class Url extends BaseModel implements Serializable {
	@PrimaryKey(autoincrement = true) public int id;
	@Column public String url;
	@Column public String category_code;
	@Column public String country_code;
	private transient Integer categoryIcon;

	public Url() {
	}

	public Url(String url, String categoryCode, String countryCode) {
		this.url = url;
		this.category_code = categoryCode;
		this.country_code = countryCode;
	}

	public static Url getUrl(String input) {
		return SQLite.select().from(Url.class).where(Url_Table.url.eq(input)).querySingle();
	}

	private static List<Url> getExistingUrls(Collection<String> urls) {
		return SQLite.select().from(Url.class).where(Url_Table.url.in(urls)).queryList();
	}

	private static void saveAll(Collection<Url> urls) {
		final DatabaseDefinition database = FlowManager.getDatabase(AppDatabase.class);
		database.executeTransaction(databaseWrapper -> Objects.requireNonNull(database.getModelAdapterForTable(Url.class)).saveAll(urls));
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


	/**
	 * This function saves or updates input urls ({@code List<Url>}) into the database and returns the
	 * list of strings ({@code List<String>}) used internally to perform this operation, where each string
	 * actually useful to continue processing the input URLs.
	 *
	 * @param urls
	 *        List of urls to save.
	 * @return list of url strings.
	 */
	public static List<String> saveOrUpdate(List<Url> urls) {

		Map<String, Url> resultUrlsMap = Maps.uniqueIndex(
				urls,
				input -> input.url
		);

		List<Url> existingUrls = Url.getExistingUrls(resultUrlsMap.keySet());

		List<Url> existingUrlsToUpdate = Lists.newArrayList(Iterables.filter(existingUrls, input -> {
			Url incomingChanges = resultUrlsMap.get(input.url);
			if (incomingChanges == null) {
				return false;
			}
			return ((!input.category_code.equals(incomingChanges.category_code)
					&& !incomingChanges.category_code.equals("MISC"))
					|| (!input.country_code.equals(incomingChanges.country_code)
					&& !incomingChanges.country_code.equals("XX")));
		}));

		if (!existingUrlsToUpdate.isEmpty()) {
			Url.saveAll(Lists.newArrayList(existingUrlsToUpdate));
		}

		if (existingUrls.size() != resultUrlsMap.size()) {

			List<String> existingUrlStrings = Lists.newArrayList(Iterables.transform(existingUrls, input -> input.url));
			Map<String, Url> newResultUrls = Maps.filterEntries(resultUrlsMap, input -> !existingUrlStrings.contains(input.getKey()));

			Url.saveAll(newResultUrls.values());

		}

		return new ArrayList<>(resultUrlsMap.keySet());
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
