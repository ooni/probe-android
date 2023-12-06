package org.openobservatory.ooniprobe.common

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import org.openobservatory.engine.BaseDescriptor
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.runtests.RunTestsActivity
import org.openobservatory.ooniprobe.activity.runtests.models.ChildItem
import org.openobservatory.ooniprobe.activity.runtests.models.GroupItem
import org.openobservatory.ooniprobe.test.suite.AbstractSuite
import org.openobservatory.ooniprobe.test.suite.DynamicTestSuite
import org.openobservatory.ooniprobe.test.test.*
import java.io.Serializable

open class OONIDescriptor<T : BaseNettest>(
    override var name: String,
    open var title: String,
    open var shortDescription: String,
    open var description: String,
    open var icon: String,
    @ColorRes open var color: Int,
    open var animation: String,
    @StringRes open var dataUsage: Int,
    override var nettests: List<T>,
    var longRunningTests: List<T>? = null
) : Serializable, BaseDescriptor<T>(name = name, nettests = nettests) {

    /**
     * Checks if any of the nettests are enabled based on the preferences stored in the provided [PreferenceManager].
     *
     * @param preferenceManager The [PreferenceManager] instance used to resolve the status of each nettest.
     * @return Boolean Returns true if at least one nettest is enabled, false otherwise.
     */
    fun isEnabled(preferenceManager: PreferenceManager): Boolean {
        return nettests.any {
            preferenceManager.resolveStatus(
                name = it.name,
                prefix = preferencePrefix()
            )
        }
    }

    /**
     * Returns the display icon for the current descriptor.
     *
     * @return Int representing the display icon for the current descriptor.
     */
    fun getDisplayIcon(context: Context): Int {
        return context.resources.getIdentifier(icon, "drawable", context.packageName)
    }

    /**
     * Converts the current descriptor to a [GroupItem] to be used in the [RunTestsActivity].
     *
     * @return [GroupItem] representing the current descriptor.
     */
    fun toRunTestsGroupItem(preferenceManager: PreferenceManager): GroupItem {
        return GroupItem(selected = false,
            name = this.name,
            title = this.title,
            shortDescription = this.shortDescription,
            description = this.description,
            icon = this.icon,
            color = this.color,
            animation = this.animation,
            dataUsage = this.dataUsage,
            nettests = this.nettests.map { nettest ->
                ChildItem(
                    selected = when (this.name == OONITests.EXPERIMENTAL.label) {
                        true -> preferenceManager.isExperimentalOn
                        false -> preferenceManager.resolveStatus(nettest.name)
                    }, name = nettest.name, inputs = nettest.inputs
                )
            })
    }

    /**
     * Returns the runtime of the current descriptor.
     *
     * @return Int representing the runtime of the current descriptor.
     */
    fun getRuntime(context: Context, preferenceManager: PreferenceManager): Int {
        return getTest(context).getRuntime(preferenceManager)
    }

    /**
     * Returns the test suite for the current descriptor.
     *
     * @return [DynamicTestSuite] representing the test suite for the current descriptor.
     */
    fun getTest(context: Context): DynamicTestSuite {
        return DynamicTestSuite(
            name = this.name,
            title = this.title,
            shortDescription = this.shortDescription,
            description = this.description,
            icon = getDisplayIcon(context),
            icon_24 = getDisplayIcon(context),
            color = this.color,
            animation = this.animation,
            dataUsage = this.dataUsage,
            nettest = this.nettests
        )
    }

    /**
     * Returns the preference prefix to be used for the current descriptor.
     *
     * This function iterates over all OONI tests and finds the one that matches the current test name.
     * If a match is found (the test is an OONI provided test), it returns an empty string as the preference prefix.
     * If no match is found, it returns "descriptor_id_" as the default preference prefix.
     *
     * @return String representing the preference prefix.
     */
    private fun preferencePrefix(): String {
        return OONITests.values().find { it.label == name }?.let { "" } ?: "descriptor_id_"
    }
}

/**
 * Enum class representing the OONI tests.
 * Rational
 *
 * @param label String representing the label of the OONI test.
 * @param title Int representing the title of the OONI test.
 * @param shortDescription Int representing the short description of the OONI test.
 * @param description Int representing the description of the OONI test.
 * @param icon String representing the icon of the OONI test.
 * @param color Int representing the color of the OONI test.
 * @param animation String representing the animation of the OONI test.
 * @param dataUsage Int representing the data usage of the OONI test.
 * @param nettests List of [BaseNettest] representing the nettests of the OONI test.
 * @param longRunningTests List of [BaseNettest] representing the long-running nettests of the OONI test.
 */
