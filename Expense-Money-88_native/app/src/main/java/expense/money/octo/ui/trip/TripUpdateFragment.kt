package expense.money.octo.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import expense.money.octo.R
import expense.money.octo.models.Trip
import expense.money.octo.utils.serializable

class TripUpdateFragment : Fragment(R.layout.fragment_trip_update), TripRegisterFragment.FragmentListener {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?,
	): View {
		val view: View = inflater.inflate(R.layout.fragment_trip_update, container, false)

		if (arguments != null) {
			val trip = requireArguments().serializable<Trip>(ARG_PARAM_TRIP)
			val bundle = Bundle()

			// Send arguments (trip info) to TripRegisterFragment.
			bundle.putSerializable(TripRegisterFragment.ARG_PARAM_TRIP, trip)
			childFragmentManager.fragments[0].arguments = bundle
		}

		return view
	}

	override fun sendFromTripRegisterFragment(status: Long) {
		when (status.toInt()) {
			0 -> Toast.makeText(context, R.string.notification_update_fail, Toast.LENGTH_SHORT).show()
			else -> {
				Toast.makeText(context, R.string.notification_update_success, Toast.LENGTH_SHORT).show()
				findNavController(requireView()).navigateUp()
			}
		}
	}

	companion object {
		const val ARG_PARAM_TRIP = "trip"
	}
}
