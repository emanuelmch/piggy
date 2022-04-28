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

package bill.piggy.data.payees

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import bill.piggy.data.budgets.Budget
import bill.piggy.data.budgets.RoomBudget

@Entity(
    tableName = "payee",
    foreignKeys = [
        ForeignKey(entity = RoomBudget::class, parentColumns = ["uid"], childColumns = ["preferred_budget_id"])
    ],
    indices = [Index("preferred_budget_id")]
)
data class PartialRoomPayee(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val name: String,
    @ColumnInfo(name = "preferred_budget_id") val preferredBudgetId: Long
)

data class RoomPayee(
    @Embedded
    val payee: PartialRoomPayee,

    @Relation(parentColumn = "preferred_budget_id", entityColumn = "uid")
    val preferredBudget: RoomBudget?
) {

    val asPayee: Payee
        get() = Payee(payee.uid, payee.name, preferredBudget?.asBudget)
}

@Dao
interface PayeeLocalDataSource {

    @Transaction
    @Query("SELECT * FROM payee")
    fun watchAll(): LiveData<List<RoomPayee>>

    @Insert
    fun insert(vararg payees: PartialRoomPayee)
}
