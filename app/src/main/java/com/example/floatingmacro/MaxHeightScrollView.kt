package com.example.floatingmacro

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class MaxHeightScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ScrollView(context, attrs) {

    var maxHeightPx: Int = Int.MAX_VALUE

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val spec = if (maxHeightPx != Int.MAX_VALUE)
            MeasureSpec.makeMeasureSpec(maxHeightPx, MeasureSpec.AT_MOST) else heightSpec
        super.onMeasure(widthSpec, spec)
    }
}
