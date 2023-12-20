package ci.projccb.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.OmbrageAdapter.OmbrageHolder
import ci.projccb.mobile.models.AdapterItemModel
import ci.projccb.mobile.models.ArbreModel
import ci.projccb.mobile.models.OmbrageVarieteModel
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import com.blankj.utilcode.util.LogUtils
import kotlinx.android.synthetic.main.activite_items_list.view.deleteCultureItem
import kotlinx.android.synthetic.main.activite_items_list.view.libelle
import kotlinx.android.synthetic.main.activite_items_list.view.valuetext
import kotlinx.android.synthetic.main.ombrage_items_list.view.*
import kotlin.random.Random

class OmbrageAdapter(private var ombrages: MutableList<OmbrageVarieteModel>?, private  var libelTitle: String = "Libellé", private  var valeurTitle: String = "Valeur") : RecyclerView.Adapter<OmbrageHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OmbrageHolder {
        return OmbrageHolder(LayoutInflater.from(parent.context).inflate(R.layout.ombrage_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: OmbrageHolder, position: Int) {
        val ombrageModel = ombrages!![position]

        holder.item_title.text = libelTitle
        holder.item_value.text = valeurTitle

        holder.varieteLabel.text = ombrageModel.variete
        holder.nombreLabel.text = ombrageModel.nombre.toString()

        holder.deleteOmbre.setOnClickListener {
            LogUtils.e(Commons.TAG, "position $position")
            LogUtils.e(Commons.TAG, "Adapter position ${holder.adapterPosition}")

            try {
                if (ombrages?.size == 1) {
                    ombrages?.removeAt(0)
                    notifyItemRemoved(0)
                } else {
                    ombrages?.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    fun getOmbragesAdded(): MutableList<OmbrageVarieteModel> = ombrages!!

    fun setOmbragesList(list:MutableList<OmbrageVarieteModel>) {
        ombrages?.addAll(list)
        notifyDataSetChanged()
    }


    override fun getItemCount() = ombrages?.size ?: 0


    class OmbrageHolder(ombrageView: View) : RecyclerView.ViewHolder(ombrageView) {
        val item_title = ombrageView.item_title
        val item_value = ombrageView.item_value

        val varieteLabel = ombrageView.labelOmbrageItem
        val nombreLabel = ombrageView.nombreOmbrageItem
        val deleteOmbre = ombrageView.deleteOmbrageItem
    }
}

class OnlyFieldAdapter(private var common: MutableList<CommonData>?, private  var libelTitle: String = "Libellé") : RecyclerView.Adapter<OnlyFieldAdapter.OnlyItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlyItemHolder {
        return OnlyItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.activite_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: OnlyItemHolder, position: Int) {
        val onlyModel = common!![position]

        holder.libelle.text = libelTitle

        holder.valuetext.text = "${onlyModel.nom}"

        holder.deleteCultureItem.setOnClickListener {
            LogUtils.e(Commons.TAG, "position $position")
            LogUtils.e(Commons.TAG, "Adapter position ${holder.adapterPosition}")

            try {
                if (common?.size == 1) {
                    common?.removeAt(0)
                    notifyItemRemoved(0)
                } else {
                    common?.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    fun getOnlyItemAdded(): MutableList<CommonData> = common!!


    override fun getItemCount() = common?.size ?: 0
    fun getCurrenntList(): MutableList<CommonData>? = common


    class OnlyItemHolder(onlyView: View) : RecyclerView.ViewHolder(onlyView) {
        val libelle = onlyView.libelle

        val valuetext = onlyView.valuetext
        val deleteCultureItem = onlyView.deleteCultureItem
    }
}


class MultipleItemAdapter(private var listItem: MutableList<AdapterItemModel>?,
                          private  var libelTitle: String = "Type",
                          private  var libelTitle2: String = "Contenant",
                          private  var libelTitle3: String = "Unité",
                          private  var libelTitle4: String = "Quantité",
                          private  var valeurTitle: String = "Fréquence") : RecyclerView.Adapter<MultipleItemAdapter.MultiItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiItemHolder {
        return MultiItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.five_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: MultiItemHolder, position: Int) {
        val multiItModel = listItem!![position]

        holder.item_title.text = libelTitle
        if(!libelTitle2.isNullOrEmpty()){
            holder.item_title2.text = libelTitle2
        }else holder.item_title2.visibility = View.GONE

        if(!libelTitle3.isNullOrEmpty()){
            holder.item_title3.text = libelTitle3
        }else holder.item_title3.visibility = View.GONE

        if(!libelTitle4.isNullOrEmpty()){
            holder.item_title4.text = libelTitle4
        }else holder.item_title4.visibility = View.GONE

        if(!valeurTitle.isNullOrEmpty()){
            holder.second_title.text = valeurTitle
        }else holder.second_title.visibility = View.GONE

        holder.labelItem.text = multiItModel.value
        if(!multiItModel.value1.isNullOrEmpty()) {
            holder.labelItem2.text = multiItModel.value1
        }else holder.labelItem2.visibility = View.GONE

        if(!multiItModel.value2.isNullOrEmpty()) {
            holder.labelItem3.text = multiItModel.value2
        }else holder.labelItem3.visibility = View.GONE

        if(!multiItModel.value3.isNullOrEmpty()) {
            holder.labelItem4.text = multiItModel.value3
        }else holder.labelItem4.visibility = View.GONE

        if(!multiItModel.value4.isNullOrEmpty()) {
            holder.labelSecondItem.text = multiItModel.value4
        }else {
            holder.labelSecondItem.visibility = View.GONE
        }

        holder.deleteMulti.setOnClickListener {
            LogUtils.e(Commons.TAG, "position $position")
            LogUtils.e(Commons.TAG, "Adapter position ${holder.adapterPosition}")

            try {
                if (listItem?.size == 1) {
                    listItem?.removeAt(0)
                    notifyItemRemoved(0)
                } else {
                    listItem?.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    fun getMultiItemAdded(): MutableList<AdapterItemModel> = listItem!!

    fun setDataToRvItem(list: MutableList<AdapterItemModel>){
        listItem?.addAll(list)
        notifyDataSetChanged()
    }


    override fun getItemCount() = listItem?.size ?: 0


    class MultiItemHolder(multiItView: View) : RecyclerView.ViewHolder(multiItView) {

        val item_title = multiItView.item_title
        val item_title2 = multiItView.findViewById<TextView>(R.id.item_title2)
        val item_title3 = multiItView.findViewById<TextView>(R.id.item_title3)
        val item_title4 = multiItView.findViewById<TextView>(R.id.item_title4)
        val second_title = multiItView.findViewById<TextView>(R.id.second_title)

        val labelItem = multiItView.findViewById<TextView>(R.id.labelItem)
        val labelItem2 = multiItView.findViewById<TextView>(R.id.labelItem2)
        val labelItem3 = multiItView.findViewById<TextView>(R.id.labelItem3)
        val labelItem4 = multiItView.findViewById<TextView>(R.id.labelItem4)
        val labelSecondItem = multiItView.findViewById<TextView>(R.id.labelSecondItem)

        val deleteMulti = multiItView.findViewById<ImageView>(R.id.deleteitem)
    }
}

class DistribArbreAdapter(private var listItem: MutableList<ArbreModel>?) : RecyclerView.Adapter<DistribArbreAdapter.DistribArbreHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistribArbreHolder {
        return DistribArbreHolder(LayoutInflater.from(parent.context).inflate(R.layout.arbre_distribuee_list, parent, false))
    }


    override fun onBindViewHolder(holder: DistribArbreHolder, position: Int) {
        val multiItModel = listItem!![position]

        holder.item_id.text = multiItModel.id.toString()
        holder.item_title.text =  multiItModel.nom.plus(" /${multiItModel.nomScientifique}")
        holder.item_limit.text =  Random.nextInt(10, 50).toString()
        holder.item_distrib.setText("0")


//        holder.deleteMulti.setOnClickListener {
//            LogUtils.e(Commons.TAG, "position $position")
//            LogUtils.e(Commons.TAG, "Adapter position ${holder.adapterPosition}")
//
//            try {
//                if (listItem?.size == 1) {
//                    listItem?.removeAt(0)
//                    notifyItemRemoved(0)
//                } else {
//                    listItem?.removeAt(holder.adapterPosition)
//                    notifyItemRemoved(holder.adapterPosition)
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        }
    }


    fun getArbreListAdded(): MutableList<ArbreModel> = listItem!!


    override fun getItemCount() = listItem?.size ?: 0


    class DistribArbreHolder(multiItView: View) : RecyclerView.ViewHolder(multiItView) {

        val item_id = multiItView.findViewById<TextView>(R.id.item_dist_id)
        val item_title = multiItView.findViewById<TextView>(R.id.item_dist_title)
        val item_limit = multiItView.findViewById<TextView>(R.id.item_dist_limit)
        val item_distrib = multiItView.findViewById<EditText>(R.id.item_dist_quant)
    }
}
