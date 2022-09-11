/*
 * Copyright (c) 2022 Emanuel Machado da Silva
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bill.piggy.data.transactions

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
import bill.piggy.data.budgets.RoomBudget
import bill.piggy.data.payees.PartialRoomPayee
import bill.piggy.data.payees.RoomPayee
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(entity = PartialRoomPayee::class, parentColumns = ["uid"], childColumns = ["payee_id"]),
        ForeignKey(entity = RoomBudget::class, parentColumns = ["uid"], childColumns = ["budget_id"])
    ],
    indices = [Index("payee_id"), Index("budget_id")]
)
data class PartialRoomTransaction(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "payee_id") val payeeId: Long,
    @ColumnInfo(name = "budget_id") val budgetId: Long,
    val amountInCents: Long,
)

data class RoomTransaction(
    @Embedded
    val transaction: PartialRoomTransaction,
    @Relation(parentColumn = "payee_id", entityColumn = "uid")
    val payee: PartialRoomPayee,
    @Relation(parentColumn = "budget_id", entityColumn = "uid")
    val budget: RoomBudget,
) {

    // TODO: For now, we don't need our payee's preferred budget, so it's fine to make it null, but this might change in the future
    val asTransaction: Transaction
        get() = Transaction(transaction.amountInCents, RoomPayee(payee, null).asPayee, budget.asBudget)
}

@Dao
interface TransactionLocalDataSource {

    @Query("SELECT * FROM transactions")
    @androidx.room.Transaction
    fun watchAll(): Flow<List<RoomTransaction>>

    @Insert
    fun insert(vararg transaction: PartialRoomTransaction)
}
