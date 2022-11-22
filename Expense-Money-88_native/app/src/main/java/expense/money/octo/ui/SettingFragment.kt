package expense.money.octo.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.firestore.FirebaseFirestore
import expense.money.octo.R
import expense.money.octo.database.BackupEntry
import expense.money.octo.database.DAO
import expense.money.octo.models.Backup
import java.util.*

class SettingFragment : PreferenceFragmentCompat() {

	private lateinit var contextRequired: Context
	private lateinit var db: DAO
	private var navController: NavController? = null
	private lateinit var backupOption: Preference
	private lateinit var resetOption: Preference
	private lateinit var darkModeOption: Preference
	private lateinit var aboutUsOption: Preference

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.settings, rootKey)
		requireActivity().actionBar

		contextRequired = requireContext()
		db = DAO(contextRequired)
		backupOption = getOptionsView(R.string.preference_backup_key)
		resetOption = getOptionsView(R.string.preference_reset_key)
		darkModeOption = getOptionsView(R.string.preference_dark_mode_key)
		aboutUsOption = getOptionsView(R.string.preference_about_us_key)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		backupOption.setOnPreferenceClickListener { backup() }
		resetOption.setOnPreferenceClickListener { resetDatabase() }
		darkModeOption.setOnPreferenceClickListener { toggleAppTheme() }
		aboutUsOption.setOnPreferenceClickListener { gotoAboutFragment() }
	}

	private fun gotoAboutFragment(): Boolean {
		navController ?: run { navController = findNavController() }
		navController!!.navigate(R.id.nav_about_us)
		return true
	}

	private fun gotoTermFragment(): Boolean {
		navController ?: run { navController = findNavController() }
		navController!!.navigate(R.id.nav_terms)
		return true
	}

	private fun toggleAppTheme(): Boolean {
		val modes = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

		if (modes == Configuration.UI_MODE_NIGHT_UNDEFINED) return false

		when (modes) {
			Configuration.UI_MODE_NIGHT_YES -> {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
			}
			Configuration.UI_MODE_NIGHT_NO -> {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
			}
		}

		return true
	}

	private fun backup(): Boolean {
		val trips = db.getTripList(null, null, false)
		val expenses = db.getExpenseList(null, null, false)

		if (trips.isNotEmpty()) {
			val deviceName =
				"${Build.MANUFACTURER} " +
					"${Build.MODEL} " +
					"${Build.VERSION.RELEASE} " +
					Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name
			val backup = Backup(Date(), deviceName, trips, expenses)

			FirebaseFirestore.getInstance().collection(BackupEntry.TABLE_NAME).add(backup)
				.addOnSuccessListener {
					Toast.makeText(context, R.string.notification_backup_success, Toast.LENGTH_SHORT).show()
				}
				.addOnFailureListener { e ->
					Toast.makeText(context, R.string.notification_backup_fail, Toast.LENGTH_SHORT).show()
					e.printStackTrace()
				}

			return true
		}

		Toast.makeText(context, R.string.error_empty_list, Toast.LENGTH_SHORT).show()
		return false
	}

	private fun resetDatabase(): Boolean {
		db.reset()
		Toast.makeText(context, R.string.label_reset_database, Toast.LENGTH_SHORT).show()

		return true
	}

	private fun getOptionsView(key: Int): Preference {
		return findPreference<Preference>(getString(key)).takeIf { it != null }!!
	}
}
