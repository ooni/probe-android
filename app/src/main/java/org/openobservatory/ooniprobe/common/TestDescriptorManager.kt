package org.openobservatory.ooniprobe.common

import android.content.Context
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.test.suite.DynamicTestSuite
import org.openobservatory.ooniprobe.test.test.AbstractTest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestDescriptorManager @Inject constructor(private val context: Context) {
    private val descriptors: List<OONIDescriptor<BaseNettest>> = ooniDescriptors(context)

    fun getDescriptors(): List<OONIDescriptor<BaseNettest>> {
        return descriptors
    }

    fun getDescriptorByName(name: String): OONIDescriptor<BaseNettest>? {
        return descriptors.find { it.name == name }
    }

    fun getTestByDescriptorName(name: String): DynamicTestSuite? {
        return getDescriptorByName(name)?.getTest(context)
    }
}
