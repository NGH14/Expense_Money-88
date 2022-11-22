package expense.money.octo.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.core.view.setPadding
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.EndIconMode
import expense.money.octo.R
import kotlinx.android.synthetic.main.view_input.view.*

class Input @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

	private var _styledAttrs: TypedArray

	var text: String?
		get() = input_text.text.toString()
		set(value) = input_text.setText(value)

	var inputType: Int?
		get() = input_text.inputType
		set(value) {
			value?.let { input_text.inputType = it }
		}

	init {
		LayoutInflater.from(context).inflate(R.layout.view_input, this, true)

		_styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.Input, 0, 0)
		_styledAttrs.run {
			val endIconMode = getInt(R.styleable.Input_endIconMode, TextInputLayout.END_ICON_NONE)
			val endIconDrawable = getDrawable(R.styleable.Input_endIconDrawable)
			val text = getString(R.styleable.Input_text)
			val textSize = getDimension(R.styleable.Input_android_textSize, 28F)
			val paddingHorizontal = getDimension(R.styleable.Input_textPaddingHorizontal, 36F).toInt()
			val paddingVertical = getDimension(R.styleable.Input_textPaddingVertical, 0F).toInt()
			val padding = getDimension(R.styleable.Input_textPadding, 0F).toInt()
			val hint = getString(R.styleable.Input_android_hint)
			val placeholder = getString(R.styleable.Input_placeholderText)
			val inputType = getInt(R.styleable.Input_android_inputType, InputType.TYPE_CLASS_TEXT)
			val gravity = getInt(R.styleable.Input_android_gravity, Gravity.START or Gravity.CENTER_VERTICAL)

			text_input_layout.endIconDrawable = endIconDrawable
			text_input_layout.endIconMode = endIconMode

			input_text.setText(text)
			input_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
			input_text.setPadding(padding)
			input_text.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
			input_text.inputType = inputType
			input_text.gravity = gravity
			placeholder?.let { text_input_layout.placeholderText = it }
			hint?.let { text_input_layout.hint = it }
		}

		_styledAttrs.recycle()
	}

	override fun performClick(): Boolean {
		super.performClick()
		return true
	}

	@SuppressLint("ClickableViewAccessibility")
	fun setInputOnTouchListener(l: OnTouchListener) = input_text.setOnTouchListener(l)

	fun addTextChangedListener(getWatcher: () -> TextWatcher) = input_text.addTextChangedListener(getWatcher())
}
