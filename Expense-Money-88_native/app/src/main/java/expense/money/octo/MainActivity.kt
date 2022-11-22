package expense.money.octo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import com.google.android.material.floatingactionbutton.FloatingActionButton
import expense.money.octo.ui.trip.TripRegisterFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_content.view.*
import kotlinx.android.synthetic.main.app_nav_bar.*
import kotlinx.android.synthetic.main.app_nav_bar.view.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
	private lateinit var appBarConfiguration: AppBarConfiguration

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val contentView = include_app_content.main_nav_host
		val bottomNavView = include_app_nav_bar.navbar_nav_view
		val navHostFragment = contentView.getFragment<NavHostFragment>()
		val navController = navHostFragment.navController

		appBarConfiguration = AppBarConfiguration.Builder(setOf(R.id.nav_home, R.id.nav_settings)).build()

		setupWithNavController(bottomNavView, navController)
		setupActionBarWithNavController(this, navController, appBarConfiguration)
		setupNavBarIgnoreDestination(navController)

		setupSubmitAction()
	}

	override fun onSupportNavigateUp(): Boolean {
		return findNavController(R.id.main_nav_host).navigateUp(appBarConfiguration)
	}

	private fun setupNavBarIgnoreDestination(navController: NavController) {
		val navBar = navbar_bottom
		val navBarCenterBtn = navbar_center_btn

		fun setVis(viewType: Int) {
			navBar.visibility = viewType
			navBarCenterBtn.visibility = viewType
		}

		navController.addOnDestinationChangedListener { _, des, _ ->
			when (des.id) {
				R.id.nav_about_us -> setVis(View.GONE)
				else -> setVis(View.VISIBLE)
			}
		}
	}

	// add click event to the bottom center function button
	private fun setupSubmitAction() {
		val navCenterBtn = findViewById<FloatingActionButton>(R.id.navbar_center_btn)

		navCenterBtn?.let {
			it.setOnClickListener {
				val navController = findNavController(R.id.main_nav_host)
				val currentDesId = navController.currentDestination?.id
				val startDesId = navController.graph.startDestinationId

				if (currentDesId != startDesId) navController.navigate(R.id.nav_home)

				showRegisterTripDialog()
			}
		}
	}

	private fun showRegisterTripDialog() {
		TripRegisterFragment().show(supportFragmentManager, null)
	}
}
