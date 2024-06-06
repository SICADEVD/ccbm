package ci.progbandama.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.models.ProducteurMenageModel
import kotlinx.android.synthetic.main.menagere_items_list.view.*


class MenageAdapter(private var menages: List<ProducteurMenageModel>?) : RecyclerView.Adapter<MenageAdapter.MenageHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenageHolder {
        return MenageHolder(LayoutInflater.from(parent.context).inflate(R.layout.menagere_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: MenageHolder, position: Int) {
        val menageModel = menages!![position]

        holder.menageQuartierLabel.text = menageModel.quartier
        holder.menageProductLabel.text = menageModel.producteurNomPrenoms

        if (menageModel.isSynced) holder.imgSyncedStatus.setImageResource(R.drawable.ic_sync_donz)
        else holder.imgSyncedStatus.setImageResource(R.drawable.ic_sync_error)
    }



    override fun getItemCount() = menages?.size ?: 0


    class MenageHolder(menageView: View) : RecyclerView.ViewHolder(menageView) {
        val menageQuartierLabel = menageView.labelQuartierMenagere
        val menageProductLabel = menageView.labelProducteurNomMenagere
        val imgSyncedStatus = menageView.imgSyncedMenage
    }
}