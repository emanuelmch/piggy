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
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class CurrencyConverterTests {

    @Test
    fun `test moneyToCents without a period`() {
        assertThat(moneyToCents(""), `is`(equalTo(0)))
        assertThat(moneyToCents("1"), `is`(equalTo(100)))
        assertThat(moneyToCents("123456789"), `is`(equalTo(12345678900)))
    }

    @Test
    fun `test moneyToCents with a period`() {
        assertThat(moneyToCents(".1"), `is`(equalTo(10)))
        assertThat(moneyToCents("0.1"), `is`(equalTo(10)))
        assertThat(moneyToCents("0.01"), `is`(equalTo(1)))
        assertThat(moneyToCents("10.01"), `is`(equalTo(1001)))
    }

    @Test
    fun `test centsToMoney with cents`() {
        assertThat(centsToMoney(1), `is`(equalTo("$ 0.01")))
        assertThat(centsToMoney(9), `is`(equalTo("$ 0.09")))
        assertThat(centsToMoney(10), `is`(equalTo("$ 0.10")))
        assertThat(centsToMoney(99), `is`(equalTo("$ 0.99")))
        assertThat(centsToMoney(101), `is`(equalTo("$ 1.01")))
        assertThat(centsToMoney(199), `is`(equalTo("$ 1.99")))
        assertThat(centsToMoney(999), `is`(equalTo("$ 9.99")))
        assertThat(centsToMoney(99999), `is`(equalTo("$ 999.99")))
        assertThat(centsToMoney(100001), `is`(equalTo("$ 1,000.01")))
        assertThat(centsToMoney(99999999), `is`(equalTo("$ 999,999.99")))
        assertThat(centsToMoney(999_999_999_999_01L), `is`(equalTo("$ 999,999,999,999.01")))
        assertThat(centsToMoney(999_999_999_999_99L), `is`(equalTo("$ 999,999,999,999.99")))
    }

    @Test
    fun `test centsToMoney without cents`() {
        Double.MAX_VALUE
        assertThat(centsToMoney(0), `is`(equalTo("$ 0")))
        assertThat(centsToMoney(1_00), `is`(equalTo("$ 1")))
        assertThat(centsToMoney(999_00), `is`(equalTo("$ 999")))
        assertThat(centsToMoney(1_000_00), `is`(equalTo("$ 1,000")))
        assertThat(centsToMoney(999_999_00), `is`(equalTo("$ 999,999")))
        assertThat(centsToMoney(999_999_00), `is`(equalTo("$ 999,999")))
        assertThat(centsToMoney(999_999_999_999_00L), `is`(equalTo("$ 999,999,999,999")))
        assertThat(centsToMoney(1_000_000_000_000_00L), `is`(equalTo("$ 1,000,000,000,000")))
    }
}
