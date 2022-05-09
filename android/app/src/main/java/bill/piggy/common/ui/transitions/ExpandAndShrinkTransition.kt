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
import android.view.View

class ExpandAndShrinkTransition(
    target: String? = null,
    duration: Long? = null
) : BaseTransition<Float>(target, duration) {

    override fun captureValues(view: View): Float {
        require(view.scaleX == view.scaleY)
        return view.scaleX
    }

    override fun createAnimator(startView: View, startValue: Float, endView: View, endValue: Float): Animator? {
        return ValueAnimator.ofFloat(startValue, startValue * 1.2f, endValue).apply {
            addUpdateListener {
                val scale = it.animatedValue as Float
                endView.scaleX = scale
                endView.scaleY = scale
            }
        }
    }
}
