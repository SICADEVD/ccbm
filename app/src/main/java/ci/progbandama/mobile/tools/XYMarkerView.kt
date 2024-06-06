package ci.progbandama.mobile.tools
import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import ci.progbandama.mobile.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

@SuppressLint("ViewConstructor")
class XYMarkerView(context: Context?, private val xAxisValueFormatter: ValueFormatter) :
    MarkerView(context, R.layout.custom_marker_view) {
    private val tvContent: TextView
    private val format: DecimalFormat

    init {
        tvContent = findViewById<TextView>(R.id.tvContent)
        format = DecimalFormat("###.0")
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight) {
        if(e == null) return
//        tvContent.text = String.format(
//            "x: %s, Total: %s",
//            format.format(e.x.toDouble()),
//            format.format(e.y.toDouble())
//        )
        tvContent.text = String.format(
            "Total: %s",
            format.format(e.y.toDouble())
        )
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}

public  class Data constructor(
    val xValue: Float,
    val yValue: Float,
    val xAxisValue: String,
    val xAxisColor: Int
)

public class ValueFormatter22 constructor() : ValueFormatter() {
    private val mFormat: DecimalFormat

    init {
        mFormat = DecimalFormat("######.0")
    }

    override fun getFormattedValue(
        value: Float,
        entry: Entry,
        dataSetIndex: Int,
        viewPortHandler: ViewPortHandler
    ): String {
        return mFormat.format(value)
    }
}