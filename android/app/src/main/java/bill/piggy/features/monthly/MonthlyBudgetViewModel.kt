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

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bill.piggy.R
import bill.piggy.common.collectInBackground
import bill.piggy.common.ui.BindableViewModel
import bill.piggy.data.budgets.Budget
import bill.piggy.data.budgets.BudgetRepository
import bill.piggy.features.addtransaction.AddTransactionFragment
import bill.piggy.features.expenses.ExpensesFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

data class CategoryViewModel(val name: String) : BindableViewModel {
    override val layoutId get() = R.layout.monthly_budget_item_category
    override val viewType get() = 0
}

data class BudgetViewModel(val budget: Budget, val clickHandler: () -> Unit = {}) : BindableViewModel {
    override val layoutId get() = R.layout.monthly_budget_item_budget
    override val viewType get() = 1

    fun onClick() = clickHandler()

    override fun equals(other: Any?): Boolean {
        return other is BudgetViewModel && budget == other.budget
    }

    override fun hashCode(): Int {
        var result = budget.hashCode()
        result = 31 * result + clickHandler.hashCode()
        return result
    }
}

sealed class MonthlyBudgetUiState(
    val budgets: List<BindableViewModel>
) {

    // FIXME: Remove this Transition Animation workaround
    val header get() = "May's Budget      "

    abstract fun update(budgets: List<BindableViewModel>): MonthlyBudgetUiState

    object Loading : MonthlyBudgetUiState(emptyList()) {
        override fun update(budgets: List<BindableViewModel>) = Idle(budgets)
    }

    class Idle(budgets: List<BindableViewModel>) : MonthlyBudgetUiState(budgets) {
        override fun update(budgets: List<BindableViewModel>) = Idle(budgets)
    }

    class Navigating(budgets: List<BindableViewModel>, val destination: Fragment) : MonthlyBudgetUiState(budgets) {
        override fun update(budgets: List<BindableViewModel>) = Navigating(budgets, destination)
    }
}

class MonthlyBudgetViewModel(
    budgetRepository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MonthlyBudgetUiState>(MonthlyBudgetUiState.Loading)
    val uiState: StateFlow<MonthlyBudgetUiState> get() = _uiState

    init {
        budgetRepository.watchAll()
            .map(this::budgetsToViewModels)
            .collectInBackground(viewModelScope) { budgets ->
                val value = _uiState.value
                check(value is MonthlyBudgetUiState.Loading || value is MonthlyBudgetUiState.Idle)
                _uiState.update { it.update(budgets) }
            }
    }

    // Events
    fun onAddTransaction() {
        val state = uiState.value
        check(state is MonthlyBudgetUiState.Idle)

        _uiState.update { MonthlyBudgetUiState.Navigating(it.budgets, AddTransactionFragment.create()) }
    }

    fun onFinishedNavigation() {
        val state = uiState.value
        check(state is MonthlyBudgetUiState.Navigating)
        _uiState.update { MonthlyBudgetUiState.Idle(state.budgets) }
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

            viewModels += BudgetViewModel(budget) {
                _uiState.update { MonthlyBudgetUiState.Navigating(it.budgets, ExpensesFragment.create()) }
            }
        }
        return viewModels
    }
}
