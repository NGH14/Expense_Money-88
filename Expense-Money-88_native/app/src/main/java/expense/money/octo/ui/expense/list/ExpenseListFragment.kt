package expense.money.octo.ui.expense.list

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import expense.money.octo.R
import expense.money.octo.database.DAO
import expense.money.octo.models.Expense
import expense.money.octo.models.Trip
import expense.money.octo.utils.serializable
import kotlinx.android.synthetic.main.fragment_expense_list.*

class ExpenseListFragment : Fragment(R.layout.fragment_expense_list) {
	private var tripDetail: Trip? = null
	private var expenseList = ArrayList<Expense>()
	private lateinit var db: DAO
	private lateinit var linearLayoutManager: LinearLayoutManager
	private lateinit var dividerItemDecoration: DividerItemDecoration

	override fun onAttach(context: Context) {
		super.onAttach(context)
		db = DAO(context)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (arguments != null) {
			tripDetail = requireArguments().serializable(ARG_PARAM_TRIP)
			val expense = Expense()
			expense.tripId = tripDetail?.id ?: -1
			expenseList = db.getExpenseList(expense, null, false)
		}

		linearLayoutManager = LinearLayoutManager(context)
		dividerItemDecoration = DividerItemDecoration(context, linearLayoutManager.orientation)
		listview_expenses.addItemDecoration(dividerItemDecoration)

		reloadList()
	}

	fun reloadList() {
		if (tripDetail == null) return

		val expense = Expense()
		expense.tripId = tripDetail?.id ?: -1
		expenseList = db.getExpenseList(expense, null, false)
		listview_expenses.adapter = ExpenseAdapter(expenseList, childFragmentManager)
		listview_expenses.layoutManager = LinearLayoutManager(context)

		listview_expenses.visibility = if (expenseList.isNotEmpty()) View.VISIBLE else View.GONE
		text_empty_notice.visibility = if (expenseList.isEmpty()) View.VISIBLE else View.GONE
	}

	companion object {
		const val ARG_PARAM_TRIP = "trip"
	}
}
