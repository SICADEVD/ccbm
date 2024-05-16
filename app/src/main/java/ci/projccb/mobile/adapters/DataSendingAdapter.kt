package ci.projccb.mobile.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.*
import ci.projccb.mobile.models.DataDraftedModel
import ci.projccb.mobile.models.ParcelleModel
import ci.projccb.mobile.models.ProducteurModel
import ci.projccb.mobile.repositories.apis.ApiClient
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons.Companion.modifyIcColor
import kotlinx.android.synthetic.main.menagere_items_list.view.*


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 18/04/2022.
 **/

@SuppressLint("All")
class DataSendingAdapter(val context: Context, var draftedList: MutableList<CommonData>?): RecyclerView.Adapter<DataSendingAdapter.DataSyncedHolder>() {


    class DataSyncedHolder(viewDataDrafted: View) : RecyclerView.ViewHolder(viewDataDrafted) {

        val item_title = viewDataDrafted.item_title
        val labelQuartierMenagere = viewDataDrafted.labelQuartierMenagere
        val item2_title = viewDataDrafted.item2_title
        val labelProducteurNomMenagere = viewDataDrafted.labelProducteurNomMenagere
        val imgSyncedStatus = viewDataDrafted.imgSyncedMenage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataSyncedHolder {
        return DataSyncedHolder(LayoutInflater.from(context).inflate(R.layout.menagere_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: DataSyncedHolder, position: Int) {
        val draftedData = draftedList!![position]

        if(draftedData == null) return

        holder.item_title.text = draftedData.listOfValue?.get(0).toString()
        holder.labelQuartierMenagere.text = draftedData.listOfValue?.get(1).toString()
        if(draftedData.listOfValue?.size!! > 2){
            holder.item2_title.text = draftedData.listOfValue?.get(2).toString()
            holder.labelProducteurNomMenagere.text = draftedData.listOfValue?.get(3).toString()
        }

        if (draftedData.listOfValue?.get(4).isNullOrEmpty() == false) holder.imgSyncedStatus.setImageResource(R.drawable.ic_sync_donz)
        else holder.imgSyncedStatus.setImageResource(R.drawable.ic_sync_error)
//draftedData.value?.uppercase()

//        modifyIcColor(context, holder.imageTypeDraft, R.color.black)

    }

    override fun getItemCount() = draftedList?.size ?: 0

}
