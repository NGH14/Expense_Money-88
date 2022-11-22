package expense.money.octo.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import expense.money.octo.models.Expense
import expense.money.octo.models.ExpenseType
import expense.money.octo.models.Trip

@Suppress("unused", "SameParameterValue")
class DAO(context: Context?) {

	private val dbHelper: DbHelper = DbHelper(context)
	private val dbWrite: SQLiteDatabase = dbHelper.readableDatabase
	private val dbRead: SQLiteDatabase = dbHelper.writableDatabase

	fun close() {
		dbRead.close()
		dbWrite.close()
	}

	fun reset() = dbHelper.onUpgrade(dbWrite, 0, 0)

	// Trip -----------------------------------------------------
	fun insertTrip(trip: Trip): Long {
		val values = getTripValues(trip)
		return dbWrite.insert(TripEntry.TABLE_NAME, null, values)
	}

	fun getTripList(trip: Trip?, orderByColumn: String?, isDesc: Boolean): ArrayList<Trip> {
		val orderBy = getOrderByString(orderByColumn, isDesc)
		var selection: String? = null
		var selectionArgs: Array<String>? = null

		if (trip != null) {
			val helper = SqlConditionHelper()

			TripEntry.run {
				helper.addCondition(" AND $COL_NAME LIKE ?", "%${trip.name}%", trip.name.trim().isNotEmpty())
				helper.addCondition(" AND $COL_DATE_OF_TRIP = ?", trip.dateOfTrip, trip.dateOfTrip.trim().isNotEmpty())
				helper.addCondition(" AND $COL_DESTINATION = ?", trip.destination, trip.destination.trim().isNotEmpty())
			}

			if (helper.selection.trim { it <= ' ' }.isNotEmpty()) selection = helper.selection.substring(5)
			selectionArgs = helper.conditionList.toTypedArray()
		}

		return getTripFromDB(null, selection, selectionArgs, null, null, orderBy)
	}

	fun getTripById(id: Long): Trip? {
		val selection = "${TripEntry.COL_ID} = ?"
		val selectionArgs = arrayOf(id.toString())

		return getTripFromDB(null, selection, selectionArgs, null, null, null)[0]
	}

	fun updateTrip(trip: Trip): Long {
		val values = getTripValues(trip)
		val selection = "${TripEntry.COL_ID} = ?"
		val selectionArgs = arrayOf<String>(java.lang.String.valueOf(trip.id))

		return dbWrite.update(TripEntry.TABLE_NAME, values, selection, selectionArgs).toLong()
	}

	fun deleteTrip(id: Long): Long {
		val tripSelection = "${TripEntry.COL_ID} = ?"
		val tripSelectionArgs = arrayOf(id.toString())

		dbWrite.delete(ExpenseEntry.TABLE_NAME, "${ExpenseEntry.COL_TRIP_ID} = ?", tripSelectionArgs).toLong()

		return dbWrite.delete(TripEntry.TABLE_NAME, tripSelection, tripSelectionArgs).toLong()
	}

	private fun getOrderByString(orderByColumn: String?, isDesc: Boolean): String? {
		if (orderByColumn == null || orderByColumn.trim { it <= ' ' }.isEmpty()) return null

		return if (isDesc) "${orderByColumn.trim { it <= ' ' }} DESC"
		else orderByColumn.trim { it <= ' ' }
	}

	private fun getTripValues(trip: Trip): ContentValues {
		val values = ContentValues()

		values.put(TripEntry.COL_NAME, trip.name)
		values.put(TripEntry.COL_DATE_OF_TRIP, trip.dateOfTrip)
		values.put(TripEntry.COL_DESCRIPTION, trip.description)
		values.put(TripEntry.COL_CREATED_DATE, trip.createdDate)
		values.put(TripEntry.COL_DESTINATION, trip.destination)
		values.put(TripEntry.COL_RISK_ASSESSMENT_REQUIRED, trip.riskAssessmentRequired)

		return values
	}

