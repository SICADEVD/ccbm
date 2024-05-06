package ci.projccb.mobile.tools

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged

class CustomTextWatcher {

}

class NoLeadingZeroTextWatcher(private val editText: EditText) : android.text.TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s?.isNotEmpty() == true && s[0] == '0') {
            s.delete(0, 1)
        }
    }
}
