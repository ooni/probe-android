package org.openobservatory.ooniprobe.activity.runtests.models

import org.openobservatory.engine.BaseDescriptor
import org.openobservatory.engine.BaseNettest

data class ChildItem(
	var selected: Boolean,
	var name: String,
	var inputs: List<String>?
)

data class GroupItem(
	var selected: Boolean,
	var name: String,
	var nettests: List<ChildItem>
)
