package ci.projccb.mobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import ci.projccb.mobile.R


class ExpandableListAdapter(private val context: Context, private val icMain: Int, private val nameMain: String) :
    BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return 1 // Only one group for "Products"
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 4 // Four child items: Add, Edit, Draft, Sync
    }

    override fun getGroup(groupPosition: Int): Any {
        return nameMain // Group name
    }

    override fun getChild(groupPosition: Int, childPosition: Int): String? {
        // Return child item name or data based on childPosition
        return when (childPosition) {
            0 -> "Ajouter nouveau"
            1 -> "Voir la liste existante"
            2 -> "Voir le brouillon"
            3 -> "Les données envoyées"
            else -> null
        }
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        // Create or reuse a view for the group header
        var root = convertView
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            root = inflater.inflate(R.layout.group_header_layout, null)

        }

        val imageView = root?.findViewById<ImageView>(R.id.ic_main_group)
        imageView?.setImageResource(icMain)
        val groupTextView = root?.findViewById<TextView>(R.id.groupTextView)
        groupTextView?.text = getGroup(groupPosition).toString()

        // Set the group name (e.g., "Products")
        return root!!
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        // Create or reuse a view for the child item
        var convertView = convertView
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.child_item_layout, null)
            val childTextView = convertView.findViewById<TextView>(R.id.childTextView)
            childTextView.text = getChild(groupPosition, childPosition).toString()

            // Set the child item icon (replace with appropriate icons)
            val childImageView = convertView.findViewById<ImageView>(R.id.childImageView)
            when (childPosition) {
                0 -> childImageView.setImageResource(R.drawable.baseline_add_black)
                1 -> childImageView.setImageResource(R.drawable.ic_edit_black)
                2 -> childImageView.setImageResource(R.drawable.baseline_drafts_black)
                3 -> childImageView.setImageResource(R.drawable.baseline_sync_black)
            }
        }

        // Set the child item name

        return convertView!!
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}
