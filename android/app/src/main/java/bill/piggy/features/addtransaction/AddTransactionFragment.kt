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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import bill.piggy.common.collectInBackground
import bill.piggy.common.ui.CurrencyTextInputFilter
import bill.piggy.common.ui.addFilters
import bill.piggy.common.ui.transitions.ExpandAndShrinkTransition
import bill.piggy.common.ui.transitions.IconTransition
import bill.piggy.common.ui.transitions.TextTransition
import bill.piggy.common.ui.transitions.transitions
import bill.piggy.databinding.AddTransactionFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTransactionFragment : Fragment() {

    private val viewModel by viewModel<AddTransactionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = transitions(
            ExpandAndShrinkTransition(target = "transition_fab"),
            IconTransition(target = "transition_fab"),
            IconTransition(target = "transition_appbar"),
            TextTransition(target = "transition_appbar")
        )
        sharedElementReturnTransition = transitions(
            ExpandAndShrinkTransition(target = "transition_fab"),
            IconTransition(target = "transition_fab", reverse = true),
            IconTransition(target = "transition_appbar", reverse = true),
            TextTransition(target = "transition_appbar", reverse = true)
        )
    }

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
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            binding.amount.editText?.addFilters(CurrencyTextInputFilter)
            val payeeNames = viewModel.payees.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, payeeNames)
            (binding.payee.editText as AutoCompleteTextView).apply {
                threshold = Int.MAX_VALUE
                launch(Dispatchers.Main) {
                    setAdapter(adapter)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val budgetNames = viewModel.budgets.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, budgetNames)
            (binding.budget.editText as AutoCompleteTextView).apply {
                threshold = Int.MAX_VALUE
                launch(Dispatchers.Main) {
                    setAdapter(adapter)
                }
            }
        }
    }

    private fun setupListeners(binding: AddTransactionFragmentBinding) {
        viewModel.uiState.collectInBackground(viewLifecycleOwner) { uiState ->
            if (uiState.isFinished) {
                activity?.supportFragmentManager?.popBackStack()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.addButton.setOnClickListener {
            viewModel.saveTransaction()
        }
    }

    companion object {

        fun create() = AddTransactionFragment()
    }
}
