package org.openobservatory.engine

import oonimkall.XOONIRunDescriptor
import oonimkall.XOONIRunFetchResponse
import oonimkall.XOONIRunNettest


data class OONIRunFetchResponse(
	@JvmField
	val createdTime: String,
	@JvmField
	val descriptor: OONIRunDescriptor
) {
	constructor(data: XOONIRunFetchResponse) : this(
		data.creationTime,
		OONIRunDescriptor(data.descriptor)
	)
}

class OONIRunDescriptor constructor(descriptor: XOONIRunDescriptor) {

	val author: String
	val description: String
	val icon: String
	val archived: Boolean
	val name: String
	val shortDescription: String
	val descriptionIntl: String
	val nameIntl: String
	val nettests: List<OONIRunNettest>

	init {
		author = descriptor.author
		description = descriptor.description
		icon = descriptor.icon
		archived = descriptor.isArchived
		name = descriptor.name
		shortDescription = descriptor.shortDescription
		descriptionIntl = descriptor.name
		nameIntl = descriptor.name
		nettests = ArrayList()
		for (i in 0 until descriptor.nettests.size()) {
			val netTest = descriptor.nettests.at(i)
			nettests.add(OONIRunNettest(netTest))
		}
	}
}


class OONIRunNettest constructor(nettest: XOONIRunNettest) {

	val name: String
	val inputs: List<String>

	init {
		name = nettest.testName
		inputs = ArrayList()
		for (i in 0 until nettest.inputs().size()) {
			inputs.add(nettest.inputs().at(i))
		}
	}
}
