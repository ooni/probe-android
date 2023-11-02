package org.openobservatory.ooniprobe.activity.runtests

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View.GONE
import android.view.View.VISIBLE
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.NOT_SELECT_ANY
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.SELECT_ALL
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.SELECT_SOME
import org.openobservatory.ooniprobe.activity.runtests.adapter.RunTestsExpandableListViewAdapter
import org.openobservatory.ooniprobe.activity.runtests.models.ChildItem
import org.openobservatory.ooniprobe.activity.runtests.models.GroupItem
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.databinding.ActivityRunTestsBinding
import org.openobservatory.ooniprobe.test.suite.AbstractSuite
import java.io.Serializable
import javax.inject.Inject

class RunTestsActivity : AbstractActivity() {
	lateinit var binding: ActivityRunTestsBinding

	lateinit var mAdapter: RunTestsExpandableListViewAdapter

	@Inject
	lateinit var preferenceManager: PreferenceManager

	@Inject
	lateinit var viewModel: RunTestsViewModel

	companion object {
		const val TESTS: String = "tests"

		@JvmStatic
		fun newIntent(context: Context, testSuites: List<AbstractSuite>): Intent {
			return Intent(context, RunTestsActivity::class.java).putExtras(Bundle().apply {
				putSerializable(TESTS, testSuites as Serializable)
			})
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityRunTestsBinding.inflate(layoutInflater)
		setContentView(binding.getRoot())
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		activityComponent?.inject(this)

		var testSuites: List<AbstractSuite>? = intent.extras?.getParcelableArrayList(TESTS)
		testSuites?.let { ts ->
			val tsGroups: List<GroupItem> = ts.map { testSuite ->
				GroupItem(
					selected = false,
					name = testSuite.name,
					nettests = testSuite.getTestList(preferenceManager).map { nettest ->
						ChildItem(
							selected = preferenceManager.resolveStatus(nettest.name),
							name = nettest.name,
							inputs = nettest.inputs
						)
					})
			}

			mAdapter = RunTestsExpandableListViewAdapter(this, tsGroups, viewModel)

			binding.expandableListView.setAdapter(mAdapter)
			binding.selectAll.setOnClickListener {
				viewModel.selectedAllBtnStatus.value = SELECT_ALL
				mAdapter.notifyDataSetChanged()
				updateStatusIndicator()
			}

			binding.selectNone.setOnClickListener {
				viewModel.selectedAllBtnStatus.value = NOT_SELECT_ANY
				mAdapter.notifyDataSetChanged()
				updateStatusIndicator()
			}

			// TODO(aanorbel) Update button color from theme
			viewModel.selectedAllBtnStatus.observe(this) { selectAllBtnStatus ->
				if (!TextUtils.isEmpty(selectAllBtnStatus)) {
					when (selectAllBtnStatus) {
						SELECT_ALL -> {
							binding.selectNone.isActivated = true
							binding.selectAll.isActivated = false
						}
						NOT_SELECT_ANY -> {

							binding.selectNone.isActivated = true
							binding.selectAll.isActivated = false
						}
						SELECT_SOME -> {
							binding.selectNone.isActivated = true
							binding.selectAll.isActivated = true
						}
					}
					mAdapter.notifyDataSetChanged()
					updateStatusIndicator()
				}
			}
		}

	}

	private fun updateStatusIndicator() {
		binding.bottomBar.setTitle(getString(R.string.OONIRun_URLs, getChildItemsSelectedIdList().size.toString()))
	}

	private fun getChildItemsSelectedIdList(): List<String> {
		val childItemSelectedIdList: MutableList<String> = ArrayList()
		for (i in 0 until mAdapter.groupCount) {
			val secondLevelItemList: List<ChildItem> = mAdapter.getGroup(i).nettests
			secondLevelItemList
				.asSequence()
				.filter { it.selected }
				.mapTo(childItemSelectedIdList) { it.name }
		}
		return childItemSelectedIdList
	}
}
