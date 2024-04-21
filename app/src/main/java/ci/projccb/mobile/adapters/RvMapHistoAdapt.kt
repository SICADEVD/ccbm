package ci.projccb.mobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.cartographies.FarmDelimiterActivity
import ci.projccb.mobile.repositories.datas.CommonData
import kotlinx.android.synthetic.main.item_mapping.view.mapp_historie_date
import kotlinx.android.synthetic.main.item_mapping.view.mapp_historie_lib

class RvMapHistoAdapt(private val context: Context, private val rvItems: List<CommonData>) :
    RecyclerView.Adapter<RvMapHistoAdapt.RVViewHolder>() {

    val activity = context as FarmDelimiterActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mapping, parent, false)
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

    inner class RVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title_histo = itemView.mapp_historie_lib
        val title_histo_date = itemView.mapp_historie_date
    }



}
