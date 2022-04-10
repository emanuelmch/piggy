/*
 * Copyright (c) 2022 Emanuel Machado da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bill.piggy.common

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlin.reflect.KProperty

class DataBindLazy<T : ViewDataBinding>(private val layoutId: Int) {

    private var cached: T? = null

//    operator fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T =
//        cached ?: DataBindingUtil.setContentView<T>(thisRef, layoutId).also {
//            it.lifecycleOwner = thisRef
//            cached = it
//        }

    operator fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        val old = cached
        if (old != null) return old

        val new = DataBindingUtil.setContentView<T>(thisRef, layoutId).apply {
            lifecycleOwner = thisRef
        }
        cached = new
        return new
    }
}

fun <T : ViewDataBinding> Activity.databind(layoutId: Int) = DataBindLazy<T>(layoutId)
