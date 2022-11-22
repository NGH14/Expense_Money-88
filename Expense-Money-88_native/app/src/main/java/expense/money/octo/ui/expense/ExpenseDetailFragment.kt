package expense.money.octo.ui.expense

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import expense.money.octo.R
import expense.money.octo.database.DAO
import expense.money.octo.models.Expense
import expense.money.octo.models.ExpenseType
import expense.money.octo.utils.setBgTransparent
import expense.money.octo.utils.setWidthPercent
import expense.money.octo.views.dialog.DeleteConfirmFragment
import kotlinx.android.synthetic.main.fragment_expense_detail.*

class ExpenseDetailFragment(private val expenseId: Long) :
	DialogFragment(R.layout.fragment_expense_detail),
	DeleteConfirmFragment.FragmentListener {

	private val db: DAO by lazy { DAO(context) }
	private var expenseDetail: Expense? = null
	private var isEditMode = false

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		btn_expense_detail_delete.setOnClickListener { showDeleteDialog() }
		btn_expense_detail_edit.setOnClickListener { updateExpense() }
		btn_expense_detail_edit_cancel.setOnClickListener { hideEditMode() }
		btn_expense_detail_edit_toggle.setOnClickListener { toggleEditMode() }

		setTypeAdapter()
		setCurrencyAdapter()
		setInfoDetails(expenseId)
	}

	override fun onResume() {
		super.onResume()

		setWidthPercent()
		setBgTransparent()
	}

	private fun toggleEditMode() = if (isEditMode) hideEditMode() else showEditMode()

	private fun hideEditMode() {
		isEditMode = false
		layout_expense_detail_info.visibility = View.VISIBLE
		layout_expense_detail_edit.visibility = View.GONE
	}

	private fun showEditMode() {
		setEditDetails()
		isEditMode = true
		layout_expense_detail_info.visibility = View.GONE
		layout_expense_detail_edit.visibility = View.VISIBLE
	}

	private fun reloadExpenseList() {
// 		val expenseListFragment = if (fmParent.list_expenses is ExpenseListFragment) mainChildFragment else null
		val foo = parentFragment?.view?.findViewById<View>(R.id.list_expenses)

// 		expenseListFragment?.reloadList()
	}

	private fun updateExpense() {
		with(expenseDetail!!) {
			type = ExpenseType.get(dropdown_expense_detail_edit_type.text.toString())
			dateSpent = text_expense_detail_edit_date_spent.text
			comment = text_expense_detail_edit_comment.text
			currency = dropdown_expense_currency_detail.text.toString()
			amount = text_expense_detail_edit_amount.text?.toInt() ?: 0
		}

		reloadExpenseList()
		db.updateExpense(expenseDetail!!)
		setInfoDetails(expenseDetail!!.id)
		hideEditMode()
	}

	private fun setTypeAdapter() {
		val adapter = ArrayAdapter.createFromResource(
			requireContext(),
			R.array.expense_type,
			android.R.layout.simple_spinner_item
		)

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		dropdown_expense_detail_edit_type.setAdapter(adapter)
	}

	private fun setCurrencyAdapter() {
		val adapter = ArrayAdapter.createFromResource(
			requireContext(),
			R.array.expense_currency,
			android.R.layout.simple_spinner_item
		)

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		dropdown_expense_currency_detail.setAdapter(adapter)
	}

	private fun setInfoDetails(id: Long? = null) {
		val errorMessage = getString(R.string.error_not_found)
		expenseDetail = if (id != null) db.getExpenseById(id) else expenseDetail

		fun setValue(tv: TextView, value: String?) {
			tv.text = if (expenseDetail == null || value == null) errorMessage else value
		}

		setValue(expense_detail_type, expenseDetail?.type?.type)
		setValue(expense_detail_currency, expenseDetail?.currency)
		setValue(expense_detail_amount, expenseDetail?.amount?.toString())
		setValue(expense_detail_created_date, expenseDetail?.createdDate)
		setValue(expense_detail_date_spent, expenseDetail?.dateSpent)
		setValue(expense_detail_comment, expenseDetail?.comment)
	}

	private fun setEditDetails() {
		(expenseDetail ?: Expense()).run {
			dropdown_expense_detail_edit_type.setText(type.type)
			dropdown_expense_currency_detail.setText(currency.toString())

			text_expense_detail_edit_amount.text = amount.toString()
			text_expense_detail_edit_date_spent.text = dateSpent
			text_expense_detail_edit_comment.text = comment
		}
	}

	private fun showDeleteDialog() {
		val dialog = DeleteConfirmFragment(getString(R.string.notification_delete_confirm))
		dialog.show(childFragmentManager, null)
	}

	override fun sendFromDeleteConfirmFragment(status: Int) {
		val deletable = status == 1 && expenseDetail?.id != null && db.deleteExpense(expenseDetail!!.id) > 0
		val messageId = if (deletable) R.string.notification_delete_success else R.string.notification_delete_fail

		reloadExpenseList()
		dismiss()
		Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
	}
}
