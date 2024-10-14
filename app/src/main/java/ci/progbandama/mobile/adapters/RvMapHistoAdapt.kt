package ci.progbandama.mobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.cartographies.FarmDelimiterActivity
import ci.progbandama.mobile.databinding.ItemMappingBinding
import ci.progbandama.mobile.repositories.datas.CommonData

class RvMapHistoAdapt(private val context: Context, private val rvItems: List<CommonData>) :
    RecyclerView.Adapter<RvMapHistoAdapt.RVViewHolder>() {

    val activity = context as FarmDelimiterActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {
        val view =
                ItemMappingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_mapping, parent, false)
        return RVViewHolder(view)
    }

    override fun onBindViewHolder(holder: RVViewHolder, position: Int) {
        val item = rvItems[position]

        holder.title_histo.text = item.nom
        holder.title_histo_date.text = item.value

        holder.itemView.setOnClickListener {
            activity.onItemHistoSelected(position)
        }

    }

    override fun getItemCount(): Int {
        return rvItems.size
    }

    inner class RVViewHolder(itemView: ItemMappingBinding) : RecyclerView.ViewHolder(itemView.root) {
        val title_histo = itemView.mappHistorieLib
        val title_histo_date = itemView.mappHistorieDate
    }



}
