package expense.money.octo.ui.trip

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.DialogFragment
import expense.money.octo.R
import expense.money.octo.models.Trip
import expense.money.octo.utils.setBgTransparent
import expense.money.octo.utils.setWidthPercent
import expense.money.octo.views.dialog.CalendarFragment
import kotlinx.android.synthetic.main.fragment_trip_search.*

class TripSearchFragment : DialogFragment(R.layout.fragment_trip_search), CalendarFragment.FragmentListener {

	override fun onResume() {
		super.onResume()

		setWidthPercent()
		setBgTransparent()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		trip_search_button_cancel.setOnClickListener { dismiss() }
		trip_search_button_search.setOnClickListener { search() }
		trip_search_text_date_of_trip.setInputOnTouchListener() { v, motionEvent ->
			v.performClick()
			showCalendar(motionEvent)
		}
	}

	private fun search() {
		val trip = Trip()
		with(trip) {
			val tripDate = trip_search_text_date_of_trip.text.toString()
			val tripName = trip_search_text_name.text.toString()

			if (tripDate.replace(" ", "").isNotEmpty()) dateOfTrip = tripDate
			if (tripName.replace(" ", "").isNotEmpty()) name = tripName
		}

		(parentFragment as FragmentListener?)?.sendFromTripSearchFragment(trip)
		dismiss()
	}

	private fun showCalendar(motionEvent: MotionEvent): Boolean {
		if (motionEvent.action != MotionEvent.ACTION_DOWN) return false

		CalendarFragment().show(childFragmentManager, null)

		return true
	}

	override fun sendFromCalendarFragment(date: String?) {
		trip_search_text_date_of_trip.text = date
	}

	interface FragmentListener {
		fun sendFromTripSearchFragment(trip: Trip?)
	}
}
