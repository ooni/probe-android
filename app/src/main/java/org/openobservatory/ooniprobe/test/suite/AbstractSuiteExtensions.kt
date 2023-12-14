package org.openobservatory.ooniprobe.test.suite

import org.openobservatory.ooniprobe.activity.runtests.models.ChildItem
import org.openobservatory.ooniprobe.activity.runtests.models.GroupItem
import org.openobservatory.ooniprobe.common.PreferenceManager

fun AbstractSuite.runTestsGroupItem(preferenceManager: PreferenceManager): GroupItem {
	if (this is ExperimentalSuite) {
		return GroupItem(
			selected = false,
			name = this.name,
			nettests = this.getTestList(preferenceManager).map { nettest ->
				ChildItem(
					selected = preferenceManager.isExperimentalOn,
					name = nettest.name,
					inputs = nettest.inputs
				)
			}.toMutableList().apply {
				addAll(longRunningTests().map {
					ChildItem(
						selected = false,
						name = it.name,
						inputs = null
					)
				})
			})
	} else {
		return GroupItem(
			selected = false,
			name = this.name,
			nettests = this.getTestList(preferenceManager).map { nettest ->
				ChildItem(
					selected = preferenceManager.resolveStatus(nettest.name),
					name = nettest.name,
					inputs = nettest.inputs
				)
			})
	}
}

fun AbstractSuite.dynamicTestSuite(nettests: List<ChildItem>): DynamicTestSuite {
	return DynamicTestSuite(
		name = this.name,
		title = this.title,
		cardDesc = this.cardDesc,
		icon = this.icon,
		icon_24 = this.iconGradient,
		color = this.color,
		themeLight = this.themeLight,
		themeDark = this.themeDark,
		desc1 = this.desc1,
		anim = this.anim,
		dataUsage = this.dataUsage,
		nettest = nettests
	)
}
