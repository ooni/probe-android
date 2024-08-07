package org.openobservatory.ooniprobe.activity.reviewdescriptorupdates

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.common.AppUpdatesViewModel
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import org.openobservatory.ooniprobe.databinding.ActivityReviewDescriptorUpdatesBinding
import org.openobservatory.ooniprobe.databinding.FragmentDescriptorUpdateBinding
import org.openobservatory.ooniprobe.model.database.ITestDescriptor
import org.openobservatory.ooniprobe.model.database.InstalledDescriptor
import org.openobservatory.ooniprobe.model.database.TestDescriptor
import org.openobservatory.ooniprobe.test.test.AbstractTest
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

/**
 * This activity is used to review the updates of the descriptors.
 * When a new update is available, the user is prompted to review the changes.
 * This activity is started by the [org.openobservatory.ooniprobe.activity.MainActivity] activity.
 */
class ReviewDescriptorUpdatesActivity : AbstractActivity() {

    companion object {
        private const val DESCRIPTORS = "descriptors"
        private const val PERSISTENT = "persistent"

        @JvmField
        var RESULT_MESSAGE = "result"

        /**
         * This method is used to create an intent to start this activity.
         * @param context is the context of the activity that calls this method
         * @param descriptors is the descriptors to review
         * @return an intent to start this activity
         */
        @JvmStatic
        fun newIntent(context: Context, descriptors: String?): Intent {
            return Intent(context, ReviewDescriptorUpdatesActivity::class.java).putExtra(
                    DESCRIPTORS,
                    descriptors
            )
        }

        @JvmStatic
        fun newIntent(context: Context, descriptors: String?, persistent: Boolean): Intent {
            return Intent(context, ReviewDescriptorUpdatesActivity::class.java).apply {
                putExtra(DESCRIPTORS, descriptors)
                putExtra(PERSISTENT, persistent)
            }
        }
    }

    @Inject
    lateinit var descriptorManager: TestDescriptorManager

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var updatesViewModel: AppUpdatesViewModel

    private lateinit var reviewUpdatesPagingAdapter: ReviewUpdatesPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        val binding = ActivityReviewDescriptorUpdatesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = getString(R.string.Dashboard_ReviewDescriptor_Title)
        val descriptorJson = intent.getStringExtra(DESCRIPTORS)
        try {
            /**
             * **[descriptorJson]** is the json string of the intent.
             * **[descriptors]** is the list of [TestDescriptor] objects obtained from **[descriptorJson]**.
             * Because [TestDescriptor.nettests] is of type [Any], the gson library converts it to a [LinkedTreeMap].
             */
            val descriptors: List<TestDescriptor> =
                    gson.fromJson(descriptorJson, Array<ITestDescriptor>::class.java)
                            .map { it.toTestDescriptor() }

            // Disable swipe behavior of viewpager
            binding.viewpager.isUserInputEnabled = false

            reviewUpdatesPagingAdapter = ReviewUpdatesPagingAdapter(this, descriptors)
            binding.viewpager.adapter = reviewUpdatesPagingAdapter

            binding.btnUpdate.setOnClickListener {
                /*
                 * When the user clicks the update button, the descriptor is updated.
                 * If the current item is the last item in the viewpager, the result is set to "Link(s) updated" and the activity is finished.
                 * If the current item is not the last item in the viewpager, the viewpager is swiped to the next item.
                 */
                with(binding.viewpager) {
                    descriptorManager.updateFromNetwork(descriptors[currentItem])
                    when (currentItem) {
                        adapter?.itemCount?.minus(1) -> {
                            // Last update
                            setResult(
                                    RESULT_OK,
                                    Intent().putExtra(RESULT_MESSAGE, getString(R.string.Dashboard_ReviewDescriptor_Success))
                            )
                            finish()
                        }

                        else -> {
                            // Not the last update
                            currentItem += 1
                        }
                    }
                }
            }

            binding.viewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    /*
                     * When the user swipes to the next item in the viewpager, the title of the action bar is updated.
                     * If the current item is the last item in the viewpager, the text of the button is updated to "UPDATE AND FINISH".
                     * If the current item is not the last item in the viewpager, the text of the button is updated to "UPDATE".
                     */

                    val total = position + 1
                    val itemCount = binding.viewpager.adapter?.itemCount
                    supportActionBar?.title = getString(R.string.Dashboard_ReviewDescriptor_Label, total, itemCount)

                    binding.btnUpdate.text = when (position + 1) {
                        binding.viewpager.adapter?.itemCount -> getString(R.string.Dashboard_ReviewDescriptor_Button_Last, total, itemCount)

                        else -> getString(R.string.Dashboard_ReviewDescriptor_Button_Default, total, itemCount)
                    }
                }
            })

        } catch (e: Exception) {
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
                if (intent.getBooleanExtra(PERSISTENT, false)) {
                    intent.getStringExtra(DESCRIPTORS)?.let { updatesViewModel.setDescriptorsWith(it) }
                }
                onSupportNavigateUp()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

/**
 * This adapter is used to display the list of descriptors in the viewpager.
 * @param fragmentActivity is the activity that contains the viewpager.
 * @param descriptors is the list of descriptors to display.
 */
class ReviewUpdatesPagingAdapter(
        fragmentActivity: FragmentActivity,
        private val descriptors: List<TestDescriptor>
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = descriptors.size

    override fun createFragment(position: Int): Fragment {
        val fragment = DescriptorUpdateFragment()
        fragment.arguments = Bundle().apply {
            putSerializable(DESCRIPTOR, descriptors[position])
        }
        return fragment
    }
}

private const val DESCRIPTOR = "descriptor"

/**
 * This fragment is used to display the details of a descriptor.
 * It is used by [ReviewUpdatesPagingAdapter].
 * @param descriptor is the descriptor to display.
 */
class DescriptorUpdateFragment : Fragment() {

    companion object {

        @JvmStatic
        fun bindData(context: Context, descriptor: TestDescriptor, binding: FragmentDescriptorUpdateBinding): InstalledDescriptor {
            val absDescriptor = InstalledDescriptor(descriptor)
            binding.apply {
                title.text = absDescriptor.title
                author.text = context.getString(R.string.Dashboard_Runv2_Overview_Description,descriptor.author,SimpleDateFormat(
                    "MMM dd, yyyy",
                    Locale.getDefault()
                ).format(descriptor.dateCreated),"")
                description.text = absDescriptor.description // Use markdown
                icon.setImageResource(absDescriptor.getDisplayIcon(context))
                val adapter =
                        ReviewDescriptorExpandableListAdapter(nettests = absDescriptor.nettests)
                expandableListView.setAdapter(adapter)
                // Expand all groups
                for (i in 0 until adapter.groupCount) {
                    expandableListView.expandGroup(i)
                }
            }
            return absDescriptor
        }
    }

    private lateinit var binding: FragmentDescriptorUpdateBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentDescriptorUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(DESCRIPTOR) }?.apply {
            val descriptor: TestDescriptor =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        getSerializable(DESCRIPTOR, TestDescriptor::class.java)!!
                    } else {
                        getSerializable(DESCRIPTOR) as TestDescriptor
                    }
            bindData(requireContext(), descriptor, binding)
        }
    }
}

