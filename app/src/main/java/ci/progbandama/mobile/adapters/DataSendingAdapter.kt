package ci.progbandama.mobile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.activities.forms.*
import ci.progbandama.mobile.databinding.MenagereItemsListBinding
import ci.progbandama.mobile.repositories.datas.CommonData


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 18/04/2022.
 **/

@SuppressLint("All")
class DataSendingAdapter(val context: Context, var draftedList: MutableList<CommonData>?): RecyclerView.Adapter<DataSendingAdapter.DataSyncedHolder>() {


    class DataSyncedHolder(viewDataDrafted: MenagereItemsListBinding) : RecyclerView.ViewHolder(viewDataDrafted.root) {

        val item_title = viewDataDrafted.itemTitle
        val labelQuartierMenagere = viewDataDrafted.labelQuartierMenagere
        val item2_title = viewDataDrafted.item2Title
        val labelProducteurNomMenagere = viewDataDrafted.labelProducteurNomMenagere
        val imgSyncedStatus = viewDataDrafted.imgSyncedMenage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataSyncedHolder {
        return DataSyncedHolder(
            MenagereItemsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            LayoutInflater.from(context).inflate(R.layout.menagere_items_list, parent, false)
        )
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

        if (draftedData.listOfValue?.size!! >= 5) {
            if( draftedData.listOfValue?.get(4).isNullOrEmpty() == false ) holder.imgSyncedStatus.setImageResource(R.drawable.ic_sync_donz)
        }
        else holder.imgSyncedStatus.setImageResource(R.drawable.ic_sync_error)
//draftedData.value?.uppercase()

//        modifyIcColor(context, holder.imageTypeDraft, R.color.black)

    }

    override fun getItemCount() = draftedList?.size ?: 0

}
