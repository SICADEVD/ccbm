package ci.projccb.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import kotlinx.android.synthetic.main.infos_livraiison_prev_items_list.view.*

class PreviewItemAdapter(var prevItemList: MutableList<Map<String, String>>) : RecyclerView.Adapter<PreviewItemAdapter.PrevItemHolder>() {


    class PrevItemHolder(prevItemView: View) : RecyclerView.ViewHolder(prevItemView) {
        var labelTitrePrev = prevItemView.labelTitrePrev
        var labelTextPrev = prevItemView.labelTextPrev
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrevItemHolder {
        return PrevItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.infos_livraiison_prev_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: PrevItemHolder, position: Int) {
        val prevItem = prevItemList[position]

        for ( (key,value) in prevItem ){
            holder.labelTitrePrev.text = key
            holder.labelTextPrev.text = value
        }

    }


    fun getListOfItems():List<Map<String,String>> = prevItemList


    override fun getItemCount(): Int = prevItemList.size
}
