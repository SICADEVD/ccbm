package ci.projccb.mobile.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.lists.ParcellesListActivity
import ci.projccb.mobile.adapters.ParcelleAdapter.ParcelleHolder
import ci.projccb.mobile.models.ParcelleModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import kotlinx.android.synthetic.main.parcelle_items_list.view.*


class ParcelleAdapter(
    private val acti: Activity,
    private var parcelles: List<ParcelleModel>?
) : RecyclerView.Adapter<ParcelleHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelleHolder {
        return ParcelleHolder(LayoutInflater.from(parent.context).inflate(R.layout.parcelle_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: ParcelleHolder, position: Int) {
        val parcelleModel = parcelles!![position]

        holder.parcelleNomLabel.text = if(!parcelleModel.codeParc.isNullOrEmpty()) parcelleModel.codeParc else "N/A"
        holder.parcelleProducteur.text = CcbRoomDatabase.getDatabase(acti)?.producteurDoa()?.getProducteurByID(parcelleModel.producteurId?.toIntOrNull())?.let { "${it.nom} ${it.prenoms}" }
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
