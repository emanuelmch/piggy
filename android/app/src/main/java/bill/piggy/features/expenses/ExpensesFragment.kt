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

package bill.piggy.features.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bill.piggy.R
import bill.piggy.common.collectInBackground
import bill.piggy.common.ui.BindableViewModel
import bill.piggy.data.transactions.Transaction
import bill.piggy.data.transactions.TransactionRepository
import bill.piggy.databinding.ExpensesFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

data class ExpenseViewModel(val transaction: Transaction) : BindableViewModel {
    override val layoutId get() = R.layout.expenses_item
}

data class ExpensesUiState(
    val expenses: List<ExpenseViewModel> = emptyList()
)

class ExpensesViewModel(
    transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState: StateFlow<ExpensesUiState> get() = _uiState

    init {
        transactionRepository.watchAll()
            .map { it.map(::ExpenseViewModel) }
            .collectInBackground(viewModelScope) { expenses ->
                _uiState.update { it.copy(expenses = expenses) }
            }
    }
}

class ExpensesFragment : Fragment() {

    private val viewModel by viewModel<ExpensesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = ExpensesFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupListeners(binding)

        return binding.root
    }

    private fun setupListeners(binding: ExpensesFragmentBinding) {
        binding.toolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    companion object {
        fun create() = ExpensesFragment()
    }
}
