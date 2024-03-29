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

package bill.piggy.data.budgets

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "budget")
data class RoomBudget(
    @PrimaryKey(autoGenerate = true) val uid: Long,
    val name: String,
    val category: String,
    val moneyInCents: Long
) {

    val asBudget: Budget
        get() = Budget(uid, name, category, moneyInCents)
}

@Dao
interface BudgetLocalDataSource {

    @Query("SELECT * FROM budget WHERE name = :name LIMIT 1")
    fun getByName(name: String): RoomBudget

    @Query("SELECT * FROM budget")
    fun getAll(): List<RoomBudget>

    @Query("SELECT * FROM budget")
    fun watchAll(): Flow<List<RoomBudget>>

    @Insert
    fun insert(vararg budgets: RoomBudget)
}
