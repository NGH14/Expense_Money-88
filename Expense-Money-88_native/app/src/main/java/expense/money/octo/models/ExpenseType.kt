package expense.money.octo.models

enum class ExpenseType(val type: String) {
	FOOD("food"),
	TRAVEL("travel"),
	REPAIRS("repairs"),
	SUPPLIES("supplies"),
	TRANSPORT("transport"),
	HEATH_CARE("heath care");

	companion object {
		private val types = values().associateBy { it.type }
		infix fun get(name: String) = types[name.lowercase()]!!
	}
}
