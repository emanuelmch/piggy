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

package bill.piggy.features.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bill.piggy.common.CurrencyConverter
import bill.piggy.data.budgets.BudgetRepository
import bill.piggy.data.payees.PayeeRepository
import bill.piggy.data.transactions.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AddTransactionUiState(
    val transaction: Transaction = Transaction.Invalid,
    val amountInCurrency: String = ""
) {
    val canSave: Boolean
        get() = transaction.isValid
}

class AddTransactionViewModel(
    private val budgetRepository: BudgetRepository,
    private val payeeRepository: PayeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> get() = _uiState
    val payees by lazy { payeeRepository.getAll() }
    val budgets by lazy { budgetRepository.getAll() }

    // Events
    fun onAmountChanged(newValue: String) {
        val state = uiState.value

        val newAmount = CurrencyConverter.moneyToCents(newValue)
        val newTransaction = state.transaction.copy(amount = newAmount)

        _uiState.value = state.copy(transaction = newTransaction, amountInCurrency = newValue)
    }

    fun onPayeeChanged(payeeName: String) {
        viewModelScope.launch {
            val state = uiState.value

            val newPayee = payees.find { it.name == payeeName }!!
            val newTransaction =
                if (newPayee.preferredBudget != null) {
                    state.transaction.copy(payee = newPayee, budget = newPayee.preferredBudget)
                } else {
                    state.transaction.copy(payee = newPayee)
                }

            _uiState.value = state.copy(transaction = newTransaction)
        }
    }

    fun onBudgetChanged(budgetName: String) {
        viewModelScope.launch {
            val state = uiState.value

            val newBudget = budgets.find { it.name == budgetName }!!
            val newTransaction = state.transaction.copy(budget = newBudget)

            _uiState.value = state.copy(transaction = newTransaction)
        }
    }
}
