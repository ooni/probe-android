package org.openobservatory.ooniprobe.tests;

import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Test;

import java.util.ArrayList;

public class NetworkTest {
    public static ArrayList<MKNetworkTest> mkNetworkTests;
    Result result;

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
            if (true)
                mkNetworkTests.add(new MKNetworkTest());
        }
    }
    public class IMNetworkTest extends NetworkTest{
        public IMNetworkTest(){
            result.name = Test.INSTANT_MESSAGING;
        }
    }
    public class MBNetworkTest extends NetworkTest{
        public MBNetworkTest(){
            result.name = Test.MIDDLE_BOXES;
        }
    }

    public class SPNetworkTest extends NetworkTest{
        public SPNetworkTest(){
            result.name = Test.WEBSITES;
        }
    }

}