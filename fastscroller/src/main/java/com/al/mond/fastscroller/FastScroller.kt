package com.al.mond.fastscroller

import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

/**
 * Fast scroller simple version.
 *
 * @author mond.al
 * @property scrollHandleView
 */

class FastScroller(private val scrollHandleView: View) {
    private var isMovedByHandleDrag = false

    /**
     * Add to Recyclerview's parent with [scrollHandleView].
     * And bind to [recyclerview] scroll position and [scrollHandleView] position.
     *
     * @param recyclerview
     */
    fun attachTo(recyclerview: RecyclerView) {
        addScrollerView(recyclerview)
        initHandlerPositionChangedListener(recyclerview)
        initUpdateHandlePositionListener(recyclerview)
    }

    private fun addScrollerView(rv: RecyclerView) {
        val fastScrollerView = FrameLayout(rv.context)
        val handleLayoutParams = FrameLayout.LayoutParams(scrollHandleView.layoutParams)
        handleLayoutParams.gravity = Gravity.END
        fastScrollerView.addView(scrollHandleView, handleLayoutParams)
        if (rv.layoutParams is ViewGroup.MarginLayoutParams) {
            (rv.parent as ViewGroup).addView(fastScrollerView, ViewGroup.MarginLayoutParams(rv.layoutParams))
        } else {
            throw IllegalArgumentException("The parent layout is not support type.")
        }
    }

    private fun initHandlerPositionChangedListener(recyclerView: RecyclerView) {
        scrollHandleView.setOnTouchListener(object : View.OnTouchListener {
            private var mDownX: Float = 0.toFloat()
            private var mDownY: Float = 0.toFloat()
            private var mDownXInHandleView: Float = 0.toFloat()
            private var mDownYInHandleView: Float = 0.toFloat()
            override fun onTouch(handleView: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isMovedByHandleDrag = true
                        mDownY = event.rawY
                        mDownYInHandleView = event.y
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (isMovedByHandleDrag.not()) return false
                        val itemCount = recyclerView.adapter?.itemCount ?: 0
                        val percent = calculateHandlePercentBy(handleView, event)
                        val adapterPosition = getAdapterPosition(percent, itemCount)
                        updateHandlePosition(percent)
                        updateRecyclerViewPosition(adapterPosition)
                        return true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isMovedByHandleDrag = false
                        mDownX = 0f
                        mDownY = 0f
                        mDownXInHandleView = 0f
                        mDownYInHandleView = 0f
                        handleView.performClick()
                        return true
                    }
                    else -> return false
                }
            }

            private fun updateRecyclerViewPosition(adapterPosition: Int) {
                recyclerView.scrollToPosition(adapterPosition)
            }

            private fun getAdapterPosition(relativePos: Float, itemCount: Int) = (relativePos * itemCount).getInMinMaX(0, itemCount - 1).toInt()

            private fun calculateHandlePercentBy(handle: View, event: MotionEvent): Float {
                val rawY: Float = event.rawY - (handle.getViewRawY() + mDownYInHandleView)
                return rawY / (recyclerView.height - handle.height)
            }
        })
    }

    private fun initUpdateHandlePositionListener(rv: RecyclerView) {
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (isMovedByHandleDrag) return
                updateHandlePosition(getPercent(rv))
            }

            private fun getPercent(rv: RecyclerView): Float {
                val offset: Int = rv.computeVerticalScrollOffset()
                val extent: Int = rv.computeVerticalScrollExtent()
                val range: Int = rv.computeVerticalScrollRange()
                return (offset / (range - extent).toFloat())
            }
        })
    }

    private fun updateHandlePosition(percent: Float) {
        scrollHandleView.y = getHandlerY(percent)
    }

    private fun getHandlerY(percent: Float): Float {
        val scrollbarHeight = (scrollHandleView.parent as View).height - scrollHandleView.height
        return (percent * scrollbarHeight).getInMinMaX(0f, scrollbarHeight)
    }
}

private fun View.getViewRawY(): Float {
    val location = IntArray(2)
    location[0] = 0
    location[1] = y.toInt()
    (parent as View).getLocationInWindow(location)
    return location[1].toFloat()
}

private fun Number.getInMinMaX(min: Number, max: Number): Float {
    val minimum = min.toFloat().coerceAtLeast(this.toFloat())
    return minimum.coerceAtMost(max.toFloat())
}

fun Number.dp2px() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
).toInt()
