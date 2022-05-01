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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import bill.piggy.common.ui.CurrencyTextInputFilter
import bill.piggy.common.ui.addFilters
import bill.piggy.databinding.AddTransactionFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddTransactionFragment : Fragment() {

    private val viewModel by viewModel<AddTransactionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AddTransactionFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupFields(binding, viewModel)
        setupListeners(binding)

        return binding.root
    }

    private fun setupFields(binding: AddTransactionFragmentBinding, viewModel: AddTransactionViewModel) {
        binding.amount.editText?.addFilters(CurrencyTextInputFilter)
        viewLifecycleOwner.lifecycleScope.launch {
            val payeeNames = viewModel.payees.first().map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, payeeNames)
            (binding.payee.editText as AutoCompleteTextView).apply {
                threshold = Int.MAX_VALUE
                setAdapter(adapter)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val budgetNames = viewModel.budgets.first().map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, budgetNames)
            (binding.budget.editText as AutoCompleteTextView).apply {
                threshold = Int.MAX_VALUE
                setAdapter(adapter)
            }
        }
    }

    private fun setupListeners(binding: AddTransactionFragmentBinding) {
        binding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }
    }
}
