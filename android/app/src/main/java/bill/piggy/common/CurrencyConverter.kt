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

import java.text.DecimalFormat

object CurrencyConverter {

    @JvmStatic
    fun centsToMoney(cents: Long): String {
        require(cents in 0..1_000_000_000_000_00L)

        val pattern = if (cents % 100L == 0L) "$ #,##0" else "$ #,##0.00"
        val formatter = DecimalFormat(pattern)
        return formatter.format(cents.toDouble() / 100.0)
    }

    fun moneyToCents(money: String): Long {
        if (money.isEmpty()) return 0

        val splitted = money.split('.')

        var cents = 100 * (splitted[0].toLongOrNull() ?: 0)
        cents += (splitted.getOrNull(1) ?: "").padEnd(2, '0').toLong()
        return cents
    }
}
