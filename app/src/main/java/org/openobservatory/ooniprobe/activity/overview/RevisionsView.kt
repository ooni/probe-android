package org.openobservatory.ooniprobe.activity.overview

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.openobservatory.engine.OONIRunRevisions
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.databinding.FragmentRevisionsBinding
import org.openobservatory.ooniprobe.databinding.ItemTextBinding


class RevisionsFragment : Fragment() {

    companion object {

        const val ARG_PREVIOUS_REVISIONS = "previous-revisions"
        const val ARG_OONI_RUN_LINK_ID = "oonirun-link-id"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param previousRevisions Previous revisions in JSON format.
         * @return A new instance of fragment RevisionsFragment.
         */
        @JvmStatic
        fun newInstance(runId: Long, previousRevisions: String) =
            RevisionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PREVIOUS_REVISIONS, previousRevisions)
                    putLong(ARG_OONI_RUN_LINK_ID, runId)
                }
            }
    }

    private var revisions: OONIRunRevisions? = null
    private var runId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            revisions =
                Gson().fromJson(it.getString(ARG_PREVIOUS_REVISIONS), OONIRunRevisions::class.java)
            runId = it.getLong(ARG_OONI_RUN_LINK_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRevisionsBinding.inflate(inflater, container, false)

        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
            adapter = revisions?.revisions?.let {
                RevisionsRecyclerViewAdapter(it, object : OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "https://run.test.ooni.org/revisions/%s?revision=%s".format(
                                        runId,
                                        it[position]
                                    )
                                )
                            )
                        )
                    }
                })
            }
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
    private val values: List<String>,
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
            text = "#$item"
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