package expense.money.octo.models

import expense.money.octo.utils.getCurrentDateString

data class Trip(
	var id: Long = -1,
	var name: String = "",
	var destination: String = "",
	var dateOfTrip: String = "",
	var riskAssessmentRequired: Boolean = false,
	var createdDate: String? = getCurrentDateString(withTime = false),
	var description: String = "",
) : java.io.Serializable{
	override fun toString() = "$name,$dateOfTrip,$destination"
}
