package expense.money.octo.views.dialog

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import expense.money.octo.R
import java.util.*

class DatePickerFragment : DialogFragment(R.layout.fragment_date_picker), OnDateSetListener {
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		super.onCreateDialog(savedInstanceState)

		val calendar = Calendar.getInstance()
		val year = calendar[Calendar.YEAR]
		val month = calendar[Calendar.MONTH]
		val day = calendar[Calendar.DAY_OF_MONTH]

		return DatePickerDialog(requireContext(), this, year, month, day)
	}

	override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
		val theMonthAfter = month + 1
		val date = "${if (day < 10) "0$day" else day}/" +
			"${if (theMonthAfter < 10) "0$theMonthAfter" else theMonthAfter}/" +
			year

		(parentFragment as FragmentListener?)?.sendFromDatePickerFragment(date)
		dismiss()
	}

	interface FragmentListener {
		fun sendFromDatePickerFragment(date: String?)
	}
}
