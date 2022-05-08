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
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionSet
import androidx.transition.TransitionValues

abstract class BaseTransition(
    target: String? = null,
    duration: Long? = null
) : Transition() {

    init {
        target?.let { super.addTarget(it) }
        duration?.let { super.setDuration(it) }
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        require(startValues != null && endValues != null)
        return createAnimatorFromValues(sceneRoot, startValues, endValues)
    }

    // TODO: Find a better name
    abstract fun createAnimatorFromValues(
        sceneRoot: ViewGroup,
        startValues: TransitionValues,
        endValues: TransitionValues
    ): Animator?
}

fun transitions(vararg transitions: Transition) = object : TransitionSet() {
    init {
        transitions.forEach(this::addTransition)
    }
}
