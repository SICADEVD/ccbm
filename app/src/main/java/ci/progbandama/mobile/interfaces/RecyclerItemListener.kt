package ci.progbandama.mobile.interfaces

interface RecyclerItemListener<T> {
    fun itemClick(item: T)
    fun itemClick(position: Int) {}
    fun itemSelected(position: Int, item: T)
}
