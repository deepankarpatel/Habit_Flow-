package com.habitflow.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloat

// Fades + slides content in when it first appears. Delay staggers a list.
@Composable
fun AnimatedEntrance(index: Int = 0, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index.coerceAtMost(6) * 60L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 5 }
    ) {
        content()
    }
}

// Counts from 0 up to target, and re-animates if the target changes.
@Composable
fun rememberCountUp(target: Int, durationMillis: Int = 900): Int {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(target) {
        anim.animateTo(
            target.toFloat(),
            tween(durationMillis, easing = FastOutSlowInEasing)
        )
    }
    return anim.value.toInt()
}

// 🔥 that gently pulses while a streak is active, dimmed when it isn't.
@Composable
fun PulsingFlame(active: Boolean, fontSize: TextUnit = 22.sp) {
    val transition = rememberInfiniteTransition(label = "flame")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "flameScale"
    )
    Text(
        text = "🔥",
        fontSize = fontSize,
        modifier = Modifier.graphicsLayer {
            val s = if (active) pulse else 1f
            scaleX = s
            scaleY = s
            alpha = if (active) 1f else 0.4f
        }
    )
}