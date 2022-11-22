package expense.money.octo.ui.expense.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import expense.money.octo.R
import expense.money.octo.models.Expense
import expense.money.octo.ui.expense.ExpenseDetailFragment
import java.util.*

class ExpenseAdapter(private var originalList: ArrayList<Expense>, private val fragmentManager: FragmentManager) :
	RecyclerView.Adapter<ExpenseAdapter.ViewHolder>(),
	Filterable {

	private var filteredList = originalList
	private val itemFilter: ItemFilter by lazy { ItemFilter(this, originalList) }

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val view = inflater.inflate(R.layout.list_item_expense, parent, false)

		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val expense = filteredList[position]

		holder.itemDateSpent.text = expense.dateSpent
		holder.itemAmount.text = expense.amount.toString()
		holder.itemType.text = expense.type.type

		val currencyIcon = if (expense.currency == "USD") R.drawable.ic_dollar_sign else R.drawable.vietnam_dong
		holder.itemIcon.setCompoundDrawablesWithIntrinsicBounds(currencyIcon, 0, holder.itemAmount.width, 0)
	}

	override fun getItemCount(): Int = filteredList.size

	override fun getFilter(): Filter = itemFilter

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private var itemLayout: LinearLayout
		var itemType: TextView
		var itemDateSpent: TextView
		var itemAmount: TextView
		var itemIcon: TextView

		init {
			itemType = itemView.findViewById(R.id.list_item_expense_type)
			itemIcon = itemView.findViewById(R.id.list_item_expense_currency)
			itemAmount = itemView.findViewById(R.id.list_item_expense_amount)
			itemDateSpent = itemView.findViewById(R.id.list_item_expense_date_spent)
			itemLayout = itemView.findViewById(R.id.layout_list_item_expense)
			itemLayout.setOnClickListener { showDetail() }
		}

		private fun showDetail() {
			val expense = filteredList[adapterPosition]
			ExpenseDetailFragment(expense.id).show(fragmentManager, null)
		}
	}

	private inner class ItemFilter(val adapter: ExpenseAdapter, parentOriginalList: ArrayList<Expense>) : Filter() {

		private var originalList: ArrayList<Expense>
		private var filteredList: ArrayList<Expense>

		init {
			originalList = parentOriginalList
			filteredList = ArrayList()
		}

		override fun performFiltering(constraint: CharSequence): FilterResults {
			filteredList.clear()

			val results = FilterResults()
			val pattern = constraint.toString().lowercase(Locale.getDefault()).trim()

			for (expense in originalList) {
				if (expense.toString().lowercase(Locale.getDefault()).contains(pattern)) {
					filteredList.add(expense)
				}
			}

			results.values = filteredList
			results.count = filteredList.size

			return results
		}

		@Suppress("UNCHECKED_CAST")
		@SuppressLint("NotifyDataSetChanged")
		override fun publishResults(constraint: CharSequence, results: FilterResults) {
			if (results.count == 0) return

			adapter.filteredList.clear()
			adapter.filteredList = results.values as ArrayList<Expense>
			notifyDataSetChanged()
		}
	}
}
