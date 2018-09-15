package org.openobservatory.ooniprobe.test.suite;

import android.content.Context;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;

import java.util.ArrayList;

public class MiddleBoxesSuite extends AbstractSuite {
	public static final String NAME = "middle_boxes";

	public MiddleBoxesSuite() {
		super(NAME,
				R.string.Test_Middleboxes_Fullname,
				R.string.Dashboard_Middleboxes_Card_Description,
				R.drawable.test_middle_boxes,
				R.color.color_violet8,
				R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_MiddleBoxes,
				R.style.Theme_AppCompat_NoActionBar_App_MiddleBoxes,
				R.string.Dashboard_Middleboxes_Overview_Paragraph_1,
				R.string.Dashboard_Middleboxes_Overview_Paragraph_2,
				R.xml.preferences_middleboxes,
				"anim/middle_boxes.json");
	}

	@Override public AbstractTest[] getTestList(Context c, PreferenceManager pm, Gson gson) {
		ArrayList<AbstractTest> list = new ArrayList<>();
		if (pm.isRunHttpHeaderFieldManipulation())
			list.add(new HttpHeaderFieldManipulation(c, pm, gson));
		if (pm.isRunHttpInvalidRequestLine())
			list.add(new HttpInvalidRequestLine(c, pm, gson));
		return list.toArray(new AbstractTest[list.size()]);
	}
}
