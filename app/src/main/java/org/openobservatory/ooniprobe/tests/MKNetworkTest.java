package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import com.google.gson.Gson;

import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.nettests.BaseTest;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Summary;
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

        //TODO
        Boolean include_ip = false;
        Boolean include_asn = true;
        Boolean include_cc = true;
        Boolean upload_results = true;

        test.use_logcat();
        test.set_output_filepath(measurement.getReportFile(context));
        test.set_error_filepath(measurement.getLogFile(context));
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
                updateProgress(percent);
            }
        });
        test.on_log(new org.openobservatory.measurement_kit.common.LogCallback(){
            @Override
            public void callback(long l, String s) {
                onLog(s);
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

    public void updateProgress(double percent){
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

    public void onLog(String s){
        //TODO update log on screen
        System.out.println(s);
    }

    public JsonResult onEntryCommon(String entry){
        if (entry != null) {
            JsonResult json = new Gson().fromJson(entry, JsonResult.class);
            if (json.test_start_time != null)
                result.setStartTimeWithUTCstr(Float.valueOf(json.test_start_time));
            if (json.measurement_start_time != null)
                measurement.setStartTimeWithUTCstr(Float.valueOf(json.measurement_start_time));
            if (json.test_runtime != null) {
                measurement.duration = Float.valueOf(json.test_runtime);
                result.addDuration(Float.valueOf(json.test_runtime));
            }
            //if the user doesn't want to share asn leave null on the db object
            //TODO ADD && [SettingsUtility getSettingWithName:@"include_asn"]
            if (json.probe_asn != null) {
                //TODO-SBS asn name
                measurement.asn = json.probe_asn;
                measurement.asnName = "Vodafone";
                if (result.asn == null){
                    result.asn = json.probe_asn;
                    result.asnName = "Vodafone";
                }
                else if (!measurement.asn.equals(result.asn))
                    System.out.println("Something's wrong");
            }
            //TODO ADD && [SettingsUtility getSettingWithName:@"include_cc"]
            if (json.probe_cc != null) {
                measurement.country = json.probe_cc;
                if (result.country == null){
                    result.country = json.probe_cc;
                }
                else if (!measurement.country.equals(result.country))
                    System.out.println("Something's wrong");
            }
            //TODO ADD && [SettingsUtility getSettingWithName:@"include_ip"]
            if (json.probe_ip != null) {
                measurement.ip = json.probe_ip;
                if (result.ip == null){
                    result.ip = json.probe_ip;
                }
                else if (!measurement.ip.equals(result.ip))
                    System.out.println("Something's wrong");
            }

            if (json.report_id != null) {
                measurement.reportId = json.report_id;
            }
            return json;
        }
        measurement.state = measurementFailed;
        return null;
    }

    public void updateSummary() {
        Summary summary = result.getSummary();
        if (measurement.state != measurementFailed)
            summary.failedMeasurements--;
        if (!measurement.anomaly)
            summary.okMeasurements++;
        else
            summary.anomalousMeasurements++;
        result.setSummary();
        result.save();
    }

    public void run(){
        measurement.state = measurementActive;
        measurement.save();
        //in iOS here i create a background task
    }

    public void testEnded() {
        updateProgress(1);
        measurement.state = measurementDone;
        measurement.save();
        //TODO call delegate (networktest) testEnded
    }
}
