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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import bill.piggy.R
import bill.piggy.databinding.FragmentMonthlyBudgetBinding


class MonthlyBudgetViewModel : ViewModel() {
    private val _mainText = MutableLiveData("Click add to add a new transaction")
    val mainText: LiveData<String> = _mainText

    private val _navigation = MutableLiveData<NavDirections?>()
    val navigation: LiveData<NavDirections?> = _navigation

    fun bind(binding: FragmentMonthlyBudgetBinding) {
        binding.viewModel = this
    }

    // Events
    fun onAddTransaction() {
        _navigation.value = MonthlyBudgetFragmentDirections.actionAddTransaction()
    }

    fun onFinishedNavigation() {
        _navigation.value = null
    }
}

class MonthlyBudgetFragment : Fragment() {

    private val viewModel by viewModels<MonthlyBudgetViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMonthlyBudgetBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.bind(binding)

        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        viewModel.navigation.observe(viewLifecycleOwner) { direction ->
            if (direction != null) {
                findNavController().navigate(direction)
                viewModel.onFinishedNavigation()
            }
        }
    }
}
