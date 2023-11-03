package org.openobservatory.ooniprobe.common

import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite
import org.openobservatory.ooniprobe.test.test.*


private fun PreferenceManager.exclusionList(): MutableList<String> {
	val exclusionList = mutableListOf<String>()
	ExperimentalSuite().run {
		exclusionList.addAll(getTestList(this@exclusionList).map { it.name })
		exclusionList.addAll(longRunningTests().map { it.name })
	}

	return exclusionList

}

fun PreferenceManager.enableTest(name: String): Boolean {
	if (!exclusionList().contains(name)) {
		this.setValue(name, true)
		return true
	}
	return false
}

fun PreferenceManager.disableTest(name: String): Boolean {
	if (!exclusionList().contains(name)) {
		this.setValue(name, false)
		return true
	}
	return false
}

fun PreferenceManager.setValue(name: String, value: Boolean) {
	when (name) {
		WebConnectivity.NAME -> {}
		Dash.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.run_dash), value)
				apply()
			}
		}

		FacebookMessenger.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.test_facebook_messenger), value)
				apply()
			}
		}

		HttpHeaderFieldManipulation.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.run_http_header_field_manipulation), value)
				apply()
			}
		}

		HttpInvalidRequestLine.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.run_http_invalid_request_line), value)
				apply()
			}
		}

		Ndt.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.run_ndt), value)
				apply()
			}
		}

		Psiphon.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.test_psiphon), value)
				apply()
			}
		}

		RiseupVPN.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.test_riseupvpn), value)
				apply()
			}
		}

		Signal.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.test_signal), value)
				apply()
			}
		}

		Telegram.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.test_telegram), value)
				apply()
			}
		}

		Tor.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.test_tor), value)
				apply()
			}
		}

		Whatsapp.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.test_whatsapp), value)
				apply()
			}
		}

		ExperimentalSuite.NAME -> {
			with(sp.edit()) {
				putBoolean(r.getString(R.string.experimental), value)
				apply()
			}
		}

		else -> {
			throw IllegalArgumentException("Unknown preference for: $name")
		}
	}
}
