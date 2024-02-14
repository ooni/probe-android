package org.openobservatory.ooniprobe.common

import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.test.test.*


private fun PreferenceManager.experimentalTestList(): MutableList<String> {
    val exclusionList = mutableListOf<String>()
    OONITests.EXPERIMENTAL.run {
        exclusionList.addAll(nettests.map { it.name })
        longRunningTests?.map { it.name }?.let { exclusionList.addAll(it) }
    }

    return exclusionList
}

/**
 * This function is used to resolve the status of a given test.
 * @param name The name of the test.
 * @param prefix The **[OONIDescriptor.preferencePrefix]** of the descriptor.
 * @return The status of the test.
 */
fun PreferenceManager.resolveStatus(
    name: String, prefix: String, autoRun: Boolean = false
): Boolean {
    if (!autoRun) {
        if (name == WebConnectivity.NAME) {
            return true
        } else if (experimentalTestList().contains(name)) {
            return isExperimentalOn
        }
    }
    val key = getPreferenceKey(name = name, prefix = prefix, autoRun = autoRun)
    return if (autoRun) {
        sp.getBoolean(
            getPreferenceKey(name = name, prefix = prefix, autoRun = autoRun),
            resolveStatus(name = name, prefix = prefix)
        )
    } else {
        /**
         * If the preference key does not exist, we return the default value for the test.
         * This is needed because ooni provided tests will not be explicitly enabled by default.
         *
         * However, we want to show them as enabled in the UI.
         *
         * Using the **[OONIDescriptor.preferencePrefix]** as an identifier, we can determine if the test is an ooni test(prefix is blank)
         * and set the default value accordingly.
         *
         * The prefix is blank for ooni tests because they do not have a descriptor id.
         * Additionally, for backward compatibility, the preference key for ooni tests
         * is a lookup from **[PreferenceManager.getPreferenceKey]** with the test name.
         */
        return sp.getBoolean(getPreferenceKey(name = name, prefix = prefix), prefix.isBlank())
    }
}

fun PreferenceManager.enableTest(
    name: String,
    prefix: String,
    autoRun: Boolean = false,
): Boolean {
    return setValue(name = name, value = true, prefix = prefix, autoRun = autoRun)
}

fun PreferenceManager.disableTest(
    name: String,
    prefix: String,
    autoRun: Boolean = false,
): Boolean {
    return setValue(name = name, value = false, prefix = prefix, autoRun = autoRun)
}

private fun PreferenceManager.setValue(
    name: String,
    value: Boolean,
    prefix: String,
    autoRun: Boolean = false,
): Boolean {
    if (experimentalTestList().contains(name) && !autoRun) {
        return false
    }
    val key = getPreferenceKey(name = name, prefix = prefix, autoRun = autoRun)

    return with(sp.edit()) {
        putBoolean(getPreferenceKey(name = name, prefix = prefix, autoRun = autoRun), value)
        commit()
    }
}

/**
 * This function is used to resolve the preference key for a given test.
 * The preference key is the name of the test prefixed with the **[OONIDescriptor.preferencePrefix]** of the
 * descriptor.
 *
 * For example, the preference key for the test "web_connectivity" in the
 * descriptor "websites" is "websites_web_connectivity".
 *
 * @param name The name of the test.
 * @param prefix The **[OONIDescriptor.preferencePrefix]** of the descriptor.
 * @return The preference key.
 */
fun PreferenceManager.getPreferenceKey(
    name: String, prefix: String, autoRun: Boolean = false
): String {
    return when (autoRun) {
        true -> "${prefix}autorun_${getPreferenceKey(name)}"
        false -> "$prefix${getPreferenceKey(name)}"
    }
}


/**
 * This function returns the name of the preference key for a given test name.
 * @param name The name of the test.
 * @return The base preference key.
 */
fun PreferenceManager.getPreferenceKey(name: String): String {
    return when (name) {
        Dash.NAME -> r.getString(R.string.run_dash)

        FacebookMessenger.NAME -> r.getString(R.string.test_facebook_messenger)

        HttpHeaderFieldManipulation.NAME -> r.getString(R.string.run_http_header_field_manipulation)

        HttpInvalidRequestLine.NAME -> r.getString(R.string.run_http_invalid_request_line)

        Ndt.NAME -> r.getString(R.string.run_ndt)

        Psiphon.NAME -> r.getString(R.string.test_psiphon)

        RiseupVPN.NAME -> r.getString(R.string.test_riseupvpn)

        Signal.NAME -> r.getString(R.string.test_signal)

        Telegram.NAME -> r.getString(R.string.test_telegram)

        Tor.NAME -> r.getString(R.string.test_tor)

        Whatsapp.NAME -> r.getString(R.string.test_whatsapp)

        OONITests.EXPERIMENTAL.label -> r.getString(R.string.experimental)

        else -> name
    }
}

/**
 * Enables a test in the [PreferenceManager].
 *
 * @param name The name of the test to enable.
 * @return true if the test was successfully enabled, false otherwise.
 */
fun PreferenceManager.enableTest(name: String): Boolean {
    if (!experimentalTestList().contains(name)) {
        return this.setValue(name, true)
    }
    return false
}


/**
 * Disables a test in the [PreferenceManager].
 *
 * @param name The name of the test to disable.
 * @return true if the test was successfully disabled, false otherwise.
 */
fun PreferenceManager.disableTest(name: String): Boolean {
    if (!experimentalTestList().contains(name)) {
        return this.setValue(name, false)
    }
    return false
}

/**
 * Sets the value of a preference in the [PreferenceManager].
 *
 * @param name The name of the preference to set.
 * @param value The value to set the preference to.
 * @return true if the preference was successfully set, false otherwise.
 */
private fun PreferenceManager.setValue(name: String, value: Boolean): Boolean {
    if (experimentalTestList().contains(name)) {
        return false
    }
    return with(sp.edit()) {
        putBoolean(getPreferenceKey(name), value)
        commit()
    }
}
