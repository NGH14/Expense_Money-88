package expense.money.octo.ui.trip

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import expense.money.octo.R
import expense.money.octo.database.DAO
import expense.money.octo.models.Trip
import expense.money.octo.utils.setBgTransparent
import expense.money.octo.utils.setWidthPercent
import kotlinx.android.synthetic.main.fragment_trip_register_confirm.*

class TripRegisterConfirmFragment(var trip: Trip? = null) :
	DialogFragment(R.layout.fragment_trip_register_confirm) {

	private lateinit var db: DAO
	private var tripDetail: Trip = trip ?: Trip()

	override fun onAttach(context: Context) {
		super.onAttach(context)
		db = DAO(getContext())
	}

	override fun onResume() {
		super.onResume()

		setWidthPercent()
		setBgTransparent()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		tripRegisterConfirmButtonCancel.setOnClickListener { dismiss() }
		tripRegisterConfirmButtonConfirm.setOnClickListener { confirm() }

		setDetail()
	}

	private fun setDetail() {
		val errorMessage = getString(R.string.error_no_info)
		fun setValue(tv: TextView, value: String?) {
			tv.text = if (trip == null || value == null) errorMessage else value
		}

		setValue(trip_register_confirm_name, tripDetail.name)
		setValue(trip_register_confirm_destination, tripDetail.destination)
		setValue(trip_register_confirm_date_of_trip, tripDetail.dateOfTrip)
		setValue(trip_register_confirm_description, tripDetail.description)
		setValue(trip_register_confirm_risk_management_required, tripDetail.riskAssessmentRequired.toString())
	}

	private fun confirm() {
		val status = db.insertTrip(tripDetail)
		status.let { (parentFragment as FragmentListener?)?.sendFromTripRegisterConfirmFragment(it) }
		dismiss()
	}

	interface FragmentListener {
		fun sendFromTripRegisterConfirmFragment(status: Long)
	}
}
