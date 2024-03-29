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
import androidx.fragment.app.commit
import bill.piggy.R
import bill.piggy.common.collectInBackground
import bill.piggy.databinding.MonthlyBudgetFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MonthlyBudgetFragment : Fragment() {

    private val viewModel by viewModel<MonthlyBudgetViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = MonthlyBudgetFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupNavigation(binding)

        return binding.root
    }

    private fun setupNavigation(binding: MonthlyBudgetFragmentBinding) {
        viewModel.uiState.collectInBackground(viewLifecycleOwner) { uiState ->
            if (uiState is MonthlyBudgetUiState.Navigating) {
                activity?.supportFragmentManager?.commit {
                    addSharedElement(binding.appbar, "transition_appbar")
                    addSharedElement(binding.addButton, "transition_fab")
                    replace(R.id.main_container, uiState.destination)
                    addToBackStack(null)
                }
                viewModel.onFinishedNavigation()
            }
        }
    }
}
