package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.measurement_kit.nettests.TelegramTest;
import org.openobservatory.ooniprobe.model.JsonResult;
import org.openobservatory.ooniprobe.model.Summary;
import org.openobservatory.ooniprobe.model.Test;

import static org.openobservatory.ooniprobe.model.Measurement.MeasurementState.measurementFailed;

public class Telegram extends MKNetworkTest {

    public Telegram(Context context){
        super(context);
        super.name = Test.TELEGRAM;
        super.measurement.name = super.name;
        initTest();
    }

    public void run(){
        super.run();
    }

    public void initTest(){
        TelegramTest test = new TelegramTest();
        this.test = test;
        test.on_entry(new org.openobservatory.measurement_kit.nettests.EntryCallback() {
            @Override
            public void callback(String entry) {
                onEntry(entry);
            }
        });
        super.initCommon();
    }

    /*
         if "telegram_http_blocking", "telegram_tcp_blocking", "telegram_web_status" are null => failed
         if either "telegram_http_blocking" or "telegram_tcp_blocking" is true, OR if "telegram_web_status" is "blocked" => anomalous
     */
    public void onEntry(String entry) {
        JsonResult json = super.onEntryCommon(entry);
        if(json != null) {
            JsonResult.TestKeys keys = json.test_keys;
            if (keys.telegram_http_blocking == null || keys.telegram_tcp_blocking == null || keys.telegram_web_status == null)
                measurement.state = measurementFailed;
            else if (Boolean.valueOf(keys.telegram_http_blocking) || Boolean.valueOf(keys.telegram_tcp_blocking) || keys.telegram_web_status.equals("blocked"))
                measurement.anomaly = true;

            Summary summary = result.getSummary();
            summary.telegram = keys;
            super.updateSummary();
            measurement.save();
        }
    }
}
