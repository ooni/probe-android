package org.openobservatory.ooniprobe.activity.runtests

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.activity.RunningActivity
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.SELECT_ALL
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.SELECT_NONE
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.SELECT_SOME
import org.openobservatory.ooniprobe.activity.runtests.adapter.RunTestsExpandableListViewAdapter
import org.openobservatory.ooniprobe.activity.runtests.models.ChildItem
import org.openobservatory.ooniprobe.activity.runtests.models.GroupItem
import org.openobservatory.ooniprobe.common.OONIDescriptor
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.databinding.ActivityRunTestsBinding
import java.io.Serializable
import javax.inject.Inject

class RunTestsActivity : AbstractActivity() {
	lateinit var binding: ActivityRunTestsBinding

	private lateinit var adapter: RunTestsExpandableListViewAdapter

	@Inject
	lateinit var preferenceManager: PreferenceManager

	@Inject
	lateinit var viewModel: RunTestsViewModel

	companion object {
		const val TESTS: String = "tests"

        @JvmStatic
        fun newIntent(context: Context, testSuites: List<OONIDescriptor<BaseNettest>>): Intent {
            return Intent(context, RunTestsActivity::class.java).putExtras(Bundle().apply {
                putSerializable(TESTS, testSuites as Serializable)
            })
        }
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityRunTestsBinding.inflate(layoutInflater)
		setContentView(binding.getRoot())

		setSupportActionBar(binding.toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		activityComponent?.inject(this)

		val descriptors: List<OONIDescriptor<BaseNettest>>? =
			intent.extras?.getSerializable(TESTS) as List<OONIDescriptor<BaseNettest>>?
		descriptors?.let { _descriptors ->

			adapter = RunTestsExpandableListViewAdapter(
				_descriptors.map { descriptor ->
					descriptor.toRunTestsGroupItem(preferenceManager = preferenceManager)
				},
				viewModel
			)
			binding.expandableListView.setAdapter(adapter)
			for (i in 0 until adapter.groupCount) {
				binding.expandableListView.expandGroup(i)
			}
			binding.selectAll.setOnClickListener { onSelectAllClickListener() }

			binding.selectNone.setOnClickListener { onSelectNoneClickListener() }

			viewModel.selectedAllBtnStatus.observe(this, this::selectAllBtnStatusObserver)

			binding.bottomBar.setOnMenuItemClickListener { menuItem ->
				onMenuItemClickListener(menuItem)
			}
		} ?: run {
			finish()
		}

	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		val inflater: MenuInflater = menuInflater
		inflater.inflate(R.menu.close, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.close_button -> {
				finish()
				true
			}

			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun onMenuItemClickListener(menuItem: MenuItem): Boolean {
		return when (menuItem.itemId) {
			R.id.runButton -> {
				val selectedChildItems: List<String> = getChildItemsSelectedIdList()
				if (selectedChildItems.isNotEmpty()) {
					val testSuitesToRun = getGroupItemsAtLeastOneChildEnabled().map { groupItem ->
						return@map groupItem.getTest(this)
					}
					RunningActivity.runAsForegroundService(
						this@RunTestsActivity,
						java.util.ArrayList(testSuitesToRun),
						{ finish() },
						preferenceManager
					)

				}
				true
			}

			else -> false
		}
	}

	private fun selectAllBtnStatusObserver(selectAllBtnStatus: String?) {
		if (!TextUtils.isEmpty(selectAllBtnStatus)) {
			when (selectAllBtnStatus) {
				SELECT_ALL -> {
					binding.selectNone.isActivated = true
					binding.selectAll.isActivated = false
				}

				SELECT_NONE -> {
					binding.selectNone.isActivated = true
					binding.selectAll.isActivated = false
				}

				SELECT_SOME -> {
					binding.selectNone.isActivated = true
					binding.selectAll.isActivated = true
				}
			}
			adapter.notifyDataSetChanged()
			updateStatusIndicator()
		}
	}

	private fun onSelectNoneClickListener() {
		viewModel.setSelectedAllBtnStatus(SELECT_NONE)
		adapter.notifyDataSetChanged()
		updateStatusIndicator()
	}

	private fun onSelectAllClickListener() {
		viewModel.setSelectedAllBtnStatus(SELECT_ALL)
		adapter.notifyDataSetChanged()
		updateStatusIndicator()
	}


	private fun updateStatusIndicator() {
		//TODO(aanorbel): translate status indicator
		binding.bottomBar.setTitle("${getChildItemsSelectedIdList().size} Tests")
	}

	private fun getChildItemsSelectedIdList(): List<String> {
		val childItemSelectedIdList: MutableList<String> = ArrayList()
		for (i in 0 until adapter.groupCount) {
			val secondLevelItemList: List<ChildItem> = adapter.getGroup(i).nettests
			secondLevelItemList
				.asSequence()
				.filter { it.selected }
				.mapTo(childItemSelectedIdList) { it.name }
		}
		return childItemSelectedIdList
	}

	private fun getGroupItemsAtLeastOneChildEnabled(): List<GroupItem> {
		val items: MutableList<GroupItem> = ArrayList()
		for (i in 0 until adapter.groupCount) {
			if (adapter.getGroup(i).nettests.any { it.selected }) {
				items.add(adapter.getGroup(i).apply {
					nettests = nettests.filter { it.selected }
				})
			}
		}
		return items
	}
}
