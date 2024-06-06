package ci.progbandama.mobile.tools

import android.text.InputType
import android.text.method.DigitsKeyListener
import android.widget.EditText


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 12/04/2022.
 **/


fun EditText.positiveNumbersOnly(){
    // numbers only soft keyboard
    inputType = InputType.TYPE_CLASS_NUMBER
    // specify the only accepted digits with point
    keyListener = DigitsKeyListener.getInstance("0123456789.")
}
