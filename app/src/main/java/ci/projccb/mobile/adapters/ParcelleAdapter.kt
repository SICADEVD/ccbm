package ci.projccb.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.ParcelleAdapter.ParcelleHolder
import ci.projccb.mobile.models.ParcelleModel
import kotlinx.android.synthetic.main.parcelle_items_list.view.*


class ParcelleAdapter(private var parcelles: List<ParcelleModel>?) : RecyclerView.Adapter<ParcelleHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelleHolder {
        return ParcelleHolder(LayoutInflater.from(parent.context).inflate(R.layout.parcelle_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: ParcelleHolder, position: Int) {
        val parcelleModel = parcelles!![position]

        holder.parcelleNomLabel.text = parcelleModel.culture
        holder.parcelleProducteur.text = parcelleModel.producteurNom
        holder.parcelleSuperficie.text = parcelleModel.superficie

        if (parcelleModel.isSynced) holder.imgSyncedParcelle.setImageResource(R.drawable.ic_sync_donz)
        else holder.imgSyncedParcelle.setImageResource(R.drawable.ic_sync_error)
    }


    override fun getItemCount() = parcelles?.size ?: 0


    class ParcelleHolder(parcelleView: View) : RecyclerView.ViewHolder(parcelleView) {
        val parcelleNomLabel = parcelleView.labelParcelleNom
        val parcelleProducteur = parcelleView.labelProducteurParcelle
        val parcelleSuperficie = parcelleView.labelSuperficeParcelle
        val imgSyncedParcelle = parcelleView.imgSyncedParcelle
    }
}