	private fun getTripFromDB(
		columns: Array<String?>? = null,
		selection: String? = null,
		selectionArgs: Array<String>? = null,
		groupBy: String? = null,
		having: String? = null,
		orderBy: String? = null,
	): ArrayList<Trip> {
		val list = ArrayList<Trip>()
		val cursor = dbRead.query(TripEntry.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy)

		while (cursor.moveToNext()) {
			list.add(
				Trip().apply {
					id = cursor.getLong(cursor.getColumnIndexOrThrow(TripEntry.COL_ID))
					name = cursor.getString(cursor.getColumnIndexOrThrow(TripEntry.COL_NAME))
					dateOfTrip = cursor.getString(cursor.getColumnIndexOrThrow(TripEntry.COL_DATE_OF_TRIP))
					destination = cursor.getString(cursor.getColumnIndexOrThrow(TripEntry.COL_DESTINATION))
					description = cursor.getString(cursor.getColumnIndexOrThrow(TripEntry.COL_DESCRIPTION))
					riskAssessmentRequired =
						cursor.getInt(cursor.getColumnIndexOrThrow(TripEntry.COL_RISK_ASSESSMENT_REQUIRED)) == 1
				}
			)
		}

		cursor.close()

		return list
	}

	// Expense -----------------------------------------------------
	fun insertExpense(expense: Expense): Long {
		val values = getExpenseValues(expense)
		return dbWrite.insert(ExpenseEntry.TABLE_NAME, null, values)
	}

	fun getExpenseList(expense: Expense?, orderByColumn: String?, isDesc: Boolean): ArrayList<Expense> {
		var selection: String? = null
		var selectionArgs: Array<String>? = null
		val orderBy = getOrderByString(orderByColumn, isDesc)

		if (expense != null && expense.id != -1L) {
			selection = "${ExpenseEntry.COL_TRIP_ID} = ?"
			selectionArgs = arrayOf(expense.tripId.toString())
		}

		return getExpenseFromDB(null, selection, selectionArgs, orderBy)
	}

	fun getExpenseById(id: Long): Expense? {
		return getExpenseFromDB(
			selection = "${ExpenseEntry.COL_ID} = ?",
			selectionArgs = arrayOf(id.toString())
		).getOrNull(0)
	}

	fun updateExpense(expense: Expense): Long {
		val values = getExpenseValues(expense)
		val selection = "${ExpenseEntry.COL_ID} = ?"
		val selectionArgs = arrayOf<String>(java.lang.String.valueOf(expense.id))

		return dbWrite.update(ExpenseEntry.TABLE_NAME, values, selection, selectionArgs).toLong()
	}

	fun deleteExpense(id: Long): Long {
		val selection = "${ExpenseEntry.COL_ID} = ?"
		val selectionArgs = arrayOf(id.toString())

		return dbWrite.delete(ExpenseEntry.TABLE_NAME, selection, selectionArgs).toLong()
	}

	private fun getExpenseValues(expense: Expense): ContentValues {
		val values = ContentValues()

		values.put(ExpenseEntry.COL_TRIP_ID, expense.tripId)
		values.put(ExpenseEntry.COL_DATE_SPENT, expense.dateSpent)
		values.put(ExpenseEntry.COL_CREATED_DATE, expense.createdDate)
		values.put(ExpenseEntry.COL_TYPE, expense.type.ordinal)
		values.put(ExpenseEntry.COL_CURRENCY, expense.currency)

		values.put(ExpenseEntry.COL_AMOUNT, expense.amount)
		values.put(ExpenseEntry.COL_COMMENT, expense.comment)

		return values
	}

	private fun getExpenseFromDB(
		columns: Array<String?>? = null,
		selection: String? = null,
		selectionArgs: Array<String>? = null,
		groupBy: String? = null,
		orderBy: String? = null,
	): ArrayList<Expense> {
		val list = ArrayList<Expense>()
		val cursor = dbRead.query(ExpenseEntry.TABLE_NAME, columns, selection, selectionArgs, groupBy, null, orderBy)

		while (cursor.moveToNext()) {
			list.add(
				Expense().apply {
					id = cursor.getLong(cursor.getColumnIndexOrThrow(ExpenseEntry.COL_ID))
					type = ExpenseType.values()[cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseEntry.COL_TYPE))]
					currency = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseEntry.COL_CURRENCY))
					dateSpent = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseEntry.COL_DATE_SPENT))
					createdDate = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseEntry.COL_CREATED_DATE))
					comment = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseEntry.COL_COMMENT))
					amount = cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseEntry.COL_AMOUNT))
					tripId = cursor.getLong(cursor.getColumnIndexOrThrow(ExpenseEntry.COL_TRIP_ID))
				}
			)
		}

		cursor.close()

		return list
	}

	private inner class SqlConditionHelper(
		superSelection: String = "",
		superConditionList: ArrayList<String> = ArrayList(),
	) {
		var selection = superSelection
		var conditionList = superConditionList

		fun addCondition(sqlCond: String, sqlArgs: String, addable: Boolean?) {
			if (addable != true) return
			selection += sqlCond
			conditionList.add(sqlArgs)
		}
	}
}
