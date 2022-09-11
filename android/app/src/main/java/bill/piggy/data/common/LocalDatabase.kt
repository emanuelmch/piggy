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

// FIXME: Remove this supression
@file:Suppress("DEPRECATION")

package bill.piggy.data.common

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import bill.piggy.data.budgets.BudgetLocalDataSource
import bill.piggy.data.budgets.RoomBudget
import bill.piggy.data.payees.PartialRoomPayee
import bill.piggy.data.payees.PayeeLocalDataSource
import bill.piggy.data.transactions.PartialRoomTransaction
import bill.piggy.data.transactions.TransactionLocalDataSource

// Yeah, yeah, I know I should be using a CoroutineWorker instead, but this is
// just for debugging NOW and I don't wanna add a dependency just for that
private class DebugDatabasePopulator : AsyncTask<Unit, Unit, Unit>() {

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Unit?) {
        val previousDebt = "Previous Debt"
        val everydayExpenses = "Everyday Expenses"
        val monthlyBills = "Monthly Bills"
        val services = "Services"
        val specials = "Specials"
        val rainyDayFunds = "Rainy Day Funds"

        LocalDatabase.instance.budgetDataSource().insert(
            RoomBudget(0, "Nubank", previousDebt, 12345),
            RoomBudget(0, "Santander", previousDebt, 4321),
            RoomBudget(0, "Food & Groceries", everydayExpenses, 18000),
            RoomBudget(0, "Socializing", everydayExpenses, 10000),
            RoomBudget(0, "Clothing", everydayExpenses, 10000),
            RoomBudget(0, "Fun & Games", everydayExpenses, 10000),
            RoomBudget(0, "Spending Money", everydayExpenses, 17500),
            RoomBudget(0, "House", monthlyBills, 2200),
            RoomBudget(0, "Phone", monthlyBills, 8999),
            RoomBudget(0, "Taxes", monthlyBills, 10000),
            RoomBudget(0, "Streaming", services, 5000),
            RoomBudget(0, "Dropbox", services, 8000),
            RoomBudget(0, "Spotify", services, 1690),
            RoomBudget(0, "Studies", specials, 78912),
            RoomBudget(0, "Travel", specials, 134679),
            RoomBudget(0, "Projects", specials, 0),
            RoomBudget(0, "Emergency Fund", rainyDayFunds, 900000),
            RoomBudget(0, "Health", rainyDayFunds, 100000),
            RoomBudget(0, "Investment Funds", rainyDayFunds, 78912),
        )

        val houseBudget = LocalDatabase.instance.budgetDataSource().getByName("House")
        val groceriesBudget = LocalDatabase.instance.budgetDataSource().getByName("Food & Groceries")

        LocalDatabase.instance.payeeDataSource().insert(
            PartialRoomPayee(0, "Rent", houseBudget.uid),
            PartialRoomPayee(0, "Angeloni", groceriesBudget.uid)
        )
    }
}

@Database(
    entities = [RoomBudget::class, PartialRoomPayee::class, PartialRoomTransaction::class],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun budgetDataSource(): BudgetLocalDataSource
    abstract fun payeeDataSource(): PayeeLocalDataSource
    abstract fun transactionDataSource(): TransactionLocalDataSource

    object DebugOnlyDatabaseCreator : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            DebugDatabasePopulator().execute()
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            DebugDatabasePopulator().execute()
        }
    }

    companion object {

        lateinit var instance: LocalDatabase

        fun create(applicationContext: Context) {
            instance = Room.databaseBuilder(
                applicationContext,
                LocalDatabase::class.java,
                "piggy-bank"
            ).fallbackToDestructiveMigration()
                .addCallback(DebugOnlyDatabaseCreator)
                .build()
        }
    }
}
