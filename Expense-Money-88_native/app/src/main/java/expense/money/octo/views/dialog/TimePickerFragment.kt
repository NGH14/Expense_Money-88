package expense.money.octo.views.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import expense.money.octo.R
import java.util.*

class TimePickerFragment : DialogFragment(R.layout.fragment_time_picker), OnTimeSetListener {
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		super.onCreateDialog(savedInstanceState)

		val calendar = Calendar.getInstance()
		val hour = calendar[Calendar.HOUR_OF_DAY]
		val minute = calendar[Calendar.MINUTE]

		return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
	}

	override fun onTimeSet(timePicker: TimePicker, hour: Int, minute: Int) {
		val time = "${if (hour < 10) "0$hour" else hour}:${if (minute < 10) "0$minute" else minute}"

		(parentFragment as FragmentListener?)?.sendFromTimePickerFragment(time)
		dismiss()
	}

	interface FragmentListener {
		fun sendFromTimePickerFragment(time: String?)
	}
}
