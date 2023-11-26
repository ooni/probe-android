package org.openobservatory.engine

import java.io.Serializable

open class BaseNettest(
	open var name: String,
	open var inputs: List<String>? = null
) : Serializable

open class BaseDescriptor<T>(
	open var name: String,
	open var nettests: List<T>
) : Serializable
