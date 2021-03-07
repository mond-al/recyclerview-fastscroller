package com.al.mond.example

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.al.mond.example.simple.SimpleAdapter
import com.al.mond.example.simple.SimpleOffsetDecoration
import com.al.mond.fastscroller.FastScroller
import com.al.mond.fastscroller.dp2px


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Very simple RecyclerView and Adapter Bind
        findViewById<RecyclerView>(R.id.recyclerview).apply {
            adapter = SimpleAdapter(10000)
            addItemDecoration(SimpleOffsetDecoration(20))
        }.also { recyclerView ->
            FastScroller(createScrollHandleView()).attachTo(recyclerView)
        }
    }

    private fun createScrollHandleView(): View {
        val view = ImageView(this@MainActivity)
        view.id = View.generateViewId()
        view.setImageResource(R.drawable.ic_unfold_more_black_48dp)
        view.setBackgroundColor(Color.parseColor("#000000"))
        view.scaleType = ImageView.ScaleType.CENTER_CROP
        view.layoutParams = ViewGroup.LayoutParams(20.dp2px(),30.dp2px())
        return view
    }
}