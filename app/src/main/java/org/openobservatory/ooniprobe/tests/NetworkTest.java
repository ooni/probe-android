package org.openobservatory.ooniprobe.tests;

import android.content.Context;

import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Test;

import java.util.ArrayList;

public class NetworkTest {

	public static ArrayList<MKNetworkTest> mkNetworkTests;
	Result result;
	Context context;
	PreferenceManager preferenceManager;

	public NetworkTest(Context context) {
		this.context = context;
		//TODO-TEST getPreferenceManager in a better way
		preferenceManager = ((AbstractActivity)context).getPreferenceManager();
		result = new Result();
		mkNetworkTests = new ArrayList<>();
	}

	public void run() {
		for (MKNetworkTest current : mkNetworkTests) {
		    current.setResultOfMeasurement(result);
            current.run();
        }
	}

	public void testEnded() {
		//TODO what i do in iOS
		/*
		Remove current from mkNetworkTests array
		if mkNetworkTests count == 0{
			//is last test
			result.done = TRUE;
		}
		 */
		result.setSummary();
		result.save();
		//if (preferenceManager.isNotifications() && preferenceManager.isNotificationsCompletion())
	  	//send local notification
	}
	

	public static class WCNetworkTest extends NetworkTest {
		public WCNetworkTest(Context context) {
			super(context);
			result.name = Test.WEBSITES;
			mkNetworkTests.add(new WebConnectivity(context));
		}
	}

	public static class IMNetworkTest extends NetworkTest {
		public IMNetworkTest(Context context) {
			super(context);
			result.name = Test.INSTANT_MESSAGING;
			if (preferenceManager.isTestWhatsapp())
				mkNetworkTests.add(new Whatsapp(context));
			if (preferenceManager.isTestTelegram())
				mkNetworkTests.add(new Telegram(context));
			if (preferenceManager.isTestFacebookMessenger())
				mkNetworkTests.add(new FacebookMessenger(context));
		}
	}

	public static class MBNetworkTest extends NetworkTest {
		public MBNetworkTest(Context context) {
			super(context);
			result.name = Test.MIDDLE_BOXES;
			if (preferenceManager.isRunHttpInvalidRequestLine())
				mkNetworkTests.add(new HttpInvalidRequestLine(context));
			if (preferenceManager.isRunHttpHeaderFieldManipulation())
				mkNetworkTests.add(new HttpHeaderFieldManipulation(context));
		}
	}

	public static class SPNetworkTest extends NetworkTest {
		public SPNetworkTest(Context context) {
			super(context);
			result.name = Test.PERFORMANCE;
			if (preferenceManager.isRunNdt())
				mkNetworkTests.add(new Ndt(context));
			if (preferenceManager.isRunDash())
				mkNetworkTests.add(new Dash(context));
		}
	}
}