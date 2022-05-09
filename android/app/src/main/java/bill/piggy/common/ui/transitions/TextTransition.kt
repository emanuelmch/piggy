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
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.view.View
import bill.piggy.common.ui.toolbar
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.roundToInt

class TextTransition(
    val reverse: Boolean = false,
    target: String? = null,
    duration: Long? = null
) : BaseTransition<String>(target, duration) {

    override fun captureValues(view: View): String {
        require(view is AppBarLayout)
        return view.toolbar.title.trim().toString()
    }

    override fun createAnimator(startView: View, startValue: String, endView: View, endValue: String): Animator? {
        val toolbar = (endView as AppBarLayout).toolbar
        toolbar.title = startValue

        return ValueAnimator.ofObject(TextEvaluator(reverse), startValue, endValue).apply {
            addUpdateListener {
                toolbar.title = it.animatedValue as String
            }
        }
    }
}

private class TextEvaluator(val reverse: Boolean) : TypeEvaluator<String> {

    override fun evaluate(fraction: Float, startValue: String, endValue: String): String {
        val max = maxOf(startValue.length, endValue.length)
        val start = startValue.padEnd(max, ' ')
        val end = endValue.padEnd(max, ' ')

        return if (reverse) {
            val cutoff = (max * (1 - fraction)).roundToInt()
            start.substring(0, cutoff) + end.substring(cutoff)
        } else {
            val cutoff = (max * fraction).roundToInt()
            end.substring(0, cutoff) + start.substring(cutoff)
        }
    }
}
