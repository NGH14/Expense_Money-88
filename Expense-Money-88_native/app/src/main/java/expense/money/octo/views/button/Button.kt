package expense.money.octo.views.button

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import expense.money.octo.R

open class Button @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttrs: Int = R.attr.appViewButtonStyle,
) : MaterialButton(context, attrs, defStyleAttrs)
