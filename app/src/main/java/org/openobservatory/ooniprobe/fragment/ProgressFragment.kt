package org.openobservatory.ooniprobe.fragment

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.RunningActivity
import org.openobservatory.ooniprobe.common.Application
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.TestProgressRepository
import org.openobservatory.ooniprobe.common.service.RunTestService
import org.openobservatory.ooniprobe.databinding.FragmentProgressBinding
import org.openobservatory.ooniprobe.receiver.TestRunBroadRequestReceiver
import javax.inject.Inject

/**
 * Monitors and displays progress of [RunTestService].
 */
class ProgressFragment : Fragment() {
	private lateinit var receiver: TestRunBroadRequestReceiver
	private lateinit var biding: FragmentProgressBinding

	@Inject
	lateinit var preferenceManager: PreferenceManager

	@Inject
	lateinit var testProgressRepository: TestProgressRepository

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		biding = FragmentProgressBinding.inflate(inflater, container, false)
		(requireActivity().application as Application).fragmentComponent.inject(this)
		biding.root.setOnClickListener { _: View? ->
			val intent = Intent(context, RunningActivity::class.java)
			ActivityCompat.startActivity(requireContext(), intent, null)
		}
		testProgressRepository.progress.observe(viewLifecycleOwner) { progressValue: Int? ->
			if (progressValue != null) {
				biding.progress.progress = progressValue
			}
		}
		return biding.root
	}

	override fun onResume() {
		super.onResume()
		val filter = IntentFilter("org.openobservatory.ooniprobe.activity.RunningActivity")
		receiver = TestRunBroadRequestReceiver(
			preferenceManager, TestRunnerEventListener(), testProgressRepository
		)
		// NOTE: Simple update to ContextCompat#registerReceiver not possible at the moment.
		LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)
		bindTestService()
	}

	fun bindTestService() {
		if ((requireActivity().application as Application).isTestRunning) {
			requireContext().bindService(
				Intent(requireContext(), RunTestService::class.java),
				receiver,
				Context.BIND_AUTO_CREATE
			)
			biding.progressLayout.visibility = View.VISIBLE
		} else {
			biding.progressLayout.visibility = View.GONE
		}
	}

	private fun updateUI(service: RunTestService?) {
		if ((requireActivity().application as Application).isTestRunning) {
			val progressLevel = testProgressRepository.progress.value
			when {
				progressLevel != null -> {
					biding.progress.progress = progressLevel
				}
				else -> {
					biding.progress.isIndeterminate = true
				}
			}
			service?.task?.let { task ->
				task.currentSuite?.let {
					biding.progress.max = service.task.getMax(preferenceManager)
				}
				task.currentTest?.let {
					biding.name.text = getString(it.labelResId)
				}
			}
		}
	}

	override fun onPause() {
		super.onPause()
		if (receiver.isBound) {
			requireContext().unbindService(receiver)
			receiver.isBound = false
		}
		LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
	}

	override fun onDestroy() {
		super.onDestroy()
		LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
	}

	private inner class TestRunnerEventListener : TestRunBroadRequestReceiver.EventListener {
		override fun onStart(service: RunTestService) = updateUI(service)

		override fun onRun(value: String) {
			biding.name.text = value
		}

		override fun onProgress(state: Int, eta: Double) {
			if (biding.progress.isIndeterminate) {
				updateUI(receiver.service)
			}
			biding.progress.apply {
				isIndeterminate = false
				progress = state
			}
		}

		override fun onLog(value: String) {/* nothing */
		}

		override fun onError(value: String) {/* nothing */
		}

		override fun onUrl() {
			biding.progress.isIndeterminate = false
		}

		override fun onInterrupt() {
			biding.running.text = getString(R.string.Dashboard_Running_Stopping_Title)
		}

		override fun onEnd(context: Context) {
			biding.progressLayout.visibility = View.GONE
		}
	}
}
