package ci.progbandama.mobile.adapters


import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.tools.Commons
import com.techatmosphere.expandablenavigation.model.ChildModel
import com.techatmosphere.expandablenavigation.model.HeaderModel


internal class ExpandableListAdapter(
    private val context: Context, private val listHeader: List<HeaderModel>
) : BaseExpandableListAdapter() {
    override fun getChild(groupPosition: Int, childPosititon: Int): Any {
        return listHeader[groupPosition].childModelList[childPosititon]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View, parent: ViewGroup
    ): View {
        var convertView = convertView
        val childText = getChild(groupPosition, childPosition) as ChildModel
        if (convertView == null) {
            val infalInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.navigation_list_item_ev, null, false)
        }
        val txtListChild = convertView
            .findViewById<View>(R.id.lblListItem) as TextView
        txtListChild.text = childText.title
        if (childText.isSelected) {
            txtListChild.setTypeface(null, Typeface.BOLD)
        } else {
            txtListChild.setTypeface(null, Typeface.NORMAL)
        }
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return try {
            listHeader[groupPosition].childModelList.size
        } catch (e: Exception) {
            0
        }
    }

    override fun getGroup(groupPosition: Int): Any {
        return listHeader[groupPosition]
    }

    override fun getGroupCount(): Int {
        return listHeader.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View, parent: ViewGroup
    ): View {
        var convertView = convertView
        val header = getGroup(groupPosition) as HeaderModel
        if (convertView == null) {
            val infalInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
            convertView = infalInflater.inflate(R.layout.navigation_list_group_ev, null, false)
        }
        val layoutGroup = convertView.findViewById<RelativeLayout>(R.id.layout_group)
        val lblListHeader = convertView.findViewById<View>(R.id.lblListHeader) as TextView
        lblListHeader.setTextColor(context.resources.getColor(R.color.text_color))
        val ivGroupIndicator = convertView.findViewById<ImageView>(R.id.ivGroupIndicator)
        Commons.modifyIcColor(this.context, ivGroupIndicator, R.color.text_color)
        val iconMenu = convertView.findViewById<ImageView>(R.id.icon_menu)
        Commons.modifyIcColor(context, iconMenu, R.color.text_color)
        val isNew = convertView.findViewById<TextView>(R.id.is_new)
        lblListHeader.text = header.title
        if (header.resource != -1) iconMenu.setBackgroundResource(header.resource)
        if (header.isHasChild) {
            lblListHeader.setTypeface(null, Typeface.BOLD)
            ivGroupIndicator.visibility = View.VISIBLE
        } else {
            ivGroupIndicator.visibility = View.GONE
            if (header.isSelected) {
                lblListHeader.setTypeface(null, Typeface.BOLD)
            } else {
                lblListHeader.setTypeface(null, Typeface.NORMAL)
            }
        }
        if (header.isNew) {
            isNew.visibility = View.VISIBLE
        } else {
            isNew.visibility = View.GONE
        }
        if (isExpanded) {
            ivGroupIndicator.setImageResource(com.techatmosphere.R.drawable.ic_arrow_up)
        } else {
            ivGroupIndicator.setImageResource(com.techatmosphere.R.drawable.ic_arrow_down)
        }
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}
