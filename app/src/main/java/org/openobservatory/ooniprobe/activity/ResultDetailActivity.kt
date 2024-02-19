package org.openobservatory.ooniprobe.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import localhost.toolkit.app.fragment.ConfirmDialogFragment
import localhost.toolkit.app.fragment.ConfirmDialogFragment.OnConfirmedListener
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.adapters.MeasurementGroup
import org.openobservatory.ooniprobe.adapters.ResultDetailExpandableListAdapter
import org.openobservatory.ooniprobe.common.OONIDescriptor
import org.openobservatory.ooniprobe.common.OONITests
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.ResubmitTask
import org.openobservatory.ooniprobe.databinding.ActivityResultDetailBinding
import org.openobservatory.ooniprobe.domain.GetResults
import org.openobservatory.ooniprobe.domain.GetTestSuite
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderDetailFragment
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderPerformanceFragment
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderTBAFragment
import org.openobservatory.ooniprobe.model.database.Measurement
import org.openobservatory.ooniprobe.model.database.Network
import org.openobservatory.ooniprobe.model.database.Result
import java.io.Serializable
import java.util.*
import javax.inject.Inject

/**
 * Activity that displays the details of a result.
 * It is composed of a header [ResultHeaderDetailFragment] and a list of measurements [ResultDetailExpandableListAdapter].
 * The header is composed of several fragments, each one handling a different test group.
 * The list of measurements is composed of several sections, each one handling a different test name.
 * @see [https://github.com/ooni/probe-android/blob/d2dd31b623229e975ee412125b89a4c7c33029c1/app/src/main/java/org/openobservatory/ooniprobe/activity/ResultDetailActivity.java] for the original Java code.
 */
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

    private lateinit var result: Result
    private lateinit var snackbar: Snackbar
    private lateinit var binding: ActivityResultDetailBinding

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
                result.getTestSuite(this@ResultDetailActivity).get()?.themeLight?.let { setTheme(it) }
                binding = ActivityResultDetailBinding.inflate(layoutInflater)
                setContentView(binding.root)
                setSupportActionBar(binding.toolbar)
                supportActionBar?.let { actionBar ->
                    actionBar.setDisplayHomeAsUpEnabled(true)
                    actionBar.setTitle(result.getTestSuite(this@ResultDetailActivity).get().title)
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

                snackbar = Snackbar.make(
                    binding.coordinatorLayout,
                    R.string.Snackbar_ResultsSomeNotUploaded_Text,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.Snackbar_ResultsSomeNotUploaded_UploadAll) { runAsyncTask() }
                load()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.rerun, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        invalidateOptionsMenu()
        if (!Objects.equals(result.test_group_name, OONITests.WEBSITES.label)) {
            menu.findItem(R.id.reRun).setVisible(false)
        }
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

        setThemeFromDescriptor()

        val groupedItemList = mutableListOf<Any>()
        val groupedItems = result.getMeasurementsSorted().groupBy { it.test_name }
        for ((_, itemList) in groupedItems) {
            if (itemList.size == 1) {
                groupedItemList.add(itemList.first())
            } else {
                if (groupedItems.size == 1) {
                    groupedItemList.addAll(itemList)
                } else {
                    groupedItemList.add(
                        MeasurementGroup(
                            title = itemList.first().test.name,
                            measurements = itemList
                        )
                    )
                }

            }
        }

        binding.recyclerView.apply {
            setAdapter(
                ResultDetailExpandableListAdapter(groupedItemList, this@ResultDetailActivity)
            )
        }

        if (Measurement.hasReport(
                this,
                Measurement.selectUploadableWithResultId(result.id)
            )
        ) snackbar.show() else snackbar.dismiss()
    }

    /**
     * Set the theme of the activity from the descriptor of the test suite.
     * The color of the toolbar, the app bar, the tab layout and the status bar will be set to the color of the descriptor.
     */
    private fun setThemeFromDescriptor() {
        result.getDescriptor(this).get().let { desriptor ->
            binding.toolbar.setBackgroundColor(desriptor.color)
            binding.appBar.setBackgroundColor(desriptor.color)
            binding.tabLayout.setBackgroundColor(desriptor.color)
            window.statusBarColor = desriptor.color
        }
    }

    /**
     * Open the [TextActivity] or the [MeasurementDetailActivity] based on the test name of the measurement test name.
     * If the test name is in the list of [OONITests.EXPERIMENTAL.nettests] or [OONITests.EXPERIMENTAL.longRunningTests],
     * the [TextActivity] will be opened otherwise, the [MeasurementDetailActivity] will be opened.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {
        val measurement = v.tag as Measurement

        val textActivityItems = (OONITests.EXPERIMENTAL.nettests).map { it.name }.toMutableList()
        (OONITests.EXPERIMENTAL.longRunningTests)?.map { it.name }
            ?.let { textActivityItems.addAll(it) }

        if (textActivityItems.contains(measurement.test_name)) {
            startActivity(
                TextActivity.newIntent(
                    this,
                    TextActivity.TYPE_JSON,
                    measurement
                )
            )
        } else {
            ActivityCompat.startActivity(
                this,
                MeasurementDetailActivity.newIntent(this, measurement.id),
                null
            )
        }
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

    private class ResubmitAsyncTask(activity: ResultDetailActivity) :
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

    private inner class ResultHeaderAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun createFragment(position: Int): Fragment {
            when (result.test_group_name) {
                OONITests.EXPERIMENTAL.label -> {
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
                    OONITests.WEBSITES.label -> ResultHeaderTBAFragment.newInstance(result)
                    OONITests.INSTANT_MESSAGING.label -> ResultHeaderTBAFragment.newInstance(result)
                    OONITests.PERFORMANCE.label -> ResultHeaderPerformanceFragment.newInstance(
                        result
                    )

                    OONITests.CIRCUMVENTION.label -> ResultHeaderTBAFragment.newInstance(result)
                    else -> ResultHeaderTBAFragment.newInstance(result)
                }
            }
        }

        override fun getItemCount(): Int {
            return if (Objects.equals(
                    result.test_group_name,
                    OONITests.EXPERIMENTAL.label
                )
            ) 2 else 3
        }
    }

}