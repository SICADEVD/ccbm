package ci.projccb.mobile.activities.forms.views

interface SelectionCompleteListener {
    fun onCompleteSelection(selectedItems: ArrayList<SearchableItem>)
    fun onCancel(selectedItems: ArrayList<SearchableItem>)
    fun onItemClicked(selectedItem:SearchableItem, which: Int, state:Boolean)
}