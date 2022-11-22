package expense.money.octo.ui.expense

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import expense.money.octo.R
import expense.money.octo.models.Expense
import expense.money.octo.models.ExpenseType
import expense.money.octo.utils.getCurrentDateString
import expense.money.octo.utils.setBgTransparent
import expense.money.octo.utils.setWidthPercent
import expense.money.octo.views.dialog.DatePickerFragment
import kotlinx.android.synthetic.main.fragment_expense_create.*

open class ExpenseCreateFragment :
	DialogFragment(R.layout.fragment_expense_create),
	DatePickerFragment.FragmentListener {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		setTypeAdapter()
		setCurrencyAdapter()

		btn_expense_create.setOnClickListener { createExpense() }
		dropdown_expense_currency.setText("USD")
		btn_expense_create_cancel.setOnClickListener { dismiss() }
		text_expense_date.setInputOnTouchListener { v, motionEvent ->
			v.performClick()
			showDateDialog(motionEvent)
		}
	}

	override fun sendFromDatePickerFragment(date: String?) {
		text_expense_date.text = date
	}

	override fun onResume() {
		super.onResume()

		setWidthPercent()
		setBgTransparent()
	}

	private fun setTypeAdapter() {
		val adapter = ArrayAdapter.createFromResource(
			requireContext(),
			R.array.expense_type,
			android.R.layout.simple_spinner_item
		)

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		dropdown_expense_type.setAdapter(adapter)
	}

	private fun setCurrencyAdapter() {
		val adapter = ArrayAdapter.createFromResource(
			requireContext(),
			R.array.expense_currency,
			android.R.layout.simple_spinner_item
		)

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		dropdown_expense_currency.setAdapter(adapter)
	}
	private fun showDateDialog(motionEvent: MotionEvent): Boolean {
		if (motionEvent.action == MotionEvent.ACTION_DOWN) {
			DatePickerFragment().show(childFragmentManager, null)
			return true
		}

		return false
	}

	private fun createExpense() {
		val foo = text_expense_amount.text?.toIntOrNull()

		val expense = Expense()
		with(expense) {
			type = ExpenseType.get(dropdown_expense_type.text.toString().lowercase())
			dateSpent = text_expense_date.text
			currency = dropdown_expense_currency.text.toString()
			amount = text_expense_amount.text?.toIntOrNull() ?: -1
			comment = text_expense_comment.text
			createdDate = getCurrentDateString(withTime = false)
		}

		(parentFragment as FragmentListener?)?.sendFromExpenseCreateFragment(expense)
		dismiss()
	}

	interface FragmentListener {
		fun sendFromExpenseCreateFragment(expense: Expense?)
	}
}
