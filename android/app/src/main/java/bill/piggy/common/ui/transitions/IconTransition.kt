/*
 * Copyright (c) 2022 Emanuel Machado da Silva
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bill.piggy.common.ui.transitions

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionValues
import bill.piggy.BuildConfig
import bill.piggy.common.ui.toolbar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.max

private const val DrawableProperty = "bill.piggy.common.ui.transitions:IconTransition:drawable"

private const val MIN_DEGREES = 0f
private const val MAX_DEGREES = 360f
private const val MID_DEGREES = 180f

class IconTransition(
    val reverse: Boolean = false,
    target: String? = null,
    duration: Long? = null
) : BaseTransition(target, duration) {

    override fun captureStartValues(transitionValues: TransitionValues) = captureValues(transitionValues)

    override fun captureEndValues(transitionValues: TransitionValues) = captureValues(transitionValues)

    private fun captureValues(transitionValues: TransitionValues) {
        when (val view = transitionValues.view) {
            is FloatingActionButton -> transitionValues.values[DrawableProperty] = view.drawable
            is AppBarLayout -> transitionValues.values[DrawableProperty] = view.toolbar.navigationIcon
            else -> {
                if (BuildConfig.DEBUG) {
                    throw IllegalArgumentException("Unknown view type")
                }
            }
        }
    }

    override fun createAnimatorFromValues(
        sceneRoot: ViewGroup,
        startValues: TransitionValues,
        endValues: TransitionValues
    ): Animator? {

        val iconSetter = IconSetterStrategy.fromView(endValues.view) ?: return null

        val startDrawable = startValues.values[DrawableProperty] as Drawable
        val endDrawable = endValues.values[DrawableProperty] as Drawable

        require(startDrawable.intrinsicHeight == endDrawable.intrinsicHeight)
        require(startDrawable.intrinsicWidth == endDrawable.intrinsicWidth)

        val width = max(startDrawable.intrinsicWidth, 1)
        val height = max(startDrawable.intrinsicHeight, 1)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        check(canvas.width == width && canvas.height == height)

        iconSetter.setIcon(startDrawable)

        return ValueAnimator.ofFloat(MIN_DEGREES, MAX_DEGREES).apply {
            addUpdateListener {
                val degrees = it.animatedValue as Float
                val drawable = (if (degrees <= MID_DEGREES) startDrawable else endDrawable)

                if (degrees == MAX_DEGREES) {
                    iconSetter.setIcon(drawable)
                    return@addUpdateListener
                }

                bitmap.eraseColor(Color.TRANSPARENT)

                val rotation = if (reverse) MAX_DEGREES - degrees else degrees
                val checkpoint = canvas.save()

                canvas.rotate(rotation, width / 2f, height / 2f)
                drawable.draw(canvas)

                iconSetter.setIcon(bitmap)
                canvas.restoreToCount(checkpoint)
            }
        }
    }

    private sealed interface IconSetterStrategy {
        fun setIcon(bitmap: Bitmap)
        fun setIcon(drawable: Drawable)

        class FloatingActionButtonIconSetterStrategy(val fab: FloatingActionButton) : IconSetterStrategy {
            override fun setIcon(bitmap: Bitmap) = fab.setImageBitmap(bitmap)
            override fun setIcon(drawable: Drawable) = fab.setImageDrawable(drawable)
        }

        class AppBarLayoutIconSetterStrategy(appbar: AppBarLayout) : IconSetterStrategy {
            val toolbar = appbar.toolbar
            val resources: Resources = appbar.context.resources

            override fun setIcon(bitmap: Bitmap) = setIcon(BitmapDrawable(resources, bitmap))
            override fun setIcon(drawable: Drawable) {
                toolbar.navigationIcon = drawable
            }
        }

        companion object {
            fun fromView(view: View) = when (view) {
                is AppBarLayout -> AppBarLayoutIconSetterStrategy(view)
                is FloatingActionButton -> FloatingActionButtonIconSetterStrategy(view)
                else -> null
            }
        }
    }
}
