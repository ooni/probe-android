package org.openobservatory.ooniprobe.activity.runtests.models

import org.openobservatory.engine.BaseDescriptor
import org.openobservatory.engine.BaseNettest

class ChildItem(
	var selected: Boolean,
	override var name: String,
	override var inputs: List<String>?
) : BaseNettest(name = name, inputs = inputs)

class GroupItem(
	var selected: Boolean,
	override var name: String,
	override var nettests: List<ChildItem>
) : BaseDescriptor<ChildItem>(name = name, nettests = nettests)
