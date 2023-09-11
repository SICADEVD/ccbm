package ci.projccb.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.LivraisonAdapter.LivraisonHolder
import ci.projccb.mobile.models.LivraisonModel
import kotlinx.android.synthetic.main.livraison_items_list.view.*


class LivraisonAdapter(private var livraisons: List<LivraisonModel>?) : RecyclerView.Adapter<LivraisonHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivraisonHolder {
        return LivraisonHolder(LayoutInflater.from(parent.context).inflate(R.layout.livraison_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: LivraisonHolder, position: Int) {
        val livraisonModel = livraisons!![position]

        holder.livraisonNombreSacsLabel.text = livraisonModel.nombreSacs
        holder.livraisonDateLabel.text = livraisonModel.dateLivre
        holder.livraisonVolumeLivreLabel.text = livraisonModel.quantiteLivre

        if (livraisonModel.isSynced) holder.imgSyncedLivraison.setImageResource(R.drawable.ic_sync_donz)
        else holder.imgSyncedLivraison.setImageResource(R.drawable.ic_sync_error)
    }



    override fun getItemCount() = livraisons?.size ?: 0


    class LivraisonHolder(livraisonView: View) : RecyclerView.ViewHolder(livraisonView) {
        val livraisonNombreSacsLabel = livraisonView.labelNombreSacsLivraison
        val livraisonDateLabel = livraisonView.labelDateLivraison
        val livraisonVolumeLivreLabel = livraisonView.labelVolumeLivreLivraison
        val imgSyncedLivraison = livraisonView.imgSyncedDoneLivraison
    }
}