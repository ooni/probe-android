package org.openobservatory.ooniprobe.test.suite;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.Experimental;

import java.util.ArrayList;

public class OONIRunSuite  extends AbstractSuite {
	public static final String NAME = "ooni-run";
	private final AbstractTest[] tests;

	public OONIRunSuite(TestDescriptor descriptor, AbstractTest... tests) {
		super(NAME,
			descriptor.getName(),
			descriptor.getShortDescription(),
			R.drawable.about_ooni,
			R.drawable.about_ooni,
			R.color.color_gray7_1,
			R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Experimental,
			R.style.Theme_MaterialComponents_NoActionBar_App_Experimental,
			descriptor.getDescription(),
			"anim/experimental.json",
			R.string.TestResults_NotAvailable);
		this.tests = tests;
	}


	@Override
	public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
		super.setTestList(tests);
		return super.getTestList(pm);
	}

}
