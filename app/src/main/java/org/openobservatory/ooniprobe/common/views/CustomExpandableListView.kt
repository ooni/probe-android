package org.openobservatory.ooniprobe.common.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ExpandableListView

/**
 * This class is needed to allow the ExpandableListView to be placed inside a ScrollView.
 * Without this, the ExpandableListView will not expand to its full size and will not be scrollable.
 */
class CustomExpandableListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ExpandableListView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        )
    }
}