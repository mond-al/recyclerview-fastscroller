# RecyclerView-fastscroller
Very Simple FastScroller 


# release
https://github.com/mond-al/recyclerview-fastscroller/releases

<img src="https://github.com/mond-al/recyclerview-fastscroller/blob/main/demo.gif?raw=true" width="50%">

### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```xml
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### Step 2. Add the dependency
```
	dependencies {
	        implementation 'com.github.mond-al:recyclerview-fastscroller:1.0'
	}
```

### Step 3. Use it~ SImple!
```xml
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/items"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
        tools:layout_editor_absoluteX="16dp"
        tools:layout_editor_absoluteY="0dp" />

    <!-- add handle view-->
    <ImageView
        android:id="@+id/handle_view"
        android:layout_width="20dp"
        android:layout_height="40dp"
        android:layout_marginTop="0dp"
        android:layout_marginEnd="0dp"
        android:background="@color/black"
        android:contentDescription="@string/quick_scroll_handle"
        android:scaleType="centerCrop"
        android:src="@drawable/ic_unfold_more_black_48dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
  
```

create FastScroller object And Bind to recyclerView.
```kotlin
FastScroller(handleView, bubbleListener).bind(recyclerView)
```
