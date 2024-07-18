package org.openobservatory.ooniprobe.test.suite

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.runtests.models.ChildItem
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.model.database.Result
import org.openobservatory.ooniprobe.model.database.TestDescriptor
import org.openobservatory.ooniprobe.test.test.AbstractTest

/**
 * This class is used to create [AbstractTest] dynamically for all instances where a Test Suite is required.
 * It is used to create a test suite from a Descriptor.
 * It acts as a bridge between the Descriptor format and the [AbstractSuite].
 */
class DynamicTestSuite(
    name: String,
    title: String,
    shortDescription: String,
    @DrawableRes icon: Int,
    @DrawableRes icon_24: Int,
    @ColorInt color: Int,
    description: String,
    animation: String?,
    dataUsage: Int,
    var nettest: List<BaseNettest>,
    var longRunningTests: List<BaseNettest>? = null,
    var descriptor: TestDescriptor? = null
) : AbstractSuite(
    name,
    title,
    shortDescription,
    icon,
    icon_24,
    color,
    R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Experimental,
    R.style.Theme_MaterialComponents_NoActionBar_App_Experimental,
    description,
    animation,
    dataUsage
) {
    override fun getTestList(pm: PreferenceManager?): Array<AbstractTest> {
        val tests = nettest.map {
            AbstractTest.getTestByName(it.name).apply {
                if (autoRun) {
                    setOrigin(AbstractTest.AUTORUN)
                }
                inputs = it.inputs
            }
        }.run {
            // check if autoRun is enabled before adding longRunningTests
            if (autoRun) {
                this + (longRunningTests?.map {
                    AbstractTest.getTestByName(it.name).apply {
                        setOrigin(AbstractTest.AUTORUN)
                        inputs = it.inputs
                    }
                }.orEmpty())
            } else {
                this
            }
        }.toTypedArray()

        if(super.getTestList(pm).orEmpty().isEmpty()){
            super.setTestList(*tests)
        }
        return super.getTestList(pm)
    }

    override fun getResult(): Result? {
        val result = super.getResult()
        result.descriptor = descriptor
        return result
    }
}
