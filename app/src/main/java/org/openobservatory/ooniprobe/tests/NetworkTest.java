package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Test;

import java.util.ArrayList;

public class NetworkTest {
    public static ArrayList<MKNetworkTest> mkNetworkTests;
    Result result;
    Context context;

    public NetworkTest(){
        result = new Result();
    }

    public void run (){
        //for (MKNetworkTest current : mkNetworkTests)
        //current.run();
    }

    public class WCNetworkTest extends NetworkTest{
        public WCNetworkTest(){
            result.name = Test.WEBSITES;
            mkNetworkTests.add(new WebConnectivity(context));
        }
    }
    public class IMNetworkTest extends NetworkTest{
        public IMNetworkTest(){
            result.name = Test.INSTANT_MESSAGING;
            //TODO preference isTestWhatsapp
            if (true)
                mkNetworkTests.add(new Whatsapp(context));
            //TODO preference isTestTelegram
            if (true)
                mkNetworkTests.add(new Telegram(context));
            //TODO preference isTestFacebookMessenger
            if (true)
                mkNetworkTests.add(new FacebookMessenger(context));
        }
    }
    public class MBNetworkTest extends NetworkTest{
        public MBNetworkTest(){
            result.name = Test.MIDDLE_BOXES;
            //TODO preference isRunHttpInvalidRequestLine
            if (true)
                mkNetworkTests.add(new HttpInvalidRequestLine(context));
            //TODO preference isRunHttpHeaderFieldManipulation
            if (true)
                mkNetworkTests.add(new HttpHeaderFieldManipulation(context));
        }
    }

    public class SPNetworkTest extends NetworkTest{
        public SPNetworkTest(){
            result.name = Test.PERFORMANCE;
            //TODO preference isRunNdt
            if (true)
                mkNetworkTests.add(new Ndt(context));
            //TODO preference isRunDash
            if (true)
                mkNetworkTests.add(new Dash(context));

        }
    }

}