package org.openobservatory.ooniprobe.test.suite

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import com.google.common.collect.Lists
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.test.test.AbstractTest

/**
 * This class is used to create [AbstractTest] dynamically for all instances where a Test Suite is required.
 * It is used to create a test suite from a Descriptor.
 * It acts as a bridge between the Descriptor format and the [AbstractSuite].
 */
class DynamicTestSuite(
	name: String,
	@StringRes title: Int,
	@StringRes cardDesc: Int,
	@DrawableRes icon: Int,
	@DrawableRes icon_24: Int,
	@ColorRes color: Int,
	@StyleRes themeLight: Int,
	@StyleRes themeDark: Int,
	@StringRes desc1: Int,
	anim: String,
	dataUsage: Int,
	var nettest: List<BaseNettest>
) : AbstractSuite(
	name,
	title,
	cardDesc,
	icon,
	icon_24,
	color,
	themeLight,
	themeDark,
	desc1,
	anim,
	dataUsage
) {
	override fun getTestList(pm: PreferenceManager?): Array<AbstractTest> {
		super.setTestList(*Lists.transform<AbstractTest, AbstractTest>(
			nettest.map { AbstractTest.getTestByName(it.name) }
		) { test: AbstractTest? ->
			if (autoRun) test!!.setOrigin(AbstractTest.AUTORUN)
			test
		}.toTypedArray<AbstractTest>()
		)
		return super.getTestList(pm)
	}
}
