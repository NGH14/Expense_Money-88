package expense.money.octo.ui.trip.list

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import expense.money.octo.R
import expense.money.octo.database.DAO
import expense.money.octo.models.Trip
import expense.money.octo.ui.trip.TripSearchFragment
import kotlinx.android.synthetic.main.fragment_trip_list.*

class TripListFragment : Fragment(R.layout.fragment_trip_list), TripSearchFragment.FragmentListener {
	private lateinit var db: DAO
	private lateinit var tripAdapter: TripAdapter
	private var tripList = ArrayList<Trip>()

	override fun onAttach(context: Context) {
		super.onAttach(context)
		db = DAO(context)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		tripAdapter = TripAdapter(tripList)
		(activity as AppCompatActivity).supportActionBar?.title = "List Business Trips"

		listview_trip_list_trips.adapter = tripAdapter
		listview_trip_list_trips.layoutManager = LinearLayoutManager(context)
		DividerItemDecoration(context, LinearLayoutManager(context).orientation).let {
			listview_trip_list_trips.addItemDecoration(it)
		}

		button_trip_list_search.setOnClickListener { showSearchDialog() }
		input_trip_list_filter.addTextChangedListener { filter() }
		input_trip_list_filter.setOnKeyListener { _, keyCode, event ->
			if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) filter()
			true
		}
	}

	override fun onResume() {
		reloadList()
		super.onResume()
	}

	fun reloadList(trip: Trip? = null) {
		tripList = db.getTripList(trip, null, false)
		tripAdapter.updateList(tripList)

		val listIsEmpty = tripList.isEmpty()
		trip_list_empty_notice_text.visibility = if (listIsEmpty) View.VISIBLE else View.GONE
		listview_trip_list_trips.visibility = if (listIsEmpty) View.GONE else View.VISIBLE
	}

	private fun filter(): TextWatcher {
		return object : TextWatcher {
			override fun afterTextChanged(editable: Editable) {}
			override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}

			@SuppressLint("NotifyDataSetChanged")
			override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
				reloadList()
				tripAdapter.filter.filter(charSequence.toString())
			}
		}
	}

	private fun resetSearch() {
		input_trip_list_filter.text = ""
		reloadList()
	}

	private fun showSearchDialog() = TripSearchFragment().show(childFragmentManager, null)

	override fun sendFromTripSearchFragment(trip: Trip?) = reloadList(trip)
}
