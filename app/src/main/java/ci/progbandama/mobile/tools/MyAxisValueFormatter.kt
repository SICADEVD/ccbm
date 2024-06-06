package ci.progbandama.mobile.tools

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat


class MyAxisValueFormatter : ValueFormatter() {
    private val mFormat: DecimalFormat

    init {
        mFormat = DecimalFormat("###,###,###,##0.0")
    }

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        return mFormat.format(value.toDouble()) + " $"
    }
}


class MyAxisXValueFormatter : ValueFormatter() {
    private val mFormat: DecimalFormat

    init {
        mFormat = DecimalFormat("###,###,###,##0.0")
    }

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        return mFormat.format(value.toDouble()) + " $"
    }
}