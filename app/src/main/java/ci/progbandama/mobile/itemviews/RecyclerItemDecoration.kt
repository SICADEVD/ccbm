package ci.progbandama.mobile.itemviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import ci.progbandama.mobile.R
import ci.progbandama.mobile.interfaces.SectionCallback
import kotlin.math.max


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 10/04/2022.
 **/

@Suppress("ALL")
class RecyclerItemDecoration(context: Context, headerHeight: Int, isSticky: Boolean, callback: SectionCallback): ItemDecoration() {


    var cContext: Context? = null
    var cHeaderHeight: Int = 0
    var cIsSticky: Boolean = false
    var cSectionCallback: SectionCallback? = null
    var cHeaderView: View? = null
    var cLabelHeaderView: AppCompatTextView? = null


    init {
        this.cContext = context
        this.cHeaderHeight = headerHeight
        this.cIsSticky = isSticky
        this.cSectionCallback = callback
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val mPosition = parent.getChildAdapterPosition(view)

        if (cSectionCallback?.isSectionHeader(mPosition)!!) {
            outRect.top = cHeaderHeight
        }
    }


    fun inflateHeaderView(pRecyclerViewHeader: RecyclerView): View? {
        return LayoutInflater.from(cContext).inflate(R.layout.inspection_header_layout, pRecyclerViewHeader, false)
    }


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        if (cHeaderView == null) {
            cHeaderView = inflateHeaderView(parent)
            cLabelHeaderView = cHeaderView?.findViewById(R.id.labelHeaderInspectionItem)

            fixLayoutSize(cHeaderView, parent)
        }

        var lastTitle = ""

        for (i in 0 until parent.childCount) {
            val mChildDrawOver = parent.getChildAt(i)
            val mChildDrawOverPosition = parent.getChildAdapterPosition(mChildDrawOver)
            val title = cSectionCallback?.getSectionHeaderName(mChildDrawOverPosition)

            if (!lastTitle.equals(title, ignoreCase = true) || cSectionCallback?.isSectionHeader(mChildDrawOverPosition)!!) {
                drawHeader(c, mChildDrawOver, cHeaderView)
                lastTitle = title!!
            }
        }
    }

    private fun drawHeader(pCanvas: Canvas, pChildDrawHeader: View?, pChildView: View?) {
        pCanvas.save()

        if (cIsSticky) {
            pCanvas.translate(0F, max(0, pChildDrawHeader?.top?.minus(pChildView?.height!!) ?: 0).toFloat())
        } else {
            pCanvas.translate(0F, (pChildDrawHeader?.top!! - pChildView?.height!!).toFloat())
        }

        pChildView?.draw(pCanvas)
        pCanvas.restore()
    }


    private fun fixLayoutSize(pHeaderView: View?, pRecyclerViewLayoutSize: RecyclerView) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(pRecyclerViewLayoutSize.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(pRecyclerViewLayoutSize.height, View.MeasureSpec.UNSPECIFIED)

        try {

            val mChildWidth = ViewGroup.getChildMeasureSpec(
                widthSpec,
                pRecyclerViewLayoutSize.paddingLeft + pRecyclerViewLayoutSize.paddingRight,
                cHeaderView?.layoutParams?.width!!
            )

            val mChildHeight = ViewGroup.getChildMeasureSpec(
                heightSpec,
                pRecyclerViewLayoutSize.paddingTop+ pRecyclerViewLayoutSize.paddingBottom,
                pHeaderView?.layoutParams?.height!!
            )

            pHeaderView.measure(mChildWidth, mChildHeight)
            pHeaderView.layout(0, 0, pHeaderView.measuredWidth, pHeaderView.measuredWidth)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
