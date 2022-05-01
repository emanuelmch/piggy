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

import bill.piggy.data.budgets.Budget
import bill.piggy.data.budgets.BudgetRepository
import bill.piggy.test.CoroutineTest
import bill.piggy.test.MockkTest
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MonthlyBudgetViewModelTests : CoroutineTest, MockkTest {

    private val budgetRepository = mockk<BudgetRepository>()

    private fun initViewModel() = MonthlyBudgetViewModel(budgetRepository)

    @Test
    fun `test budgets can emit a single budget`() = runTest {
        val budget = Budget(1, "Budget name", "Category name", 123)
        every { budgetRepository.watchAll() } returns flowOf(listOf(budget))

        val viewModel = initViewModel()

        val budgetViewModels = viewModel.uiState.first().budgets
        assertEquals(2, budgetViewModels.size)

        assertEquals(CategoryViewModel("Category name"), budgetViewModels[0])
        assertEquals(BudgetViewModel(budget), budgetViewModels[1])
    }

    @Test
    fun `test budgets can emit multiple budgets of the same category`() = runBlocking {
        val budgets = listOf(
            Budget(1, "First budget", "Category name", 123),
            Budget(2, "Second budget", "Category name", 321)
        )
        every { budgetRepository.watchAll() } returns flowOf(budgets)

        val viewModel = initViewModel()

        val budgetViewModels = viewModel.uiState.first().budgets
        assertEquals(3, budgetViewModels.size)

        assertEquals(CategoryViewModel("Category name"), budgetViewModels[0])
        assertEquals(BudgetViewModel(budgets[0]), budgetViewModels[1])
        assertEquals(BudgetViewModel(budgets[1]), budgetViewModels[2])
    }

    @Test
    fun `test budgets can emit multiple categories with a single budget each`() = runBlocking {
        val budgets = listOf(
            Budget(1, "First budget", "First category", 123),
            Budget(2, "Second budget", "Second category", 321)
        )
        every { budgetRepository.watchAll() } returns flowOf(budgets)

        val viewModel = initViewModel()

        val budgetViewModels = viewModel.uiState.first().budgets
        assertEquals(4, budgetViewModels.size)

        assertEquals(CategoryViewModel("First category"), budgetViewModels[0])
        assertEquals(BudgetViewModel(budgets[0]), budgetViewModels[1])
        assertEquals(CategoryViewModel("Second category"), budgetViewModels[2])
        assertEquals(BudgetViewModel(budgets[1]), budgetViewModels[3])
    }

    @Test
    fun `test budgets can emit multiple categories with multiple budget each`() = runBlocking {
        val budgets = listOf(
            Budget(1, "First budget", "First category", 123),
            Budget(2, "Second budget", "First category", 234),
            Budget(3, "Third budget", "Second category", 345),
            Budget(4, "Fourth budget", "Second category", 456)
        )
        every { budgetRepository.watchAll() } returns flowOf(budgets)

        val viewModel = initViewModel()

        val budgetViewModels = viewModel.uiState.first().budgets
        assertEquals(6, budgetViewModels.size)

        assertEquals(CategoryViewModel("First category"), budgetViewModels[0])
        assertEquals(BudgetViewModel(budgets[0]), budgetViewModels[1])
        assertEquals(BudgetViewModel(budgets[1]), budgetViewModels[2])
        assertEquals(CategoryViewModel("Second category"), budgetViewModels[3])
        assertEquals(BudgetViewModel(budgets[2]), budgetViewModels[4])
        assertEquals(BudgetViewModel(budgets[3]), budgetViewModels[5])
    }
}
