package it.mindtek.ruah.adapters.dividers

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class GridSpaceItemDecoration(val verticalSpacing: Int, val horizontalSpacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = verticalSpacing / 2
        outRect.bottom = verticalSpacing / 2
        outRect.left = horizontalSpacing / 2
        outRect.right = horizontalSpacing / 2
    }
}