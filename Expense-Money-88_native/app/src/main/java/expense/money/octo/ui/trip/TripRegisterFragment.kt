package expense.money.octo.ui.trip

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import expense.money.octo.R
import expense.money.octo.database.DAO
import expense.money.octo.models.Trip
import expense.money.octo.ui.trip.list.TripListFragment
import expense.money.octo.utils.serializable
import expense.money.octo.utils.setBgTransparent
import expense.money.octo.utils.setWidthPercent
import expense.money.octo.views.dialog.CalendarFragment
import kotlinx.android.synthetic.main.fragment_trip_register.*

open class TripRegisterFragment :
	DialogFragment(R.layout.fragment_trip_register),
	TripRegisterConfirmFragment.FragmentListener,
	CalendarFragment.FragmentListener {

	private lateinit var db: DAO

	override fun onResume() {
		super.onResume()

		setWidthPercent()
		setBgTransparent()
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)

		db = DAO(getContext())
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		var updateId: Long? = null

		// Update current trip
		arguments?.serializable<Trip>(ARG_PARAM_TRIP)?.run {
			input_trip_register_name.text = name
			input_trip_register_description.text = description
			input_trip_register_destination.text = destination
			input_trip_register_date_of_trip.text = dateOfTrip
			input_trip_register_risk_assessment_required.isChecked = riskAssessmentRequired
			trip_register_btn.setText(R.string.label_update)
			updateId = id
		}
		trip_register_btn.setOnClickListener {
			if (arguments != null && updateId != null) update(updateId!!)
			else register()
		}
		input_trip_register_date_of_trip.setInputOnTouchListener { v, event ->
			v.performClick()
			showCalendar(event)
		}
	}

	private fun register() {
		if (!isValidForm()) return

		val trip = getTripFromInput(-1)
		TripRegisterConfirmFragment(trip).show(childFragmentManager, null)
	}

	private fun update(id: Long) {
		if (!isValidForm()) return

		val trip = getTripFromInput(id)
		val status = db.updateTrip(trip)

		status.let { (parentFragment as FragmentListener?)?.sendFromTripRegisterFragment(it) }

		return
	}

	private fun showCalendar(event: MotionEvent): Boolean {
		if (event.action != MotionEvent.ACTION_DOWN) return false

		CalendarFragment().show(childFragmentManager, null)
		return true
	}

	private fun getTripFromInput(id: Long): Trip {
		val name = input_trip_register_name.text
		val destination = input_trip_register_destination.text
		val description = input_trip_register_description.text
		val dateOfTrip = input_trip_register_date_of_trip.text
		val rar = input_trip_register_risk_assessment_required.isChecked
		val trip = Trip().also { t ->
			t.id = id
			t.riskAssessmentRequired = rar
			destination?.let { t.destination = it }
			description?.let { t.description = it }
			dateOfTrip?.let { t.dateOfTrip = it }
			name?.let { t.name = it }
		}
		return trip
	}

	private fun isValidForm(): Boolean {
		val name = input_trip_register_name.text
		val destination = input_trip_register_destination.text
		val dateOfTrip = input_trip_register_date_of_trip.text
		val errors = mutableListOf<String>()

		if (name == null || name.isBlank() || name.isEmpty()) errors.add("Name is Required")
		if (destination == null || destination.isBlank() || destination.isEmpty()) errors.add("Destination is Required")
		if (dateOfTrip == null || dateOfTrip.isBlank() || dateOfTrip.isEmpty()) errors.add("Date of Trip is Required")

		val isValid: Boolean = errors.isEmpty()

		listview_trip_register_errors.visibility = if (isValid) View.GONE else View.VISIBLE

		if (!isValid) {
			val res = android.R.layout.simple_list_item_1
			listview_trip_register_errors.adapter = ArrayAdapter(requireContext(), res, errors)
		}

		return isValid
	}

	override fun sendFromTripRegisterConfirmFragment(status: Long) {
		if (status.toInt() == -1) {
			Toast.makeText(context, R.string.notification_create_fail, Toast.LENGTH_SHORT).show()
		} else {
			Toast.makeText(context, R.string.notification_create_success, Toast.LENGTH_SHORT).show()
			input_trip_register_name.text = ""
			input_trip_register_destination.text = ""
			input_trip_register_date_of_trip.text = ""
			input_trip_register_description.text = ""
			input_trip_register_risk_assessment_required.text = ""
		}

		val primaryNav = requireActivity().supportFragmentManager.primaryNavigationFragment
		val mainChildFragment = primaryNav?.childFragmentManager?.fragments?.get(0)
		if (mainChildFragment is TripListFragment) mainChildFragment.reloadList()
		dismiss()
	}

	override fun sendFromCalendarFragment(date: String?) {
		input_trip_register_date_of_trip.text = date
	}

	interface FragmentListener {
		fun sendFromTripRegisterFragment(status: Long)
	}

	companion object {
		const val ARG_PARAM_TRIP = "trip"
	}
}
