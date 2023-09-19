package com.example.listadecompras.presentation.shop_item_screen.components

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


@Composable
fun FragmentContainer(
    modifier: Modifier = Modifier,
    fragmentManager: FragmentManager,
    commit: FragmentTransaction.(containerId: Int) -> Unit
) {
    val containerId by rememberSaveable { mutableIntStateOf(View.generateViewId()) }
//    var initialized by rememberSaveable { mutableStateOf(false) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            FragmentContainerView(context).apply { id = containerId }
        },
        update = { view ->
//            if (!initialized) {
                with(fragmentManager) {
                    popBackStack()
                    beginTransaction()
                        .commit(view.id)    //adding fragment to container
                }
//                initialized = true
//            } else {
//                fragmentManager.onContainerAvailable(view)
//            }
        }
    )
}

/** Access to package-private method in FragmentManager through reflection */
//private fun FragmentManager.onContainerAvailable(view: FragmentContainerView) {
//    val method = FragmentManager::class.java.getDeclaredMethod(
//        "onContainerAvailable", FragmentContainerView::class.java
//    )
//    method.isAccessible = true
//    method.invoke(this, view)
//}