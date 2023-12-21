package org.openobservatory.ooniprobe.test.suite

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import com.google.common.collect.Lists
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.test.test.AbstractTest

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
    var nettest: List<BaseNettest>
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
            }
        }.toTypedArray()

        if(super.getTestList(pm).orEmpty().isEmpty()){
            super.setTestList(*tests)
        }
        return super.getTestList(pm)
    }
}
