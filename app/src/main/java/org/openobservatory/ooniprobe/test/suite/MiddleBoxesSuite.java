package org.openobservatory.ooniprobe.test.suite;

import android.content.res.Resources;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;

import java.util.ArrayList;

/**
 * @deprecated
 * It is not possible to run a MiddleBoxesSuite anymore
 * The HttpHeaderFieldManipulation and HttpInvalidRequestLine tests
 * are being ran inside the PerformanceSuite
 */
@Deprecated
public class MiddleBoxesSuite extends AbstractSuite {
	public static final String NAME = "middle_boxes";

	public MiddleBoxesSuite(Resources resources) {
		super(NAME,
				resources.getString(R.string.Test_Middleboxes_Fullname),
				resources.getString(R.string.Dashboard_Middleboxes_Card_Description),
				R.drawable.test_middle_boxes,
				R.drawable.test_middle_boxes_24,
				R.color.color_violet8,
				R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_MiddleBoxes,
				R.style.Theme_MaterialComponents_NoActionBar_App_MiddleBoxes,
				resources.getString(R.string.Dashboard_Middleboxes_Overview_Paragraph),
				"anim/middle_boxes.json",
				R.string.small_datausage);
	}

	@Override public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
		if (super.getTestList(pm) == null) {
			ArrayList<AbstractTest> list = new ArrayList<>();
			if (pm == null || pm.isRunHttpHeaderFieldManipulation())
				list.add(new HttpHeaderFieldManipulation());
			if (pm == null || pm.isRunHttpInvalidRequestLine())
				list.add(new HttpInvalidRequestLine());
			super.setTestList(Lists.transform(list, test->{
				if (getAutoRun()) test.setOrigin(AbstractTest.AUTORUN);
				return test;
			}).toArray(new AbstractTest[0]));
		}
		return super.getTestList(pm);
	}
}
