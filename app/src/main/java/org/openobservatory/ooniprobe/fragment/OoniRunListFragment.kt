package org.openobservatory.ooniprobe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnGroupExpandListener
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.OoniRunActivity
import org.openobservatory.ooniprobe.adapters.NettestRecyclerViewAdapter


/**
 * A fragment representing a list of Items.
 */
class OoniRunListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ooni_run_list, container, false)
        val adapter = NettestRecyclerViewAdapter((activity as OoniRunActivity).items)
        // Set the adapter
        if (view is ExpandableListView) {
            view.setOnGroupExpandListener(object : OnGroupExpandListener {
                var previousItem = -1
                override fun onGroupExpand(groupPosition: Int) {
                    if (groupPosition != previousItem) view.collapseGroup(previousItem)
                    previousItem = groupPosition
                }
            })
            with(view) {
                setAdapter(adapter)
                expandGroups(adapter)
                setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                    Snackbar.make(
                        view,
                        "${adapter.groups[groupPosition].name} > ${adapter.groups[groupPosition].inputs[childPosition]}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    true
                }
            }
        }
        return view
    }

    companion object {

        fun ExpandableListView.expandGroups(adapter: ExpandableListAdapter) {
            for (i in 0 until adapter.groupCount) expandGroup(i)
        }

        @JvmStatic
        fun newInstance() = OoniRunListFragment().apply {}
    }
}