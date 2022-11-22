package expense.money.octo.ui.trip

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import expense.money.octo.R
import expense.money.octo.database.DAO
import expense.money.octo.models.Expense
import expense.money.octo.models.Trip
import expense.money.octo.ui.expense.ExpenseCreateFragment
import expense.money.octo.ui.expense.list.ExpenseListFragment
import expense.money.octo.utils.serializable
import expense.money.octo.views.dialog.DeleteConfirmFragment
import kotlinx.android.synthetic.main.fragment_trip_detail.*

class TripDetailFragment :
	Fragment(R.layout.fragment_trip_detail),
	DeleteConfirmFragment.FragmentListener,
	ExpenseCreateFragment.FragmentListener,
	MenuProvider {

	private lateinit var navController: NavController
	private lateinit var db: DAO
	private var tripDetail: Trip? = null

	override fun onAttach(context: Context) {
		super.onAttach(context)
		db = DAO(getContext())
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		btn_expense_add.setOnClickListener { showAddExpenseCreate() }

		navController = requireParentFragment().findNavController()

		val menuHost: MenuHost = requireActivity()
		menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

		showDetails()
		showExpenseList()
	}

	override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.menu_in_detail, menu)
	}

	override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
		when (menuItem.itemId) {
			R.id.tripUpdateFragment -> showUpdateDialog()
			R.id.tripDeleteFragment -> showDeleteConfirmDialog()
			android.R.id.home -> navController.navigateUp()
		}

		return true
	}

	override fun sendFromDeleteConfirmFragment(status: Int) {
		if (status == 1 && tripDetail?.id != null && db.deleteTrip(tripDetail!!.id) > 0) {
			Toast.makeText(context, R.string.notification_delete_success, Toast.LENGTH_SHORT).show()
			view?.let { findNavController(it).navigateUp() }

			return
		}

		Toast.makeText(context, R.string.notification_delete_fail, Toast.LENGTH_SHORT).show()
	}

	override fun sendFromExpenseCreateFragment(expense: Expense?) {
		if (expense == null) return

		tripDetail?.id?.let { expense.tripId = it }

		val id = db.insertExpense(expense)
		val text = if (id == -1L) R.string.notification_create_fail else R.string.notification_create_success
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

		reloadExpenseList()
	}

	private fun showDetails() {
		val detailBundle = requireArguments().serializable<Trip>(ARG_PARAM_TRIP)
		val errorMessage = getString(R.string.error_not_found)
		tripDetail = db.getTripById(detailBundle!!.id)

		fun setValue(tv: TextView, value: String?) {
			tv.text = if (tripDetail == null || value == null) errorMessage else value
		}
		(activity as AppCompatActivity).supportActionBar?.title = tripDetail?.name

		setValue(trip_detail_name, tripDetail?.name)
		setValue(trip_detail_destination, tripDetail?.destination)
		setValue(trip_detail_description, tripDetail?.description)
		setValue(trip_detail_risk_assessment, tripDetail?.riskAssessmentRequired?.toString())
		setValue(trip_detail_date_of_trip, tripDetail?.dateOfTrip)
		setValue(trip_detail_created_date, tripDetail?.createdDate)
	}

	private fun showExpenseList() {
		if (arguments == null) return

		val bundle = Bundle()

		bundle.putSerializable(ExpenseListFragment.ARG_PARAM_TRIP, tripDetail)
		childFragmentManager.fragments[0].arguments = bundle
	}

	private fun showUpdateDialog() {
		var bundle: Bundle? = null

		if (tripDetail != null) {
			bundle = Bundle()
			bundle.putSerializable(TripUpdateFragment.ARG_PARAM_TRIP, tripDetail)
		}

		navController.navigate(R.id.nav_trip_update, bundle)
	}

	private fun showDeleteConfirmDialog() {
		DeleteConfirmFragment(getString(R.string.notification_delete_confirm)).show(childFragmentManager, null)
	}

	private fun showAddExpenseCreate() = ExpenseCreateFragment().show(childFragmentManager, null)

	private fun reloadExpenseList() {
		val bundle = Bundle()

		bundle.putSerializable(ExpenseListFragment.ARG_PARAM_TRIP, tripDetail)
		childFragmentManager.beginTransaction().setReorderingAllowed(true)
			.replace(R.id.list_expenses, ExpenseListFragment::class.java, bundle).commit()
	}

	companion object {
		const val ARG_PARAM_TRIP = "trip"
	}
}
