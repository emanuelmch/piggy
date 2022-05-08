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
import android.view.ViewGroup
import androidx.transition.TransitionValues

private const val ScaleProperty = "bill.piggy.common.ui.transitions:ExpandAndShrinkTransition:scale"

class ExpandAndShrinkTransition(
    target: String? = null,
    duration: Long? = null
) : BaseTransition(target, duration) {

    override fun captureStartValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        require(view.scaleX == view.scaleY)
        transitionValues.values[ScaleProperty] = view.scaleX
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        require(view.scaleX == view.scaleY)
        transitionValues.values[ScaleProperty] = view.scaleX
    }

    override fun createAnimatorFromValues(
        sceneRoot: ViewGroup,
        startValues: TransitionValues,
        endValues: TransitionValues
    ): Animator? {
        val startScale = startValues.values[ScaleProperty] as Float
        val endScale = endValues.values[ScaleProperty] as Float
        val view = endValues.view

        return ValueAnimator.ofFloat(startScale, startScale * 1.2f, endScale).apply {
            addUpdateListener {
                val scale = it.animatedValue as Float
                view.scaleX = scale
                view.scaleY = scale
            }
        }
    }
}
