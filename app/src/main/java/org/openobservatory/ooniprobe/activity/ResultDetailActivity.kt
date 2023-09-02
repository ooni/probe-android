package org.openobservatory.ooniprobe.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import localhost.toolkit.app.fragment.ConfirmDialogFragment
import localhost.toolkit.app.fragment.ConfirmDialogFragment.OnConfirmedListener
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerAdapter
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.ResubmitTask
import org.openobservatory.ooniprobe.databinding.ActivityResultDetailBinding
import org.openobservatory.ooniprobe.domain.GetResults
import org.openobservatory.ooniprobe.domain.GetTestSuite
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderDetailFragment
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderMiddleboxFragment
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderPerformanceFragment
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderTBAFragment
import org.openobservatory.ooniprobe.item.MeasurementItem
import org.openobservatory.ooniprobe.item.MeasurementPerfItem
import org.openobservatory.ooniprobe.model.database.Measurement
import org.openobservatory.ooniprobe.model.database.Network
import org.openobservatory.ooniprobe.model.database.Result
import org.openobservatory.ooniprobe.test.suite.*
import java.io.Serializable
import javax.inject.Inject

class ResultDetailActivity : AbstractActivity(), View.OnClickListener, OnConfirmedListener {

    companion object {
        private const val ID = "id"
        private const val UPLOAD_KEY = "upload"
        private const val RERUN_KEY = "rerun"

        @JvmStatic
        fun newIntent(context: Context?, id: Int): Intent {
            return Intent(context, ResultDetailActivity::class.java).putExtra(ID, id)
        }
    }

    private var items: ArrayList<HeterogeneousRecyclerItem<*, *>>? = null
    private var adapter: HeterogeneousRecyclerAdapter<HeterogeneousRecyclerItem<*, *>>? = null
    private lateinit var result: Result
    private lateinit var snackbar: Snackbar

    @Inject
    lateinit var getTestSuite: GetTestSuite

