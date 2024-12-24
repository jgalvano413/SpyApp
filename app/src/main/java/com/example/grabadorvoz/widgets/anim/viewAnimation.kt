package com.example.pruebaremoto.widgets.anim

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform

fun viewAnimation(startView2: View, endView2: View, container: ViewGroup) {
    startView2.visibility = View.GONE
    endView2.visibility = View.VISIBLE

    val transform = MaterialContainerTransform().apply {
        scrimColor = Color.TRANSPARENT
        duration = 500L
        startView = startView2
        endView = endView2
        addTarget(endView!!)
    }
    transform.setPathMotion(MaterialArcMotion())
    TransitionManager.beginDelayedTransition(container, transform)
}
