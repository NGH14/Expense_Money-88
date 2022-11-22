package expense.money.octo.utils // ktlint-disable filename

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentDateString(locale: Locale = Locale.US, withTime: Boolean = true): String {
	val pattern = if (withTime) "dd/MM/yyyy'T'HH:mm:ss.SSS" else "dd/MM/yyyy"
	val sdf = SimpleDateFormat(pattern, locale)

	return sdf.format(Date())
}
