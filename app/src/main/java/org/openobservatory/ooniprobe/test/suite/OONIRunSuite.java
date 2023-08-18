package org.openobservatory.ooniprobe.test.suite;

import android.content.Context;

import androidx.annotation.Nullable;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.StringUtils;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

public class OONIRunSuite  extends AbstractSuite {
	public static final String NAME = "ooni-run";
	private final AbstractTest[] tests;
	private final TestDescriptor descriptor;

	public OONIRunSuite(Context context, TestDescriptor descriptor, AbstractTest... tests) {
		super(NAME,
			descriptor.getName(),
			descriptor.getShortDescription(),
			context.getResources().getIdentifier(StringUtils.camelToSnake(descriptor.getIcon()), "drawable", context.getPackageName()),
			context.getResources().getIdentifier(StringUtils.camelToSnake(descriptor.getIcon()), "drawable", context.getPackageName()),
			R.color.color_gray7_1,
			R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Experimental,
			R.style.Theme_MaterialComponents_NoActionBar_App_Experimental,
			descriptor.getDescription(),
			descriptor.getAnimation(),
			R.string.TestResults_NotAvailable);
		this.tests = tests;
		this.descriptor = descriptor;
	}


	@Override
	public AbstractTest[] getTestList(@Nullable PreferenceManager pm) {
		super.setTestList(tests);
		return super.getTestList(pm);
	}

	public TestDescriptor getDescriptor() {
		return descriptor;
	}

	public Result getResult() {
		Result	result = super.getResult();
		result.descriptor = descriptor;
		return result;
	}
}
