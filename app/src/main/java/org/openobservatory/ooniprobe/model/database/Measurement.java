package org.openobservatory.ooniprobe.model.database;

import android.content.Context;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.MeasurementsClient;
import org.openobservatory.ooniprobe.common.TestListsClient;
import org.openobservatory.ooniprobe.model.api.ExplorerUrlResponse;
import org.openobservatory.ooniprobe.model.api.RetrieveUrlResponse;
import org.openobservatory.ooniprobe.model.api.MeasurementsResults;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Table(database = Application.class)
public class Measurement extends BaseModel implements Serializable {
	@PrimaryKey(autoincrement = true) public int id;
	@Column public String test_name;
	@Column public Date start_time;
	@Column public double runtime;
	@Column public boolean is_done;
	@Column public boolean is_uploaded;
	@Column public boolean is_failed;
	@Column public String failure_msg;
	@Column public boolean is_upload_failed;
	@Column public String upload_failure_msg;
	@Column public boolean is_rerun;
	@Column public String report_id;
	@Column public boolean is_anomaly;
	@Column public String test_keys;
	@ForeignKey(saveForeignKeyModel = true) public Url url;
	@ForeignKey(saveForeignKeyModel = true, stubbedRelationship = true) public Result result;
	private transient TestKeys testKeys;
	private transient AbstractTest test;

	public Measurement() {
	}

	public Measurement(Result result, String test_name) {
		this.result = result;
		this.test_name = test_name;
		start_time = new java.util.Date();
	}

	public Measurement(Result result, String test_name, String report_id) {
		this.result = result;
		this.test_name = test_name;
		this.report_id = report_id;
		start_time = new java.util.Date();
	}

	public static Where<Measurement> selectUploadable() {
		// We check on both the report_id and is_uploaded as we
		// may have some unuploaded measurements which are marked
		// as is_uploaded = true, but we always know that those with
		// report_id set to null are not uploaded
		return SQLite.select().from(Measurement.class)
				.where(Measurement_Table.is_failed.eq(false))
				.and(Measurement_Table.is_rerun.eq(false))
				.and(Measurement_Table.is_done.eq(true))
				.and(OperatorGroup.clause()
						.or(Measurement_Table.is_uploaded.eq(false))
						.or(Measurement_Table.report_id.isNull())
				);
	}

	public static Where<Measurement> selectUploadableWithResultId(int resultId) {
		return Measurement.selectUploadable().and(Measurement_Table.result_id.eq(resultId));
	}

	public static File getEntryFile(Context c, int measurementId, String test_name) {
		return new File(getMeasurementDir(c), measurementId + "_" + test_name + ".json");
	}

	public static File getLogFile(Context c, int resultId, String test_name) {
		return new File(getMeasurementDir(c), resultId + "_" + test_name + ".log");
	}

	static File getMeasurementDir(Context c) {
		return new File(c.getFilesDir(), Measurement.class.getSimpleName());
	}

	public AbstractTest getTest() {
		if (test == null)
			switch (test_name) {
				case FacebookMessenger.NAME:
					test = new FacebookMessenger();
					break;
				case Telegram.NAME:
					test = new Telegram();
					break;
				case Whatsapp.NAME:
					test = new Whatsapp();
					break;
				case HttpHeaderFieldManipulation.NAME:
					test = new HttpHeaderFieldManipulation();
					break;
				case HttpInvalidRequestLine.NAME:
					test = new HttpInvalidRequestLine();
					break;
				case WebConnectivity.NAME:
					test = new WebConnectivity();
					break;
				case Ndt.NAME:
					test = new Ndt();
					break;
				case Dash.NAME:
					test = new Dash();
					break;
			}
		return test;
	}

	@NonNull public TestKeys getTestKeys() {
		if (testKeys == null)
			testKeys = test_keys == null ? new TestKeys() : new Gson().fromJson(test_keys, TestKeys.class);
		return testKeys;
	}

	public boolean isUploaded() {
		return is_uploaded && report_id != null;
	}

	public void setTestKeys(TestKeys testKeys) {
		test_keys = new Gson().toJson(testKeys);
	}

	public void getExplorerUrl(){
		try {
			Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.ooni.io/").addConverterFactory(GsonConverterFactory.create()).build();
			//TODO not send url.url when is not web_connectivity
			Response<ExplorerUrlResponse> response = retrofit.create(MeasurementsClient.class).getMeasurementUrl(report_id, url.url).execute();
			if (response.isSuccessful() && response.body() != null && response.body().responseObject != null) {
				List<MeasurementsResults> results = response.body().responseObject.get("results");
				    /*
     					Checking if the array is longer than 1.
				    	https://github.com/ooni/probe-ios/pull/293#discussion_r302136014
     				*/
				/*
				Check array different from 1 and measurement_url exists
				otherwise emit error

				NSArray *resultsArray = [dic objectForKey:@"results"];
				if ([resultsArray count] != 1 ||
						![[resultsArray objectAtIndex:0] objectForKey:@"measurement_url"]) {
					errorcb([NSError errorWithDomain:@"io.ooni.api"
					code:ERR_JSON_EMPTY
					userInfo:@{NSLocalizedDescriptionKey:@"Modal.Error.JsonEmpty"
					}]);
					return;
				}
				successcb([[resultsArray objectAtIndex:0] objectForKey:@"measurement_url"]);
			*/
			}
		}
		catch (Exception e) {
			//publishProgress(ERR, act.getString(R.string.Modal_Error_CantDownloadURLs));
		}
	}
}
