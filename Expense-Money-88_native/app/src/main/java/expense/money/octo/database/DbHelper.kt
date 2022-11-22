package expense.money.octo.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context?) :
	SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

	override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
		sqLiteDatabase.execSQL(TripEntry.SQL_CREATE_TABLE.trimIndent())
		sqLiteDatabase.execSQL(ExpenseEntry.SQL_CREATE_TABLE.trimIndent())
	}

	override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
		sqLiteDatabase.execSQL(ExpenseEntry.SQL_DELETE_TABLE)
		sqLiteDatabase.execSQL(TripEntry.SQL_DELETE_TABLE)

		onCreate(sqLiteDatabase)
	}

	companion object {
		const val DATABASE_NAME = "trip_expense_88"
		const val DATABASE_VERSION = 1
	}
}
