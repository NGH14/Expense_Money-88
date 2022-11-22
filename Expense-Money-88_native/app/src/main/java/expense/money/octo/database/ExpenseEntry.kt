package expense.money.octo.database

object ExpenseEntry {
	const val TABLE_NAME = "expense"

	const val COL_ID = "id"
	const val COL_COMMENT = "comment"
	const val COL_DATE_SPENT = "date_spent"
	const val COL_CREATED_DATE = "created_date"
	const val COL_TYPE = "type"
	const val COL_AMOUNT = "amount"
	const val COL_TRIP_ID = "trip_id"
	const val COL_CURRENCY = "currency"

	const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
	const val SQL_CREATE_TABLE = """
			CREATE TABLE IF NOT EXISTS $TABLE_NAME (
				$COL_ID INTEGER PRIMARY KEY,
				$COL_COMMENT TEXT NOT NULL,
				$COL_DATE_SPENT TEXT NOT NULL,
				$COL_CREATED_DATE TEXT NOT NULL,
				$COL_TYPE INTEGER NOT NULL,
				$COL_AMOUNT INTEGER NOT NULL,
				$COL_TRIP_ID INTEGER NOT NULL,
				$COL_CURRENCY TEXT NOT NULL,

				FOREIGN KEY($COL_TRIP_ID) REFERENCES ${TripEntry.TABLE_NAME}(${TripEntry.COL_ID})
        )"""
}
