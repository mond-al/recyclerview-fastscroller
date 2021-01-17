package com.al.mond.support

import android.content.res.Resources
import android.util.TypedValue
import java.util.*


// About Display Numbers

fun Number.dp2px() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
).toInt()

// About Date

fun Long.toDate() = Date(this)
fun Date.fromDate() = time

