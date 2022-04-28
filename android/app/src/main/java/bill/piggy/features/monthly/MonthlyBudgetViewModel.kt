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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.navigation.NavDirections
import bill.piggy.R
import bill.piggy.common.ui.BindableViewModel
import bill.piggy.data.budgets.Budget
import bill.piggy.data.budgets.BudgetRepository

data class CategoryViewModel(val name: String) : BindableViewModel {
    override val layoutId get() = R.layout.monthly_budget_item_category
    override val viewType get() = 0
}

data class BudgetViewModel(val budget: Budget) : BindableViewModel {
    override val layoutId get() = R.layout.monthly_budget_item_budget
    override val viewType get() = 1
}

sealed class MonthlyBudgetUiState {

    val header get() = "April's Budget"

    object Idle : MonthlyBudgetUiState()
    class Navigating(val direction: NavDirections) : MonthlyBudgetUiState()
}

class MonthlyBudgetViewModel(
    budgetRepository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<MonthlyBudgetUiState>(MonthlyBudgetUiState.Idle)
    val uiState = _uiState.distinctUntilChanged()
    val budgets = budgetRepository.getAll().map(this::budgetsToViewModels)

    // Events
    fun onAddTransaction() {
        assert(uiState.value is MonthlyBudgetUiState.Idle)
        _uiState.value = MonthlyBudgetUiState.Navigating(MonthlyBudgetFragmentDirections.actionAddTransaction())
    }

    fun onFinishedNavigation() {
        assert(uiState.value is MonthlyBudgetUiState.Navigating)
        _uiState.value = MonthlyBudgetUiState.Idle
    }

    // Processing functions
    private fun budgetsToViewModels(budgets: List<Budget>): List<BindableViewModel> {
        val viewModels = mutableListOf<BindableViewModel>()

        var lastCategory: String? = null
        for (budget in budgets) {
            if (budget.category != lastCategory) {
                viewModels += CategoryViewModel(budget.category)
                lastCategory = budget.category
            }

            viewModels += BudgetViewModel(budget)
        }
        return viewModels
    }
}
