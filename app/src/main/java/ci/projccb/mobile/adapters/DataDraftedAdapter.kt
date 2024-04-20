package ci.projccb.mobile.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.*
import ci.projccb.mobile.adapters.DataDraftedAdapter.DataDraftedHolder
import ci.projccb.mobile.models.DataDraftedModel
import ci.projccb.mobile.models.ParcelleModel
import ci.projccb.mobile.models.ProducteurModel
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.tools.Commons.Companion.modifyIcColor
import kotlinx.android.synthetic.main.drafted_items_list.view.*


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 18/04/2022.
 **/

@SuppressLint("All")
class DataDraftedAdapter(val context: Context, var draftedList: MutableList<DataDraftedModel>?): RecyclerView.Adapter<DataDraftedHolder>() {


    class DataDraftedHolder(viewDataDrafted: View) : RecyclerView.ViewHolder(viewDataDrafted) {

        val labelNumberDraft = viewDataDrafted.labelDraftedNumberItem
        val labelDateDraft = viewDataDrafted.labelDraftedDateItem
        val imageTypeDraft = viewDataDrafted.imageDraftedTypeItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataDraftedHolder {
        return DataDraftedHolder(LayoutInflater.from(context).inflate(R.layout.drafted_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: DataDraftedHolder, position: Int) {
        val draftedData = draftedList!![position]

        holder.labelNumberDraft.text = draftedData.uid.toString()
        holder.labelDateDraft.text = draftedData.dateDraft
        var intentUndraftedData: Intent? = null

        when (draftedData.typeDraft?.uppercase()) {
            //for update or draft list
            "CONTENT_PRODUCTEUR",
            "PRODUCTEUR" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_farmer)
                intentUndraftedData = Intent(context, ProducteurActivity::class.java)
                intentUndraftedData.putExtra("from", if (draftedData.typeDraft.toString().lowercase() ==  "producteur") "producteur" else "content_producteur")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "INFOS_PRODUCTEUR" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_profile_producteur)
                intentUndraftedData = Intent(context, UniteAgricoleProducteurActivity::class.java)
                intentUndraftedData.putExtra("from", "infos_producteur")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            //for sync draft
            "LOCALITE" -> {
                holder.imageTypeDraft.setImageResource(android.R.drawable.ic_menu_mylocation)
                intentUndraftedData = Intent(context, LocaliteActivity::class.java)
                intentUndraftedData.putExtra("from", "localite")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "MENAGE" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_menage)
                intentUndraftedData = Intent(context, ProducteurMenageActivity::class.java)
                intentUndraftedData.putExtra("from", "menage")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "CONTENT_PARCELLE",
            "PARCELLE" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_parcel)
                intentUndraftedData = Intent(context, ParcelleActivity::class.java)
                intentUndraftedData.putExtra("from", if (draftedData.typeDraft.toString().lowercase() ==  "parcelle") "parcelle" else "content_parcelle")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "PARCELLES" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_parcel)
                intentUndraftedData = Intent(context, SuiviParcelleActivity::class.java)
                intentUndraftedData.putExtra("from", "suivi_parcelle")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "FORMATION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_formation)
                intentUndraftedData = Intent(context, FormationActivity::class.java)
                intentUndraftedData.putExtra("from", "formation")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "ESTIMATION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.estimations)
                intentUndraftedData = Intent(context, CalculEstimationActivity::class.java)
                intentUndraftedData.putExtra("from", "calcul_estimation")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "APPLICATION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_applicateurs)
                intentUndraftedData = Intent(context, SuiviApplicationActivity::class.java)
                intentUndraftedData.putExtra("from", "application")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "LIVRAISON" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.livrais_mag_sect)
                intentUndraftedData = Intent(context, LivraisonActivity::class.java)
                intentUndraftedData.putExtra("from", "livraison")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "SSRTECLMRS" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.ic_ssrt_black)
                intentUndraftedData = Intent(context, SsrtClmsActivity::class.java)
                intentUndraftedData.putExtra("from", "ssrte")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "INSPECTION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.baseline_elevator)
                intentUndraftedData = Intent(context, InspectionActivity::class.java)
                intentUndraftedData.putExtra("from", "inspection")
                //intentUndraftedData.putExtra("update_type", "sync")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "FORMATION_VISITEUR" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.arbre_black)
                intentUndraftedData = Intent(context, VisiteurFormationActivity::class.java)
                intentUndraftedData.putExtra("from", "visiteur_formation")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "AGRO_EVALUATION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.arbre_black)
                intentUndraftedData = Intent(context, EvaluationArbreActivity::class.java)
                intentUndraftedData.putExtra("from", "AGRO_EVALUATION".lowercase())
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "AGRO_DISTRIBUTION" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.distrib_arbre)
                intentUndraftedData = Intent(context, DistributionArbreActivity::class.java)
                intentUndraftedData.putExtra("from", "AGRO_DISTRIBUTION".lowercase())
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "POSTPLANTING" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.distrib_arbre)
                intentUndraftedData = Intent(context, PostPlantingEvalActivity::class.java)
                intentUndraftedData.putExtra("from", "postplanting")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
            "LIVRAISON_MAGCENTRAL" -> {
                holder.imageTypeDraft.setImageResource(R.drawable.livrais_mag_central)
                intentUndraftedData = Intent(context, LivraisonCentralActivity::class.java)
                intentUndraftedData.putExtra("from", "suivi_livraison_central")
                intentUndraftedData.putExtra("drafted_uid", draftedData.uid)
            }
        }

        modifyIcColor(context, holder.imageTypeDraft, R.color.black)

        when (draftedData.typeDraft?.uppercase()) {
            "CONTENT_PRODUCTEUR" ->  {
                val producteurContent = ApiClient.gson.fromJson(draftedData.datas, ProducteurModel::class.java)
                holder.labelNumberDraft.text = "${producteurContent.nom ?: ""} ${producteurContent.prenoms ?: ""} (${if (producteurContent.codeProdApp.isNullOrBlank()) context.getString(R.string.inconnu) else producteurContent.codeProdApp})"
            }
            "CONTENT_PARCELLE" ->  {
                val parcelleContent = ApiClient.gson.fromJson(draftedData.datas, ParcelleModel::class.java)
                holder.labelNumberDraft.text = "${parcelleContent.producteurNom} (${parcelleContent.codeParc})"
            }
            else ->  holder.labelNumberDraft.text = draftedData.uid.toString()
        }

        holder.itemView.setOnClickListener {
            if(intentUndraftedData!= null) {
                context.startActivity(intentUndraftedData)
                (context as Activity).finish()
            }else Toast.makeText(context, "Aucun brouillon définit !", Toast.LENGTH_SHORT).show()
        }
    }


    override fun getItemCount() = draftedList?.size ?: 0

}
