package expense.money.octo.views.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import expense.money.octo.R

class DeleteConfirmFragment(message: String) : DialogFragment(R.layout.fragment_delete_confirm) {
	private var _message = message

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		super.onCreateDialog(savedInstanceState)

		return AlertDialog.Builder(context)
			.setNegativeButton(R.string.label_cancel) { _, _ -> dismiss() }
			.setPositiveButton(R.string.label_confirm) { _, _ -> delete() }
			.setTitle(R.string.label_confirmation)
			.setMessage(_message).create()
	}

	private fun delete() {
		(parentFragment as FragmentListener?)?.sendFromDeleteConfirmFragment(1)
		dismiss()
	}

	interface FragmentListener {
		fun sendFromDeleteConfirmFragment(status: Int)
	}
}
