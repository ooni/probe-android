package org.openobservatory.ooniprobe.tests;

import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.utils.TestUtility;

import java.util.ArrayList;

public class NetworkTest {
    public static ArrayList<MKNetworkTest> mkNetworkTests;
    Result result;

    public NetworkTest(){
        result = new Result();
    }

    public class WCNetworkTest extends NetworkTest{
        public WCNetworkTest(){
            result.name = TestUtility.WEBSITES;
            if (true)
                mkNetworkTests.add(new MKNetworkTest());
        }
    }
    public class IMNetworkTest extends NetworkTest{
        public IMNetworkTest(){
            result.name = TestUtility.INSTANT_MESSAGING;
        }
    }
    public class MBNetworkTest extends NetworkTest{
        public MBNetworkTest(){
            result.name = TestUtility.MIDDLE_BOXES;
        }
    }

    public class SPNetworkTest extends NetworkTest{
        public SPNetworkTest(){
            result.name = TestUtility.WEBSITES;
        }
    }

}

