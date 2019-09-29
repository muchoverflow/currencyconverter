package com.adrian.currencyconverter.ui.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemOffsetDecoration : RecyclerView.ItemDecoration {
    private val halfSpacePixelSize: Int

    constructor(spaceInPixelSize: Int) {
        halfSpacePixelSize = spaceInPixelSize / 2
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.paddingStart != halfSpacePixelSize) {
            parent.setPadding(
                halfSpacePixelSize, parent.paddingTop, halfSpacePixelSize,
                if (parent.paddingBottom > halfSpacePixelSize) parent.paddingBottom else halfSpacePixelSize
            )
            parent.clipToPadding = false
        }

        outRect.top = halfSpacePixelSize
        outRect.bottom = halfSpacePixelSize
        outRect.left = halfSpacePixelSize
        outRect.right = halfSpacePixelSize
    }
}