enum class OONITests(
    val label: String,
    @StringRes val title: Int,
    @StringRes val shortDescription: Int,
    @StringRes val description: Int,
    val icon: String,
    val color: Int,
    val animation: String,
    @StringRes val dataUsage: Int,
    var nettests: List<BaseNettest>,
    var longRunningTests: List<BaseNettest>? = null
) {
    WEBSITES(
        label = "websites",
        title = R.string.Test_Websites_Fullname,
        shortDescription = R.string.Dashboard_Websites_Card_Description,
        description = R.string.Dashboard_Websites_Overview_Paragraph,
        icon = "test_websites",
        color = R.color.color_indigo6,
        animation = "anim/websites.json",
        dataUsage = R.string.websites_datausage,
        nettests = listOf(
            BaseNettest(name = WebConnectivity.NAME)
        )
    ),
    INSTANT_MESSAGING(
        label = "instant_messaging",
        title = R.string.Test_InstantMessaging_Fullname,
        shortDescription = R.string.Dashboard_InstantMessaging_Card_Description,
        description = R.string.Dashboard_InstantMessaging_Overview_Paragraph,
        icon = "test_instant_messaging",
        color = R.color.color_cyan6,
        animation = "anim/instant_messaging.json",
        dataUsage = R.string.small_datausage,
        nettests = listOf(
            BaseNettest(name = Whatsapp.NAME),
            BaseNettest(name = Telegram.NAME),
            BaseNettest(name = FacebookMessenger.NAME),
            BaseNettest(name = Signal.NAME),
        )
    ),
    CIRCUMVENTION(
        label = "circumvention",
        title = R.string.Test_Circumvention_Fullname,
        shortDescription = R.string.Dashboard_Circumvention_Card_Description,
        description = R.string.Dashboard_Circumvention_Overview_Paragraph,
        icon = "test_circumvention",
        color = R.color.color_pink6,
        animation = "anim/circumvention.json",
        dataUsage = R.string.small_datausage,
        nettests = listOf(
            BaseNettest(name = Psiphon.NAME),
            BaseNettest(name = Tor.NAME),
        )
    ),
    PERFORMANCE(
        label = "performance",
        title = R.string.Test_Performance_Fullname,
        shortDescription = R.string.Dashboard_Performance_Card_Description,
        description = R.string.Dashboard_Performance_Overview_Paragraph,
        icon = "test_performance",
        color = R.color.color_fuchsia6,
        animation = "anim/performance.json",
        dataUsage = R.string.performance_datausage,
        nettests = listOf(
            BaseNettest(name = Ndt.NAME),
            BaseNettest(name = Dash.NAME),
            BaseNettest(name = HttpHeaderFieldManipulation.NAME),
            BaseNettest(name = HttpInvalidRequestLine.NAME),
        )
    ),
    EXPERIMENTAL(
        label = "experimental",
        title = R.string.Test_Experimental_Fullname,
        shortDescription = R.string.Dashboard_Experimental_Card_Description,
        description = R.string.Dashboard_Experimental_Overview_Paragraph,
        icon = "test_experimental",
        color = R.color.color_gray7_1,
        animation = "anim/experimental.json",
        dataUsage = R.string.TestResults_NotAvailable,
        nettests = listOf(
            BaseNettest(name = "stunreachability"),
            BaseNettest(name = "dnscheck"),
            BaseNettest(name = "riseupvpn"),
            BaseNettest(name = "echcheck"),
        ),
        longRunningTests = listOf(
            BaseNettest(name = "torsf"),
            BaseNettest(name = "vanilla_tor"),
        )
    );

    /**
     * Converts the current OONI test to an [OONIDescriptor].
     *
     * @return [OONIDescriptor] representing the current OONI test.
     */
    fun toOONIDescriptor(context: Context): OONIDescriptor<BaseNettest> {
        val r = context.resources
        val experimentalLinks = """
        * [STUN Reachability](https://github.com/ooni/spec/blob/master/nettests/ts-025-stun-reachability.md)

        * [DNS Check](https://github.com/ooni/spec/blob/master/nettests/ts-028-dnscheck.md)
        
        * [RiseupVPN](https://ooni.org/nettest/riseupvpn/)

        * [ECH Check](https://github.com/ooni/spec/blob/master/nettests/ts-039-echcheck.md)

        * [Tor Snowflake](https://ooni.org/nettest/tor-snowflake/) ${
            String.format(
                " ( %s )", r.getString(R.string.Settings_TestOptions_LongRunningTest)
            )
        }

        * [Vanilla Tor](https://github.com/ooni/spec/blob/master/nettests/ts-016-vanilla-tor.md) ${
            String.format(
                " ( %s )", r.getString(R.string.Settings_TestOptions_LongRunningTest)
            )
        }
    """.trimIndent()
        this.run {
            return OONIDescriptor(
                name = label,
                title = context.getString(title),
                shortDescription = context.getString(shortDescription),
                description = when (label) {
                    EXPERIMENTAL.label -> context.getString(
                        description,
                        experimentalLinks
                    )

                    else -> context.getString(description)
                },
                icon = icon,
                color = color,
                animation = animation,
                dataUsage = dataUsage,
                nettests = nettests,
                longRunningTests = longRunningTests,
            )
        }
    }

    override fun toString(): String {
        return label
    }
}

/**
 * Creates a list of [OONIDescriptor] representing the OONI tests.
 *
 * @return List of [OONIDescriptor] representing the OONI tests.
 */
@SuppressLint("StringFormatInvalid")
fun ooniDescriptors(context: Context): MutableList<OONIDescriptor<BaseNettest>> {
    return mutableListOf(
        OONITests.WEBSITES.toOONIDescriptor(context),
        OONITests.INSTANT_MESSAGING.toOONIDescriptor(context),
        OONITests.CIRCUMVENTION.toOONIDescriptor(context),
        OONITests.PERFORMANCE.toOONIDescriptor(context),
        OONITests.EXPERIMENTAL.toOONIDescriptor(context)
    )
}
