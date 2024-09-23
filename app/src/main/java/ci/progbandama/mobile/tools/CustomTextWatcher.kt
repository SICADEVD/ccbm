package ci.progbandama.mobile.tools

import android.text.Editable
import android.widget.EditText

class CustomTextWatcher {

}

class NoLeadingZeroTextWatcher(private val editText: EditText, private val  onTextChange: (String)-> Unit? = {}) : android.text.TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChange(s.toString())
    }

    override fun afterTextChanged(s: Editable?) {
        if(s?.isNotEmpty() == true){
            if (s?.toString().length > 1 && s?.isNotEmpty() == true && s[0] == '0') {
                s.delete(0, 1)
            }
        }
    }
}