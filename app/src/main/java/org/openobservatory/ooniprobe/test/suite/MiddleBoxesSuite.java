package org.openobservatory.ooniprobe.test.suite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class MiddleBoxesSuite extends AbstractSuite {
	public static final String NAME = "middle_boxes";

	public MiddleBoxesSuite() {
		super(NAME,
				R.string.Test_Middleboxes_Fullname,
				R.string.Dashboard_Middleboxes_Card_Description,
				R.drawable.test_middle_boxes,
				R.color.color_violet8,
				R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_MiddleBoxes,
				R.style.Theme_MaterialComponents_NoActionBar_App_MiddleBoxes,
				R.string.Dashboard_Middleboxes_Overview_Paragraph,
				R.xml.preferences_middleboxes,
				"anim/middle_boxes.json",
				"< 1 MB", 15);
	}

	@Override public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
		if (super.getTestList(pm) == null) {
			ArrayList<AbstractTest> list = new ArrayList<>();
			if (pm == null || pm.isRunHttpHeaderFieldManipulation())
				list.add(new HttpHeaderFieldManipulation());
			if (pm == null || pm.isRunHttpInvalidRequestLine())
				list.add(new HttpInvalidRequestLine());
			super.setTestList(list.toArray(new AbstractTest[0]));
		}
		return super.getTestList(pm);
	}
}
