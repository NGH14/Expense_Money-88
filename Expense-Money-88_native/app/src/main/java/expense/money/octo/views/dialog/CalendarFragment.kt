package expense.money.octo.views.dialog

import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import androidx.fragment.app.DialogFragment
import expense.money.octo.R
import kotlinx.android.synthetic.main.fragment_calendar.*

class CalendarFragment : DialogFragment(R.layout.fragment_calendar) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		fmCalendarCalendar.setOnDateChangeListener { _: CalendarView?, year: Int, month: Int, day: Int ->
			handleDateChange(year, month, day)
		}
	}

	private fun handleDateChange(year: Int, month: Int, day: Int) {
		val theMonthAfter = month + 1
		val date = "${if (day < 10) "0$day" else day}/" +
			"${if (theMonthAfter < 10) "0$theMonthAfter" else theMonthAfter}/" +
			year

		(parentFragment as FragmentListener?)?.sendFromCalendarFragment(date)

		dismiss()
	}

	interface FragmentListener {
		fun sendFromCalendarFragment(date: String?)
	}
}
