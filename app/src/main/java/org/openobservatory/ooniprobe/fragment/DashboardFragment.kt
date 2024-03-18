package org.openobservatory.ooniprobe.fragment

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.activity.OverviewActivity
import org.openobservatory.ooniprobe.activity.runtests.RunTestsActivity
import org.openobservatory.ooniprobe.adapters.DashboardAdapter
import org.openobservatory.ooniprobe.common.Application
import org.openobservatory.ooniprobe.common.OONIDescriptor
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.ReachabilityManager
import org.openobservatory.ooniprobe.common.TestGroupStatus
import org.openobservatory.ooniprobe.common.TestStateRepository
import org.openobservatory.ooniprobe.common.ThirdPartyServices
import org.openobservatory.ooniprobe.databinding.FragmentDashboardBinding
import org.openobservatory.ooniprobe.fragment.dashboard.DashboardViewModel
import org.openobservatory.ooniprobe.model.database.Result
import javax.inject.Inject

class DashboardFragment : Fragment(), View.OnClickListener {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var viewModel: DashboardViewModel

    @Inject
    lateinit var testStateRepository: TestStateRepository

    private var descriptors: ArrayList<OONIDescriptor<BaseNettest>> = ArrayList()

    private lateinit var binding: FragmentDashboardBinding

    private lateinit var adapter: DashboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        (requireActivity().application as Application).fragmentComponent.inject(this)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.title = null
        }
        binding.apply {
            runAll.setOnClickListener { _: View? -> runAll() }
            vpn.setOnClickListener { _: View? -> (requireActivity().application as Application).openVPNSettings() }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getGroupedItemList().observe(viewLifecycleOwner) { items ->
            binding.recycler.layoutManager = LinearLayoutManager(requireContext())
            adapter = DashboardAdapter(items, this, preferenceManager)
            binding.recycler.adapter = adapter
        }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            descriptors.apply {
                clear()
                addAll(items)
            }
        }

        testStateRepository.testGroupStatus.observe(viewLifecycleOwner) { status ->
            if (status == TestGroupStatus.RUNNING) {
                binding.runAll.visibility = View.GONE
                binding.lastTested.visibility = View.GONE
            } else {
                binding.runAll.visibility = View.VISIBLE
                binding.lastTested.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        /**
         * Updates the list of tests when the user changes the default configuration
         * after starting a test from [RunTestsActivity]
         */
        binding.recycler.post { adapter.notifyDataSetChanged() }
        setLastTest()
        if (ReachabilityManager.isVPNinUse(this.context)
            && preferenceManager.isWarnVPNInUse
        ) binding.vpn.visibility = View.VISIBLE else binding.vpn.visibility = View.GONE
    }

    private fun setLastTest() {
        val lastResult = Result.getLastResult()
        if (lastResult == null) {
            (getString(R.string.Dashboard_Overview_LatestTest) + " " + getString(R.string.Dashboard_Overview_LastRun_Never))
                .also { binding.lastTested.text = it }
        } else {
            (getString(R.string.Dashboard_Overview_LatestTest) + " " + DateUtils.getRelativeTimeSpanString(lastResult.start_time.time))
                .also { binding.lastTested.text = it }
        }
    }

    private fun runAll() {
        ActivityCompat.startActivity(requireContext(), RunTestsActivity.newIntent(requireContext(), descriptors), null)
    }

    private fun onTestServiceStartedListener() = try {
        (requireActivity() as AbstractActivity).bindTestService()
    } catch (e: Exception) {
        e.printStackTrace()
        ThirdPartyServices.logException(e)
    }

    override fun onClick(v: View) {
        val descriptor = v.tag as OONIDescriptor<BaseNettest>
        ActivityCompat.startActivity(
            requireActivity(),
            OverviewActivity.newIntent(activity, descriptor),
            null
        )
    }
}