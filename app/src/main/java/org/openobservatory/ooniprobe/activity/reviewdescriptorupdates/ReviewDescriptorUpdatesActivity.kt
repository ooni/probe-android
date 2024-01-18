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
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.gson.Gson
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import org.openobservatory.ooniprobe.databinding.ActivityReviewDescriptorUpdatesBinding
import org.openobservatory.ooniprobe.databinding.FragmentDescriptorUpdateBinding
import org.openobservatory.ooniprobe.model.database.InstalledDescriptor
import org.openobservatory.ooniprobe.model.database.TestDescriptor
import javax.inject.Inject

class ReviewDescriptorUpdatesActivity : AbstractActivity() {

    companion object {
        private const val DESCRIPTORS = "descriptors"

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
    }

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var descriptorManager: TestDescriptorManager

    @Inject
    lateinit var gson: Gson

    private lateinit var reviewUpdatesPagingAdapter: ReviewUpdatesPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        val binding = ActivityReviewDescriptorUpdatesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = "Link Update"
        val descriptorJson = intent.getStringExtra(DESCRIPTORS)
        try {
            val descriptors: Array<TestDescriptor> =
                gson.fromJson(descriptorJson, Array<TestDescriptor>::class.java)

            binding.viewpager.isUserInputEnabled = false
            reviewUpdatesPagingAdapter = ReviewUpdatesPagingAdapter(this, descriptors.toList())
            binding.viewpager.adapter = reviewUpdatesPagingAdapter


            val bottomBarOnMenuItemClickListener: Toolbar.OnMenuItemClickListener =
                Toolbar.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.update_descriptor -> {
                            val currPos: Int = binding.viewpager.currentItem
                            if ((currPos + 1) != binding.viewpager.adapter?.itemCount) {
                                binding.viewpager.currentItem = currPos + 1
                            } else {
                                /*preferenceManager.setLastUpdateDescriptorReview(
                                    descriptorManager.getDescriptorVersion()
                                )*/
                                finish()
                            }
                            true
                        }

                        else -> false
                    }
                }
            binding.bottomBar.setOnMenuItemClickListener(bottomBarOnMenuItemClickListener)

            binding.viewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.bottomBar.menu.findItem(R.id.update_descriptor)
                        ?.setTitle("UPDATE (${position + 1} of ${binding.viewpager.adapter?.itemCount})")

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
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

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

class DescriptorUpdateFragment : Fragment() {

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
            val absDescriptor = InstalledDescriptor(descriptor)
            binding.apply {
                title.text = absDescriptor.title
                description.text = absDescriptor.description // Use markdown
                icon.setImageResource(absDescriptor.getDisplayIcon(requireContext()))
            }
        }
    }
}
