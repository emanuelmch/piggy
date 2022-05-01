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

package bill.piggy.common

import bill.piggy.common.CurrencyConverter.centsToMoney
import bill.piggy.common.CurrencyConverter.moneyToCents
import kotlin.test.assertEquals
import org.junit.Test

class CurrencyConverterTests {

    @Test
    fun `test moneyToCents without a period`() {
        assertEquals(0, moneyToCents(""))
        assertEquals(100, moneyToCents("1"))
        assertEquals(12345678900, moneyToCents("123456789"))
    }

    @Test
    fun `test moneyToCents with a period`() {
        assertEquals(10, moneyToCents(".1"))
        assertEquals(10, moneyToCents("0.1"))
        assertEquals(1, moneyToCents("0.01"))
        assertEquals(1001, moneyToCents("10.01"))
    }

    @Test
    fun `test centsToMoney with cents`() {
        assertEquals("$ 0.01", centsToMoney(1))
        assertEquals("$ 0.09", centsToMoney(9))
        assertEquals("$ 0.10", centsToMoney(10))
        assertEquals("$ 0.99", centsToMoney(99))
        assertEquals("$ 1.01", centsToMoney(101))
        assertEquals("$ 1.99", centsToMoney(199))
        assertEquals("$ 9.99", centsToMoney(999))
        assertEquals("$ 999.99", centsToMoney(99999))
        assertEquals("$ 1,000.01", centsToMoney(100001))
        assertEquals("$ 999,999.99", centsToMoney(99999999))
        assertEquals("$ 999,999,999,999.01", centsToMoney(999_999_999_999_01L))
        assertEquals("$ 999,999,999,999.99", centsToMoney(999_999_999_999_99L))
    }

    @Test
    fun `test centsToMoney without cents`() {
        assertEquals("$ 0", centsToMoney(0))
        assertEquals("$ 1", centsToMoney(1_00))
        assertEquals("$ 999", centsToMoney(999_00))
        assertEquals("$ 1,000", centsToMoney(1_000_00))
        assertEquals("$ 999,999", centsToMoney(999_999_00))
        assertEquals("$ 999,999", centsToMoney(999_999_00))
        assertEquals("$ 999,999,999,999", centsToMoney(999_999_999_999_00L))
        assertEquals("$ 1,000,000,000,000", centsToMoney(1_000_000_000_000_00L))
    }
}
