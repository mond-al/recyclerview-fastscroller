package com.al.mond.example

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.al.mond.example.simple.SimpleAdapter
import com.al.mond.example.simple.SimpleOffsetDecoration
import com.al.mond.fastscroller.FastScroller


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val handleView = findViewById<View>(R.id.handle_view)
        findViewById<RecyclerView>(R.id.items).apply {
            adapter = SimpleAdapter(10000)
            addItemDecoration(SimpleOffsetDecoration(20))
        }.also { recyclerView ->
            FastScroller(handleView, 400, 3000).bind(recyclerView)
        }
    }

}