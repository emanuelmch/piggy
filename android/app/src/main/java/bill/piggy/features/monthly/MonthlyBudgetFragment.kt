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

package bill.piggy.features.monthly

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import bill.piggy.R
import bill.piggy.common.ui.BindableViewModel
import bill.piggy.data.budgets.Budget
import bill.piggy.databinding.MonthlyBudgetFragmentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CategoryViewModel(val name: String) : BindableViewModel {
    override val layoutId get() = R.layout.monthly_budget_item_category
    override val viewType get() = 0
}

class BudgetViewModel(val budget: Budget) : BindableViewModel {

    // FIXME: Delete this constructor
    constructor(name: String, money: Long) : this(Budget(0, name, money)) {}

    override val layoutId get() = R.layout.monthly_budget_item_budget
    override val viewType get() = 1
}

sealed class MonthlyBudgetUiState {

    val header get() = "April's Budget"

    object Idle : MonthlyBudgetUiState()
    class Navigating(val direction: NavDirections) : MonthlyBudgetUiState()
}

class MonthlyBudgetViewModel : ViewModel() {
    val uiState = MutableLiveData<MonthlyBudgetUiState>(MonthlyBudgetUiState.Idle)

    val budgets = MutableLiveData<List<BindableViewModel>>()

    fun bind(binding: MonthlyBudgetFragmentBinding) {
        binding.viewModel = this
        viewModelScope.launch {
            delay(1000)
            budgets.value = listOf(
                CategoryViewModel("Previous Debt"),
                BudgetViewModel("Nubank", 12345),
                BudgetViewModel("Santander", 4321),
                CategoryViewModel("Everyday Expenses"),
                BudgetViewModel("Food & Groceries", 18000),
                BudgetViewModel("Socializing", 10000),
                BudgetViewModel("Clothing", 10000),
                BudgetViewModel("Fun & Games", 10000),
                BudgetViewModel("Spending Money", 17500),
                CategoryViewModel("Monthly Bills"),
                BudgetViewModel("House", 2200),
                BudgetViewModel("Phone", 8999),
                BudgetViewModel("Taxes", 10000),
                CategoryViewModel("Services"),
                BudgetViewModel("Streaming", 5000),
                BudgetViewModel("Dropbox", 8000),
                BudgetViewModel("Spotify", 1690),
                CategoryViewModel("Specials"),
                BudgetViewModel("Studies", 78912),
                BudgetViewModel("Travel", 134679),
                BudgetViewModel("Projects", 0),
                CategoryViewModel("Rainy Day Funds"),
                BudgetViewModel("Emergency Fund", 900000),
                BudgetViewModel("Health", 100000),
                BudgetViewModel("Investment Funds", 78912),
            )
        }
    }

    // Events
    fun onAddTransaction() {
        assert(uiState.value is MonthlyBudgetUiState.Idle)
        uiState.value = MonthlyBudgetUiState.Navigating(MonthlyBudgetFragmentDirections.actionAddTransaction())
    }

    fun onFinishedNavigation() {
        assert(uiState.value is MonthlyBudgetUiState.Navigating)
        uiState.value = MonthlyBudgetUiState.Idle
    }
}

class MonthlyBudgetFragment : Fragment() {

    private val viewModel by viewModels<MonthlyBudgetViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = MonthlyBudgetFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.bind(binding)

        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is MonthlyBudgetUiState.Idle -> {
                    Log.d("Bill", "We're idle")
                }
                is MonthlyBudgetUiState.Navigating -> {
                    findNavController().navigate(uiState.direction)
                    viewModel.onFinishedNavigation()
                }
            }
        }
    }
}
