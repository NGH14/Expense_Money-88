package expense.money.octo.utils

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

fun DialogFragment.setWidthPercent(percentage: Int = 90) {
	val percent = percentage.toFloat() / 100
	val dm = Resources.getSystem().displayMetrics
	val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
	val percentWidth = rect.width() * percent

	dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun DialogFragment.setBgTransparent() {
	dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}
