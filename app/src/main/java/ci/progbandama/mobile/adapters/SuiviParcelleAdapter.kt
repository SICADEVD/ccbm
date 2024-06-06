package ci.progbandama.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.adapters.SuiviParcelleAdapter.SuiviParcelleHolder
import ci.progbandama.mobile.models.SuiviParcelleModel
import kotlinx.android.synthetic.main.suivi_parcelle_items_list.view.*


class SuiviParcelleAdapter(private var suivis: List<SuiviParcelleModel>?) : RecyclerView.Adapter<SuiviParcelleHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuiviParcelleHolder {
        return SuiviParcelleHolder(LayoutInflater.from(parent.context).inflate(R.layout.suivi_parcelle_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: SuiviParcelleHolder, position: Int) {
        val suiviModel = suivis!![position]

        holder.suiviNomLabel.text = suiviModel.parcelleNom
        holder.suiviProducteur.text = suiviModel.parcelleProducteur
        holder.suiviSuperficie.text = suiviModel.parcelleSuperficie
        holder.suiviVisite.text = suiviModel.dateVisite

        if (suiviModel.isSynced) holder.imgSyncedParcelle.setImageResource(R.drawable.ic_sync_donz)
        else holder.imgSyncedParcelle.setImageResource(R.drawable.ic_sync_error)
    }



    override fun getItemCount() = suivis?.size ?: 0


    class SuiviParcelleHolder(suiviView: View) : RecyclerView.ViewHolder(suiviView) {
        val suiviNomLabel = suiviView.labelSuiviNom
        val suiviProducteur = suiviView.labelProducteurSuivi
        val suiviSuperficie = suiviView.labelSuperficeSuivi
        val imgSyncedParcelle = suiviView.imgSyncedSuivi
        val suiviVisite = suiviView.labelDateVisiteSuivi
    }
}