package ci.progbandama.mobile.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.adapters.ParcelleAdapter.ParcelleHolder
import ci.progbandama.mobile.databinding.ParcelleItemsListBinding
import ci.progbandama.mobile.models.ParcelleModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase


class ParcelleAdapter(
    private val acti: Activity,
    private var parcelles: List<ParcelleModel>?
) : RecyclerView.Adapter<ParcelleHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelleHolder {
        return ParcelleHolder(
            ParcelleItemsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            LayoutInflater.from(parent.context).inflate(R.layout.parcelle_items_list, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ParcelleHolder, position: Int) {
        val parcelleModel = parcelles!![position]

        holder.parcelleNomLabel.text = if(!parcelleModel.codeParc.isNullOrEmpty()) parcelleModel.codeParc else "N/A"
        holder.parcelleProducteur.text = ProgBandRoomDatabase.getDatabase(acti)?.producteurDoa()?.getProducteurByID(parcelleModel.producteurId?.toIntOrNull())?.let { "${it.nom} ${it.prenoms}" }
        holder.parcelleSuperficie.text = parcelleModel.superficie

        if (parcelleModel.isSynced) holder.imgSyncedParcelle.setImageResource(R.drawable.ic_sync_donz)
        else holder.imgSyncedParcelle.setImageResource(R.drawable.ic_sync_error)
    }


    override fun getItemCount() = parcelles?.size ?: 0


    class ParcelleHolder(parcelleView: ParcelleItemsListBinding) : RecyclerView.ViewHolder(parcelleView.root) {
        val parcelleNomLabel = parcelleView.labelParcelleNom
        val parcelleProducteur = parcelleView.labelProducteurParcelle
        val parcelleSuperficie = parcelleView.labelSuperficeParcelle
        val imgSyncedParcelle = parcelleView.imgSyncedParcelle
    }
}