    @Inject
    lateinit var getResults: GetResults

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        when (val iResult = getResults[intent.getIntExtra(ID, 0)]) {
            null -> {
                /**
                 * Close the activity if the result is not found. This should never happen.
                 * Previous use of 'assert' closed the entire app.
                 */
                finish()
                return
            }

            else -> {
                result = iResult
                setTheme(result.testSuite.themeLight)
                val binding = ActivityResultDetailBinding.inflate(layoutInflater)
                setContentView(binding.root)
                setSupportActionBar(binding.toolbar)
                supportActionBar?.let { actionBar ->
                    actionBar.setDisplayHomeAsUpEnabled(true)
                    actionBar.setTitle(result.testSuite.title)
                }
                binding.pager.apply {
                    setAdapter(ResultHeaderAdapter(this@ResultDetailActivity))
                    TabLayoutMediator(binding.tabLayout, this)
                    { tab: TabLayout.Tab, _: Int -> tab.setText("●") }.attach()
                }
                result.apply {
                    is_viewed = true
                    save()
                }
                items = ArrayList()
                adapter = HeterogeneousRecyclerAdapter(this, items)
                binding.recyclerView.apply {
                    val layoutManager = LinearLayoutManager(this@ResultDetailActivity)
                    setLayoutManager(layoutManager)
                    addItemDecoration(DividerItemDecoration(this@ResultDetailActivity, layoutManager.orientation))
                    setAdapter(this@ResultDetailActivity.adapter)
                }
                snackbar = Snackbar.make(
                    binding.coordinatorLayout,
                    R.string.Snackbar_ResultsSomeNotUploaded_Text,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.Snackbar_ResultsSomeNotUploaded_UploadAll) { runAsyncTask() }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.rerun, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        invalidateOptionsMenu()
        if (result.test_group_name != WebsitesSuite.NAME) menu.findItem(R.id.reRun).setVisible(false)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reRun -> {
                ConfirmDialogFragment.Builder()
                    .withExtra(RERUN_KEY)
                    .withMessage(
                        getString(
                            R.string.Modal_ReRun_Websites_Title,
                            result.getMeasurements().size.toString()
                        )
                    )
                    .withPositiveButton(getString(R.string.Modal_ReRun_Websites_Run))
                    .build().show(supportFragmentManager, null)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun reTestWebsites() {
        RunningActivity.runAsForegroundService(
            this,
            getTestSuite.getFrom(result).asArray(),
            { finish() },
            preferenceManager
        )
    }

    private fun runAsyncTask() {
        ResubmitAsyncTask(this).execute(result.id, null)
    }

    private fun load() {
        result = getResults[result.id]
        val isPerf = result.test_group_name == PerformanceSuite.NAME
        items?.clear()
        result.getMeasurementsSorted().let { measurements ->
            for (measurement in measurements) items!!.add(
                if (isPerf && !measurement.is_failed) MeasurementPerfItem(
                    measurement,
                    this
                ) else MeasurementItem(measurement, this)
            )
        }

        adapter?.notifyTypesChanged()
        if (Measurement.hasReport(
                this,
                Measurement.selectUploadableWithResultId(result.id)
            )
        ) snackbar.show() else snackbar.dismiss()
    }

    override fun onClick(v: View) {
        val measurement = v.tag as Measurement
        if (result.test_group_name == ExperimentalSuite.NAME) startActivity(
            TextActivity.newIntent(
                this,
                TextActivity.TYPE_JSON,
                measurement
            )
        ) else ActivityCompat.startActivity(this, MeasurementDetailActivity.newIntent(this, measurement.id), null)
    }

    override fun onConfirmation(extra: Serializable, buttonClicked: Int) {
        if (buttonClicked == DialogInterface.BUTTON_POSITIVE && extra == UPLOAD_KEY) {
            runAsyncTask()
        } else if (buttonClicked == DialogInterface.BUTTON_POSITIVE && extra == RERUN_KEY) {
            reTestWebsites()
        } else if (buttonClicked == DialogInterface.BUTTON_NEUTRAL) {
            startActivity(
                TextActivity.newIntent(this, TextActivity.TYPE_UPLOAD_LOG, extra as String)
            )
        }
    }

    private class ResubmitAsyncTask internal constructor(activity: ResultDetailActivity) :
        ResubmitTask<ResultDetailActivity?>(activity, activity.preferenceManager.proxyURL) {
        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if (getActivity() != null) {
                getActivity()!!.result = d.getResults[getActivity()!!.result.id]
                getActivity()!!.load()
                if (!result) ConfirmDialogFragment.Builder()
                    .withExtra(UPLOAD_KEY)
                    .withTitle(getActivity()!!.getString(R.string.Modal_UploadFailed_Title))
                    .withMessage(
                        getActivity()!!.getString(
                            R.string.Modal_UploadFailed_Paragraph,
                            errors.toString(),
                            totUploads.toString()
                        )
                    )
                    .withPositiveButton(getActivity()!!.getString(R.string.Modal_Retry))
                    .withNeutralButton(getActivity()!!.getString(R.string.Modal_DisplayFailureLog))
                    .withExtra(java.lang.String.join("\n", logger.logs))
                    .build().show(getActivity()!!.supportFragmentManager, null)
            }
        }
    }

    private inner class ResultHeaderAdapter internal constructor(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun createFragment(position: Int): Fragment {
            when (result.test_group_name) {
                ExperimentalSuite.NAME -> {
                    when (position) {
                        0 -> return ResultHeaderDetailFragment.newInstance(
                            false,
                            result.formattedDataUsageUp,
                            result.formattedDataUsageDown,
                            result.start_time,
                            result.getRuntime(),
                            true,
                            null,
                            null
                        )

                        1 -> return ResultHeaderDetailFragment.newInstance(
                            false,
                            null,
                            null,
                            null,
                            null,
                            null,
                            Network.getCountry(this@ResultDetailActivity, result.network),
                            result.network
                        )
                    }
                }
            }
            return when (position) {
                1 -> ResultHeaderDetailFragment.newInstance(
                    false,
                    result.formattedDataUsageUp,
                    result.formattedDataUsageDown,
                    result.start_time,
                    result.getRuntime(),
                    true,
                    null,
                    null
                )

                2 -> ResultHeaderDetailFragment.newInstance(
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Network.getCountry(this@ResultDetailActivity, result.network),
                    result.network
                )

                else -> when (result.test_group_name) {
                    WebsitesSuite.NAME -> ResultHeaderTBAFragment.newInstance(result)
                    InstantMessagingSuite.NAME -> ResultHeaderTBAFragment.newInstance(result)
                    MiddleBoxesSuite.NAME -> ResultHeaderMiddleboxFragment.newInstance(result.countAnomalousMeasurements() > 0)
                    PerformanceSuite.NAME -> ResultHeaderPerformanceFragment.newInstance(result)
                    CircumventionSuite.NAME -> ResultHeaderTBAFragment.newInstance(result)
                    else -> ResultHeaderTBAFragment.newInstance(result)
                }
            }
        }

        override fun getItemCount(): Int {
            return if (result.test_group_name == ExperimentalSuite.NAME) 2 else 3
        }
    }

}
