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

}
