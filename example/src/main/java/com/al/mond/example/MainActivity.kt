package com.al.mond.example

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.al.mond.example.simple.SimpleAdapter
import com.al.mond.example.simple.SimpleOffsetDecoration
import com.al.mond.fastscroller.BubbleListener
import com.al.mond.fastscroller.FastScroller


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The height of the parent of the recycler view and the handle view must be the same.
        // However, they don't have to be under the same parents.
        setContentView(R.layout.activity_main)

        // HandleView
        val handleView = findViewById<View>(R.id.handle_view)

        // BubbleView (optional feature)
        // must implement with BubbleAdapter. see @SimpleAdapter
        val bubble = findViewById<View>(R.id.bubble)
        val bubbleText = findViewById<TextView>(R.id.bubble_text)
        val bubbleListener = object : BubbleListener {
            override fun setBubble(str: String) {
                bubbleText.text = str
            }
            override fun setViewY(y: Float) {
                bubble.y = y
            }
            override fun setVisible(isVisible: Boolean) {
                bubble.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }

        findViewById<RecyclerView>(R.id.items).apply {
            adapter = SimpleAdapter(2000)
            layoutManager = GridLayoutManager(this@MainActivity,3)
            addItemDecoration(SimpleOffsetDecoration(20))
        }.also { recyclerView ->
            // Simple Bind!!
            FastScroller(handleView, bubbleListener).bind(recyclerView)
        }
    }

}