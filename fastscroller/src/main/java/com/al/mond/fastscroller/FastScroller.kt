package com.al.mond.fastscroller

import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random


/**
 * Fast scroller simple version.
 *
 * @author mond.al
 * @property handleView
 * @param fadeOutDuration hide animation duration time (ms)
 * @param hideDelayMillis hide animation delay time (ms)
 */

class FastScroller(
    private val handleView: View,
    private val bubbleListener: BubbleListener? = null,
    private val fadeOutDuration: Long = 300.toLong(),
    private val hideDelayMillis: Long = 1000.toLong(),
) {
    private lateinit var listView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager

    private var isMovedByHandleDrag = false
    private var runnable = Runnable { }

    private var listViewAdapter: BubbleAdapter? = null

    fun bind(recyclerView: RecyclerView) {
        if (recyclerView.layoutManager !is LinearLayoutManager)
            throw IllegalArgumentException("recyclerView's layoutManager is not LinearLayoutManager")
        listView = recyclerView
        layoutManager = recyclerView.layoutManager as LinearLayoutManager
        if (bubbleListener != null && recyclerView.adapter is BubbleAdapter) {
            bubbleListener.setVisible(false)
            listViewAdapter = recyclerView.adapter as BubbleAdapter
        }
        initHandlerPositionChangedListener()
        initUpdateHandlePositionListener()
    }

    private fun initHandlerPositionChangedListener() {
        handleView.setOnTouchListener(object : View.OnTouchListener {
            private var mDownX: Float = 0.toFloat()
            private var mDownY: Float = 0.toFloat()
            private var mDownXInHandleView: Float = 0.toFloat()
            private var mDownYInHandleView: Float = 0.toFloat()
            override fun onTouch(handleView: View, event: MotionEvent): Boolean {
                if (handleView.alpha == 0f)
                    return true
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isMovedByHandleDrag = true
                        mDownY = event.rawY
                        mDownYInHandleView = event.y
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (isMovedByHandleDrag.not()) return false
                        val itemCount = listView.adapter?.itemCount ?: 0
                        val percent = calculateHandlePercentBy(handleView, event)
                        val adapterPosition = getAdapterPosition(percent, itemCount)
                        updateHandlePosition(percent)
                        updateRecyclerViewPosition(adapterPosition)
                        updateBubble(adapterPosition)
                        return true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isMovedByHandleDrag = false
                        mDownX = 0f
                        mDownY = 0f
                        mDownXInHandleView = 0f
                        mDownYInHandleView = 0f
                        handleView.performClick()
                        bubbleListener?.setVisible(false)
                        return true
                    }
                    else -> return false
                }
            }

            private fun updateBubble(adapterPosition: Int) {
                if (bubbleListener != null && listViewAdapter != null) {
                    bubbleListener.setVisible(true)
                    bubbleListener.setBubble(listViewAdapter!!.getBubbleItem(adapterPosition))
                }
            }

            private fun updateRecyclerViewPosition(adapterPosition: Int) {
                // Trick! by a thumb. you have a big finger! :~)
                if (listView.adapter?.itemCount!! > 300) {
                    layoutManager.scrollToPositionWithOffset(
                        adapterPosition,
                        Random.nextInt(handleView.height)
                    )
                } else {
                    layoutManager.scrollToPosition(adapterPosition)
                }
            }

            private fun getAdapterPosition(relativePos: Float, itemCount: Int) =
                (relativePos * itemCount).getInMinMaX(0, itemCount - 1).toInt()

            private fun calculateHandlePercentBy(handle: View, event: MotionEvent): Float {
                val rawY: Float = event.rawY - (handle.getViewRawY() + mDownYInHandleView)
                return rawY / (listView.height - handle.height)
            }
        })
    }

    private fun initUpdateHandlePositionListener() {
        listView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        setHandleView(true)
        val handlerY = getHandlerY(percent)
        handleView.y = handlerY
        bubbleListener?.setViewY(handlerY)
    }

    private fun setHandleView(visible: Boolean) {
        if (visible || isMovedByHandleDrag) {
            handleView.clearAnimation()
            if (handleView.alpha != 1f)
                handleView.alpha = 1f
            handleView.removeCallbacks(runnable)                // clear running callback
            runnable = Runnable { setHandleView(false) }        // set new callback
            handleView.postDelayed(runnable, hideDelayMillis)   // post delayed call
        } else {
            val anim = AlphaAnimation(1f, 0f).apply {
                duration = fadeOutDuration
                fillAfter = true
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        if (isMovedByHandleDrag)
                            handleView.alpha = 1f // cancel
                        else
                            handleView.alpha = 0f // end
                    }

                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }
            handleView.startAnimation(anim)
        }
    }

    private fun getHandlerY(percent: Float): Float {
        val scrollbarHeight = (handleView.parent as View).height - handleView.height
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


interface BubbleAdapter {
    fun getBubbleItem(adapterPosition: Int): String
}

interface BubbleListener {
    fun setBubble(str: String)
    fun setViewY(y: Float)
    fun setVisible(isVisible: Boolean)
}
