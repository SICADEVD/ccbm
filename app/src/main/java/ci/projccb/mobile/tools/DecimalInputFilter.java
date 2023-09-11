package ci.projccb.mobile.tools;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        String input = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());

        // If the input is empty or a decimal number
        if (input.isEmpty() || input.matches("^\\d*\\.?\\d*$")) {
            return null;  // Accept the input
        }

        // Reject the input
        return "";
    }
}
