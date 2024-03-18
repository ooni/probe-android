package org.openobservatory.ooniprobe.activity.runtests.models

import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.common.OONIDescriptor

class ChildItem(
    var selected: Boolean,
    override var name: String,
    override var inputs: List<String>?,
) : BaseNettest(name = name, inputs = inputs)

class GroupItem(
    var selected: Boolean,
    override var name: String,
    override var title: String,
    override var shortDescription: String,
    override var description: String,
    override var icon: String,
    override var color: Int,
    override var animation: String,
    override var dataUsage: Int,
    override var nettests: List<ChildItem>
) : OONIDescriptor<ChildItem>(
    name = name,
    title = title,
    shortDescription = shortDescription,
    description = description,
    icon = icon,
    color = color,
    animation = animation,
    dataUsage = dataUsage,
    nettests = nettests
)
