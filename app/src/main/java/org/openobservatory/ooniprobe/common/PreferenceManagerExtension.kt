package org.openobservatory.ooniprobe.common

import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.test.test.*


fun PreferenceManager.enableTest(name: String) {
	this.setValue(name, true)
}

fun PreferenceManager.disableTest(name: String) {
	this.setValue(name, false)
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

		else -> {
			throw IllegalArgumentException("Unknown preference for: $name")
		}
	}
}
