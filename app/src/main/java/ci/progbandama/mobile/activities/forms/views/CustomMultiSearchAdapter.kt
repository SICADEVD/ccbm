package ci.progbandama.mobile.activities.forms.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import ci.progbandama.mobile.R
import java.util.Locale


interface OnMultiDialClickListener{
    fun onCheckClick(which:Int, checked:Boolean)
}

class CustomMultiSearchAdapter(
    private val context: Context,
    private val items: MutableList<String>,
    private val mSelection: BooleanArray,
    private val listener: OnMultiDialClickListener
) :
    ArrayAdapter<String?>(context, 0, items as List<String?>), Filterable {
    private val checkedList: MutableList<Boolean>
    private val filteredItems: MutableList<String?> = mutableListOf()

    init {
        checkedList = ArrayList()
        for (i in items.indices) {
            checkedList.add(mSelection[i])
        }
        filteredItems.addAll(items)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_multi_search_layout, parent, false)
        }
//        LogUtils.d(filteredItems)
        val checkBox = view!!.findViewById<CheckBox>(R.id.checkBox)
        if(filteredItems.get(position)!=null){
            checkBox.setText(filteredItems.get(position))
            checkBox.isChecked = checkedList[position]
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                checkedList[position] = isChecked
                listener.onCheckClick(position, isChecked)
            }
        }
        return view
    }

//    fun setChecked(position: Int, isChecked: Boolean) {
//        val item = items[position]
//        checkedMap.put(item, isChecked)
//        notifyDataSetChanged() // Notify the adapter of the change
//    }

    fun getCheckedList(): List<Boolean> {
        return checkedList
    }

    fun getCheckedItems(): List<String>? {
        val checkedItems: MutableList<String> = ArrayList()
        for (i in items.indices) {
            if (checkedList[i]) {
                checkedItems.add(items[i]) // Add the item to the checked list if it's checked
            }
        }
        return checkedItems
    }

    fun getCheckedItemsIndex(): List<Int>? {
        val checkedItems: MutableList<Int> = ArrayList()
        for (i in items.indices) {
            if (checkedList[i]) {
                checkedItems.add(i) // Add the item to the checked list if it's checked
            }
        }
        return checkedItems
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            protected override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val results = FilterResults()
                val filteredList: MutableList<String> = ArrayList()
                if (constraint == null || constraint.length == 0) {
                    // No filter implemented, return all items
                    filteredList.addAll(items)
                } else {
                    val filterPattern =
                        constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                    for (item in items) {
                        if (item.lowercase(Locale.getDefault()).contains(filterPattern)) {
                            filteredList.add(item)
                        }
                    }
                }
                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            protected override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                filteredItems.clear()
                filteredItems.addAll(results.values as MutableList<String?>)
                notifyDataSetChanged()
            }
        }
    }
}