/**
 * This adapter is used to display the list of nettests in the expandable list view.
 * It is used by [DescriptorUpdateFragment] to display the list of nettests.
 * @param nettests is the list of nettests to display.
 */
class ReviewDescriptorExpandableListAdapter(
        val nettests: List<BaseNettest>,
) : BaseExpandableListAdapter() {

    /**
     * @return Number of groups in the list.
     */
    override fun getGroupCount(): Int = nettests.size

    /**
     * @param groupPosition Position of the group in the list.
     * @return Number of children in the group.
     */
    override fun getChildrenCount(groupPosition: Int): Int =
            nettests[groupPosition].inputs?.size ?: 0

    /**
     * @param groupPosition Position of the group in the list.
     * @return [BaseNettest] object.
     */
    override fun getGroup(groupPosition: Int): BaseNettest = nettests[groupPosition]

    /**
     * @param groupPosition Position of the group in the list.
     * @param childPosition Position of the child in the group.
     * @return string item at position.
     */
    override fun getChild(groupPosition: Int, childPosition: Int): String? =
            nettests[groupPosition].inputs?.get(childPosition)

    /**
     * @param groupPosition Position of the group in the list.
     * @return Group position.
     */
    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    /**
     * @param groupPosition Position of the group in the list.
     * @param childPosition Position of the child in the group.
     * @return Child position.
     */
    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    /**
     * @return true if the same ID always refers to the same object.
     */
    override fun hasStableIds(): Boolean = false

    /**
     * @param groupPosition Position of the group in the list.
     * @param isExpanded true if the group is expanded.
     * @param convertView View of the group.
     * @param parent Parent view.
     * @return View of the group.
     */
    override fun getGroupView(
            groupPosition: Int,
            isExpanded: Boolean,
            convertView: View?,
            parent: ViewGroup,
    ): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.nettest_group_list_item, parent, false)
        val groupItem = getGroup(groupPosition)
        val groupIndicator = view.findViewById<ImageView>(R.id.group_indicator)

        val abstractNettest = AbstractTest.getTestByName(groupItem.name)
        view.findViewById<TextView>(R.id.group_name).text =
                when (abstractNettest.labelResId == R.string.Test_Experimental_Fullname) {
                    true -> groupItem.name
                    false -> parent.context.resources.getText(abstractNettest.labelResId)
                }

        val groupCheckBox = view.findViewById<ImageView>(R.id.groupCheckBox)
        groupCheckBox.visibility = View.GONE
        if (groupItem.inputs?.isNotEmpty() == true) {
            if (isExpanded) {
                groupIndicator.setImageResource(R.drawable.expand_less)
            } else {
                groupIndicator.setImageResource(R.drawable.expand_more)
            }
        } else {
            groupIndicator.visibility = View.INVISIBLE
        }

        return view
    }

    /**
     * @param groupPosition Position of the group in the list.
     * @param childPosition Position of the child in the group.
     * @param isLastChild True if the child is the last child in the group.
     * @param convertView View object.
     * @param parent ViewGroup object.
     * @return View object.
     */
    override fun getChildView(
            groupPosition: Int,
            childPosition: Int,
            isLastChild: Boolean,
            convertView: View?,
            parent: ViewGroup
    ): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.nettest_child_list_item, parent, false)

        view.findViewById<TextView>(R.id.text).apply {
            text = getChild(groupPosition, childPosition)
        }
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = false

}