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

package bill.piggy.features.add_transaction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.navigation.findNavController
import bill.piggy.common.CurrencyConverter
import bill.piggy.common.ui.CurrencyTextInputFilter
import bill.piggy.common.ui.addFilters
import bill.piggy.data.budgets.Budget
import bill.piggy.data.budgets.BudgetRepository
import bill.piggy.data.payees.Payee
import bill.piggy.data.payees.PayeeRepository
import bill.piggy.data.transactions.Transaction
import bill.piggy.databinding.AddTransactionFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddTransactionViewModel(
    private val budgetRepository: BudgetRepository,
    private val payeeRepository: PayeeRepository
) : ViewModel() {

    fun bind(binding: AddTransactionFragmentBinding) {
        binding.viewModel = this
    }

    // Setup properties
    val payees: LiveData<List<Payee>> = liveData { emit(payeeRepository.fetchAll()) }
    val budgets: LiveData<List<Budget>> = liveData { emit(budgetRepository.fetchAll()) }

    // Input properties
    private val _transaction = MutableLiveData(Transaction.Invalid)
    val transaction: LiveData<Transaction> = _transaction.distinctUntilChanged()

    val amountInCurrency = MutableLiveData("")

    // Dynamic properties
    val canSave: LiveData<Boolean> = transaction.map { it.isValid }

    // Events
    fun onAmountChanged(newValue: String) {
        amountInCurrency.value = newValue
        val newAmount = CurrencyConverter.moneyToCents(newValue)
        _transaction.value = _transaction.value!!.copy(amount = newAmount)
        Log.d("BILL", "Setting the new amount as ($newAmount)")
    }

    fun onPayeeChanged(newValue: String) {
        val newPayee = payeeRepository.getByName(newValue)!!
        _transaction.value = _transaction.value!!.copy(payee = newPayee, budget = newPayee.preferredBudget)
    }

    fun onBudgetChanged(newValue: String) {
        val newBudget = budgetRepository.getByName(newValue)!!
        _transaction.value = _transaction.value!!.copy(budget = newBudget)
    }
}

class AddTransactionFragment : Fragment() {

    private val viewModel: AddTransactionViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AddTransactionFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.bind(binding)

        setupFields(binding, viewModel)
        setupListeners(binding)

        return binding.root
    }

    private fun setupFields(binding: AddTransactionFragmentBinding, viewModel: AddTransactionViewModel) {
        binding.amount.editText?.addFilters(CurrencyTextInputFilter)
        viewModel.payees.observe(viewLifecycleOwner) { payees ->
            val payeeNames = payees.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, payeeNames)
            (binding.payee.editText as AutoCompleteTextView).threshold = Int.MAX_VALUE
            (binding.payee.editText as AutoCompleteTextView).setAdapter(adapter)
        }
        viewModel.budgets.observe(viewLifecycleOwner) { budgets ->
            val budgetNames = budgets.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, budgetNames)
            (binding.budget.editText as AutoCompleteTextView).threshold = Int.MAX_VALUE
            (binding.budget.editText as AutoCompleteTextView).setAdapter(adapter)
        }
    }

    private fun setupListeners(binding: AddTransactionFragmentBinding) {
        binding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }
    }
}
