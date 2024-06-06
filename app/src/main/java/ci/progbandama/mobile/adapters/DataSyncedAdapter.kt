package ci.progbandama.mobile.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.*
import ci.progbandama.mobile.repositories.datas.CommonData
import ci.progbandama.mobile.tools.Commons.Companion.modifyIcColor
import kotlinx.android.synthetic.main.synced_items_list.view.*


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 18/04/2022.
 **/

@SuppressLint("All")
class DataSyncedAdapter(val context: Context, var draftedList: MutableList<CommonData>?): RecyclerView.Adapter<DataSyncedAdapter.DataSyncedHolder>() {


    class DataSyncedHolder(viewDataDrafted: View) : RecyclerView.ViewHolder(viewDataDrafted) {

        val labelNumberDraft = viewDataDrafted.labelDraftedNumberItem
        val labelDateDraft = viewDataDrafted.labelDraftedDateItem
        val linearDraftedItemDateContainer = viewDataDrafted.linearDraftedItemDateContainer
        val imageTypeDraft = viewDataDrafted.imageDraftedTypeItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataSyncedHolder {
        return DataSyncedHolder(LayoutInflater.from(context).inflate(R.layout.synced_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: DataSyncedHolder, position: Int) {
        val draftedData = draftedList!![position]

        if(draftedData == null) return

        holder.labelNumberDraft.text = draftedData.listOfValue?.get(0).toString()
        if(draftedData.listOfValue?.size!! > 1){
            holder.labelDateDraft.text = draftedData.listOfValue?.get(1).toString()
            holder.linearDraftedItemDateContainer.visibility = VISIBLE
        }
        var intentUndraftedData: Intent? = null

        when (draftedData.value?.uppercase()) {
            //for update or draft list
            "CONTENT_PRODUCTEUR",
            "PRODUCTEUR" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_farmer)
                intentUndraftedData = Intent(context, ProducteurActivity::class.java)
                intentUndraftedData.putExtra("from", if (draftedData.value.toString().lowercase() ==  "producteur") "producteur" else "content_producteur")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "INFOS_PRODUCTEUR" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_profile_producteur)
                intentUndraftedData = Intent(context, UniteAgricoleProducteurActivity::class.java)
                intentUndraftedData.putExtra("from", "infos_producteur")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            //for sync draft
            "LOCALITE" -> {
                holder.imageTypeDraft.setImageResource(android.R.drawable.ic_menu_mylocation)
                intentUndraftedData = Intent(context, LocaliteActivity::class.java)
                intentUndraftedData.putExtra("from", "localite")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "MENAGE" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_menage)
                intentUndraftedData = Intent(context, ProducteurMenageActivity::class.java)
                intentUndraftedData.putExtra("from", "menage")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "CONTENT_PARCELLE",
            "PARCELLE" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_parcel)
                intentUndraftedData = Intent(context, ParcelleActivity::class.java)
                intentUndraftedData.putExtra("from", if (draftedData.value.toString().lowercase() ==  "parcelle") "parcelle" else "content_parcelle")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "PARCELLES" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_parcel)
                intentUndraftedData = Intent(context, SuiviParcelleActivity::class.java)
                intentUndraftedData.putExtra("from", "suivi_parcelle")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "FORMATION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_formation)
                intentUndraftedData = Intent(context, FormationActivity::class.java)
                intentUndraftedData.putExtra("from", "formation")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "ESTIMATION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.estimations)
                intentUndraftedData = Intent(context, CalculEstimationActivity::class.java)
                intentUndraftedData.putExtra("from", "estimation")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "APPLICATION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_applicateurs)
                intentUndraftedData = Intent(context, SuiviApplicationActivity::class.java)
                intentUndraftedData.putExtra("from", "application")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "LIVRAISON" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.livrais_mag_sect)
                intentUndraftedData = Intent(context, LivraisonActivity::class.java)
                intentUndraftedData.putExtra("from", "livraison")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "SSRTECLMRS" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_ssrt_black)
                intentUndraftedData = Intent(context, SsrtClmsActivity::class.java)
                intentUndraftedData.putExtra("from", "ssrte")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "INSPECTION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.baseline_elevator)
                intentUndraftedData = Intent(context, InspectionActivity::class.java)
                intentUndraftedData.putExtra("from", "inspection")
                //intentUndraftedData.putExtra("update_type", "sync")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "FORMATION_VISITEUR" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.arbre_black)
                intentUndraftedData = Intent(context, VisiteurFormationActivity::class.java)
                intentUndraftedData.putExtra("from", "visiteur_formation")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "AGRO_EVALUATION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.arbre_black)
                intentUndraftedData = Intent(context, EvaluationArbreActivity::class.java)
                intentUndraftedData.putExtra("from", "AGRO_EVALUATION".lowercase())
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "AGRO_DISTRIBUTION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.distrib_arbre)
                intentUndraftedData = Intent(context, DistributionArbreActivity::class.java)
                intentUndraftedData.putExtra("from", "AGRO_DISTRIBUTION".lowercase())
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "POSTPLANTING" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.distrib_arbre)
                intentUndraftedData = Intent(context, PostPlantingEvalActivity::class.java)
                intentUndraftedData.putExtra("from", "postplanting")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
            "LIVRAISON_MAGCENTRAL" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.livrais_mag_central)
                intentUndraftedData = Intent(context, LivraisonCentralActivity::class.java)
                intentUndraftedData.putExtra("from", "suivi_livraison_central")
                intentUndraftedData.putExtra("sync_uid", draftedData.id)
            }
        }

        modifyIcColor(context, holder.imageTypeDraft, R.color.black)

        holder.itemView.setOnClickListener {
            if(intentUndraftedData!= null) {
                context.startActivity(intentUndraftedData)
                (context as Activity).finish()
            }else Toast.makeText(context, "Aucune donnée de synchronisation trouvée !", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateItems(newItems: List<CommonData>) {
        draftedList?.clear()
        draftedList?.addAll(newItems)
        notifyDataSetChanged() // Use cautiously
    }


    override fun getItemCount() = draftedList?.size ?: 0

}
