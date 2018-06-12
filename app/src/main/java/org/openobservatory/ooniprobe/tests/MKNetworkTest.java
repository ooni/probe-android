package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.nettests.BaseTest;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.utils.VersionUtils;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.*;

public class MKNetworkTest {
    Result result;
    Measurement measurement;
    String name;
    Context context;

    public MKNetworkTest(Context context){
        this.context = context;
        createMeasurementObject();
    }

    public void createMeasurementObject(){
        measurement = new Measurement();
        if (result != null)
            measurement.result = result;
        if (name != null)
            measurement.name = name;
        measurement.save();
    }

    public void setResultOfMeasurement(Result result) {
        this.result = result;
        measurement.result = result;
    }


    public BaseTest initCommon(BaseTest test) {
        final String geoip_asn = context.getFilesDir() + "/GeoIPASNum.dat";
        final String geoip_country = context.getFilesDir() + "/GeoIP.dat";

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        final Boolean include_ip = preferences.getBoolean("include_ip", false);
        final Boolean include_asn = preferences.getBoolean("include_asn", true);
        final Boolean include_cc = preferences.getBoolean("include_cc", true);
        final Boolean upload_results = preferences.getBoolean("upload_results", true);


        test.use_logcat();
        test.set_output_filepath(measurement.getReportFile(ctx));
        test.set_error_filepath(measurement.getLogFile(ctx));
        if (true)
            test.set_verbosity(LogSeverity.LOG_DEBUG2);
        else
            test.set_verbosity(LogSeverity.LOG_INFO);
        test.set_option("geoip_country_path", geoip_country);
        test.set_option("geoip_asn_path", geoip_asn);
        test.set_option("save_real_probe_ip", boolToString(include_ip));
        test.set_option("save_real_probe_asn", boolToString(include_asn));
        test.set_option("save_real_probe_cc", boolToString(include_cc));
        test.set_option("no_collector", boolToString(!upload_results));
        test.set_option("software_name", "ooniprobe-android");
        test.set_option("software_version", VersionUtils.get_software_version());
        test.on_progress(new org.openobservatory.measurement_kit.nettests.ProgressCallback() {
            @Override
            public void callback(double percent, String msg) {
                /*
                //TODO UPDATE progressbar. Old method below
                currentTest.progress = (int)(percent*100);
                if (activity != null){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TestData.getInstance(context, activity).notifyObservers();
                        }
                    });
                }*/
            }
        });
        test.on_log(new org.openobservatory.measurement_kit.common.LogCallback(){
            @Override
            public void callback(long l, String s) {
                //TODO update log on screen
                System.out.println(s);
            }
        });

        //on_begin
        //on_overall_data_usage
        //start
        return test;
    }

    private static String boolToString(Boolean b) {
        return b ? "1" : "0";
    }

    public void run(){

    }

    public JSONObject onEntryCommon(String entry){
        try {
            JSONObject jsonObj = new JSONObject(entry);
            JSONObject test_keys = jsonObj.getJSONObject("test_keys");
            jsonObj.getString("test_start_time");

            /*
            if ([json safeObjectForKey:@"test_start_time"])
            [self.result setStartTimeWithUTCstr:[json safeObjectForKey:@"test_start_time"]];
        if ([json safeObjectForKey:@"measurement_start_time"])
            [self.measurement setStartTimeWithUTCstr:[json safeObjectForKey:@"measurement_start_time"]];
        if ([json safeObjectForKey:@"test_runtime"]){
            [self.measurement setDuration:[[json safeObjectForKey:@"test_runtime"] floatValue]];
            [self.result addDuration:[[json safeObjectForKey:@"test_runtime"] floatValue]];
        }
        //if the user doesn't want to share asn leave null on the db object
        if ([json safeObjectForKey:@"probe_asn"] && [SettingsUtility getSettingWithName:@"include_asn"]){
            //TODO-SBS asn name
            [self.measurement setAsn:[json objectForKey:@"probe_asn"]];
            [self.measurement setAsnName:@"Vodafone"];
            if (self.result.asn == nil){
                //TODO-SBS asn name
                [self.result setAsn:[json objectForKey:@"probe_asn"]];
                [self.result setAsnName:@"Vodafone"];
                [self.result save];
            }
            else {
                if (![self.result.asn isEqualToString:self.measurement.asn])
                    NSLog(@"Something's wrong");
            }
        }
        if ([json safeObjectForKey:@"probe_cc"] && [SettingsUtility getSettingWithName:@"include_cc"]){
            [self.measurement setCountry:[json objectForKey:@"probe_cc"]];
            if (self.result.country == nil){
                [self.result setCountry:[json objectForKey:@"probe_cc"]];
                [self.result save];
            }
            else {
                if (![self.result.country isEqualToString:self.measurement.country])
                    NSLog(@"Something's wrong");
            }
        }
        if ([json safeObjectForKey:@"probe_ip"] && [SettingsUtility getSettingWithName:@"include_ip"]){
            [self.measurement setIp:[json objectForKey:@"probe_ip"]];
            if (self.result.ip == nil){
                [self.result setIp:[json objectForKey:@"probe_ip"]];
                [self.result save];
            }
            else {
                if (![self.result.ip isEqualToString:self.measurement.ip])
                    NSLog(@"Something's wrong");
            }
        }
        if ([json safeObjectForKey:@"report_id"])
            [self.measurement setReportId:[json objectForKey:@"report_id"]];

            */

            return jsonObj;
        } catch (JSONException e) {
            this.measurement.state = measurementFailed;
            return null;
        }
    }

    }
