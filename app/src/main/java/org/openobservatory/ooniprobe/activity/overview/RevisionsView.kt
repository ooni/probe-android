package org.openobservatory.ooniprobe.activity.overview

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.openobservatory.engine.OONIRunDescriptor
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.activity.reviewdescriptorupdates.DescriptorUpdateFragment
import org.openobservatory.ooniprobe.common.toTestDescriptor
import org.openobservatory.ooniprobe.databinding.FragmentDescriptorUpdateBinding
import org.openobservatory.ooniprobe.databinding.FragmentRevisionsBinding
import org.openobservatory.ooniprobe.databinding.ItemTextBinding
import org.openobservatory.ooniprobe.model.database.TestDescriptor
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale


class RevisionsFragment : Fragment() {

    companion object {

        const val ARG_PREVIOUS_REVISIONS = "previous-revisions"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param previousRevisions Previous revisions in JSON format.
         * @return A new instance of fragment RevisionsFragment.
         */
        @JvmStatic
        fun newInstance(previousRevisions: String) =
            RevisionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PREVIOUS_REVISIONS, previousRevisions)
                }
            }
    }

    private var revisions = emptyList<OONIRunDescriptor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            revisions = Gson().fromJson(
                it.getString(ARG_PREVIOUS_REVISIONS), Array<OONIRunDescriptor>::class.java
            ).toList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRevisionsBinding.inflate(inflater, container, false)

        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
            adapter = RevisionsRecyclerViewAdapter(revisions, object : OnItemClickListener {
                override fun onItemClick(position: Int) {
                    ActivityCompat.startActivity(
                        requireActivity(),
                        RevisionsViewActivity.newIntent(
                            requireContext(),
                            revisions[position].toTestDescriptor()
                        ),
                        null
                    )
                }
            })
        }

        return binding.root
    }

}

/**
 * Interface for handling item clicks in the RecyclerView.
 */
interface OnItemClickListener {
    /**
     * Called when an item in the RecyclerView is clicked.
     *
     * @param position The position of the clicked item.
     */
    fun onItemClick(position: Int)
}

/**
 * RecyclerView adapter for displaying a list of revisions.
 *
 * @param values The list of revisions to display.
 * @param onClickListener The click listener for handling item clicks.
 */
class RevisionsRecyclerViewAdapter(
    private val values: List<OONIRunDescriptor>,
    private val onClickListener: OnItemClickListener,
) : RecyclerView.Adapter<RevisionsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTextBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.binding.root.setPadding(0, 10, 0, 10)
        holder.binding.textView.apply {
            text = SimpleDateFormat(
                "MMMM d, yyyy HH:mm:ss z",
                Locale.ENGLISH
            ).format(item.dateCreated)
            setTextColor(
                ContextCompat.getColor(holder.binding.root.context, R.color.color_blue6)
            )
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener { onClickListener.onItemClick(position) }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(var binding: ItemTextBinding) : RecyclerView.ViewHolder(binding.root)
}

/**
 * Activity for displaying a single revision.
 */
class RevisionsViewActivity : AbstractActivity() {

    companion object {
        private const val ARG_REVISION = "revision"

        /**
         * Create an intent for starting this activity.
         *
         * @param context The context from which to create the intent.
         * @param revision The revision to display.
         * @return The intent for starting this activity.
         */
        fun newIntent(context: Context, revision: TestDescriptor) =
            Intent(context, RevisionsViewActivity::class.java).putExtra(ARG_REVISION, revision)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentDescriptorUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val descriptorExtra = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ARG_REVISION, TestDescriptor::class.java)
        } else {
            intent.getSerializableExtra(ARG_REVISION) as TestDescriptor?
        }

        supportActionBar?.title = descriptorExtra?.dateCreated?.let { date ->
            SimpleDateFormat(
                "MMMM d, yyyy HH:mm:ss z",
                Locale.ENGLISH
            ).format(date)
        }

        descriptorExtra?.let { descriptor ->
            binding.testsLabel.text = "TESTS"
            DescriptorUpdateFragment.bindData(this@RevisionsViewActivity, descriptor, binding)
                .apply {
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
                    window.statusBarColor = color
                }
        }
    }
}