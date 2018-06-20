package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Test;

import java.util.ArrayList;

public class NetworkTest {

	public static ArrayList<MKNetworkTest> mkNetworkTests;
	Result result;
	Context context;
	PreferenceManager preferenceManager;

	public NetworkTest() {
		result = new Result();
	}

	public void run() {
		//for (MKNetworkTest current : mkNetworkTests)
		//current.run();
	}

	public class WCNetworkTest extends NetworkTest {
		public WCNetworkTest() {
			result.name = Test.WEBSITES;
			mkNetworkTests.add(new WebConnectivity(context));
		}
	}

	public class IMNetworkTest extends NetworkTest {
		public IMNetworkTest() {
			result.name = Test.INSTANT_MESSAGING;
			if (preferenceManager.isTestWhatsapp())
				mkNetworkTests.add(new Whatsapp(context));
			if (preferenceManager.isTestTelegram())
				mkNetworkTests.add(new Telegram(context));
			if (preferenceManager.isTestFacebookMessenger())
				mkNetworkTests.add(new FacebookMessenger(context));
		}
	}

	public class MBNetworkTest extends NetworkTest {
		public MBNetworkTest() {
			result.name = Test.MIDDLE_BOXES;
			if (preferenceManager.isRunHttpInvalidRequestLine())
				mkNetworkTests.add(new HttpInvalidRequestLine(context));
			if (preferenceManager.isRunHttpHeaderFieldManipulation())
				mkNetworkTests.add(new HttpHeaderFieldManipulation(context));
		}
	}

	public class SPNetworkTest extends NetworkTest {
		public SPNetworkTest() {
			result.name = Test.PERFORMANCE;
			if (preferenceManager.isRunNdt())
				mkNetworkTests.add(new Ndt(context));
			if (preferenceManager.isRunDash())
				mkNetworkTests.add(new Dash(context));
		}
	}
}