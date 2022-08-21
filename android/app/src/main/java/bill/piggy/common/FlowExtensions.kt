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

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@Suppress("NOTHING_TO_INLINE")
// TODO: Parameter "action" should actually be "crossinline", figure out why the compiler won't allow us
inline fun <T> Flow<T>.collectInBackground(scope: CoroutineScope, noinline action: suspend (value: T) -> Unit) {
    scope.launch { this@collectInBackground.collect(action) }
}

@Suppress("NOTHING_TO_INLINE")
// TODO: Parameter "action" should actually be "crossinline", figure out why the compiler won't allow us
inline fun <T> Flow<T>.collectInBackground(owner: LifecycleOwner, noinline action: suspend (value: T) -> Unit) {
    owner.lifecycleScope.launch { this@collectInBackground.flowWithLifecycle(owner.lifecycle).collect(action) }
}

inline fun <T> ViewModel.eagerShare(crossinline flowFactory: () -> Flow<T>): Flow<T> {
    val shared = MutableSharedFlow<T>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    viewModelScope.launch { flowFactory().take(1).collect { shared.emit(it) } }
    return shared
}
