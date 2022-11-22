package expense.money.octo.ui.trip.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import expense.money.octo.R
import expense.money.octo.models.Trip
import expense.money.octo.ui.trip.TripDetailFragment
import java.util.*

class TripAdapter(private var originalList: ArrayList<Trip>) :
	RecyclerView.Adapter<TripAdapter.ViewHolder>(),
	Filterable {

	private lateinit var view: View
	private var filteredList = originalList
	private val itemFilter: ItemFilter by lazy { ItemFilter(this, originalList) }

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		view = inflater.inflate(R.layout.list_item_trip, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val trip = filteredList[position]
		val requiredMessage = "Risk Assessment"
		val notRequiredMessage = ""

		holder.itemName.text = trip.name
		holder.itemDateOfTrip.text = trip.dateOfTrip
		holder.itemDestination.text = trip.destination
		holder.itemRiskAssessmentRequired.text = if (trip.riskAssessmentRequired) requiredMessage
		else notRequiredMessage
	}

	override fun getItemCount(): Int = filteredList.size

	override fun getFilter(): Filter = itemFilter

	@SuppressLint("NotifyDataSetChanged")
	fun updateList(list: ArrayList<Trip>) {
		originalList = list
		filteredList = list
		notifyDataSetChanged()
	}

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private var itemLayout: LinearLayout
		var itemName: TextView
		var itemDestination: TextView
		var itemDateOfTrip: TextView
		var itemRiskAssessmentRequired: TextView

		init {
			itemName = itemView.findViewById(R.id.list_item_trip_name)
			itemDestination = itemView.findViewById(R.id.list_item_trip_destination)
			itemDateOfTrip = itemView.findViewById(R.id.list_item_trip_date)
			itemRiskAssessmentRequired = itemView.findViewById(R.id.list_item_trip_risk_assessment_required)
			itemLayout = itemView.findViewById(R.id.layout_list_item_expense)
			itemLayout.setOnClickListener(::showDetail)
		}

		private fun showDetail(view: View?) {
			try {
				val trip = filteredList[adapterPosition]
				val bundle = Bundle()

				bundle.putSerializable(TripDetailFragment.ARG_PARAM_TRIP, trip)
				findNavController(view!!).navigate(R.id.nav_trip_detail, bundle)
			} catch (_: Exception) {
			}
		}
	}

	private inner class ItemFilter(val adapter: TripAdapter, parentOriginalList: ArrayList<Trip>) : Filter() {

		private var originalList: ArrayList<Trip>
		private var filteredList: ArrayList<Trip>

		init {
			originalList = parentOriginalList
			filteredList = ArrayList()
		}

		override fun performFiltering(constraint: CharSequence): FilterResults {
			filteredList.clear()

			val results = FilterResults()
			val pattern = constraint.toString().lowercase(Locale.getDefault()).trim()

			for (trip in originalList) {
				if (trip.toString().lowercase(Locale.getDefault()).contains(pattern)) filteredList.add(trip)
			}

			results.values = filteredList
			results.count = filteredList.size

			return results
		}

		@Suppress("UNCHECKED_CAST")
		@SuppressLint("NotifyDataSetChanged")
		override fun publishResults(constraint: CharSequence, results: FilterResults) {
			(results.values as? ArrayList<Trip>)?.let { adapter.filteredList = it }
			notifyDataSetChanged()
		}
	}
}
