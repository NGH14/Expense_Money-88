package expense.money.octo.models

import java.util.Date

data class Backup(
	var date: Date,
	var userId: String,
	var trips: ArrayList<Trip>,
	var expenses: ArrayList<Expense>,
) : java.io.Serializable
