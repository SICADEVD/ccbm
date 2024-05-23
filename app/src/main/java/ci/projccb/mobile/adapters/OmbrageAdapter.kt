package ci.projccb.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.OmbrageAdapter.OmbrageHolder
import ci.projccb.mobile.models.AdapterItemModel
import ci.projccb.mobile.models.ArbreModel
import ci.projccb.mobile.models.ListeEspeceArbrePostPlantModel
import ci.projccb.mobile.models.OmbrageVarieteModel
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import com.blankj.utilcode.util.LogUtils
import kotlinx.android.synthetic.main.activite_items_list.view.deleteCultureItem
import kotlinx.android.synthetic.main.activite_items_list.view.libelle
import kotlinx.android.synthetic.main.activite_items_list.view.valuetext
import kotlinx.android.synthetic.main.ombrage_items_list.view.*

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

    fun setupList(list: MutableList<CommonData>){
        common?.addAll(list)
        notifyDataSetChanged()
    }


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
        }else holder.labelSecondItem.visibility = View.GONE



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

class SixItemAdapter(private var listItem: MutableList<AdapterItemModel>?,
                          private  var libelTitle: String = "Pesticide",
                          private  var libelTitle2: String = "Toxicicologie",
                          private  var libelTitle3: String = "Nom commercial",
                          private  var libelTitle4: String = "Matières Actives",
                          private  var libelTitle5: String = "Dose",
                          private  var valeurTitle: String = "Fréquence") : RecyclerView.Adapter<SixItemAdapter.SixItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SixItemHolder {
        return SixItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.six_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: SixItemHolder, position: Int) {
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

        if(!libelTitle5.isNullOrEmpty()){
            holder.item_title5.text = libelTitle5
        }else holder.item_title5.visibility = View.GONE

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
            holder.labelItem5.text = multiItModel.value4
        }else holder.labelItem5.visibility = View.GONE

        if(!multiItModel.value5.isNullOrEmpty()) {
            holder.labelSecondItem.text = multiItModel.value5
        }else holder.labelSecondItem.visibility = View.GONE



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


    class SixItemHolder(multiItView: View) : RecyclerView.ViewHolder(multiItView) {

        val item_title = multiItView.item_title
        val item_title2 = multiItView.findViewById<TextView>(R.id.item_title2)
        val item_title3 = multiItView.findViewById<TextView>(R.id.item_title3)
        val item_title4 = multiItView.findViewById<TextView>(R.id.item_title4)
        val item_title5 = multiItView.findViewById<TextView>(R.id.item_title5)
        val second_title = multiItView.findViewById<TextView>(R.id.second_title)

        val labelItem = multiItView.findViewById<TextView>(R.id.labelItem)
        val labelItem2 = multiItView.findViewById<TextView>(R.id.labelItem2)
        val labelItem3 = multiItView.findViewById<TextView>(R.id.labelItem3)
        val labelItem4 = multiItView.findViewById<TextView>(R.id.labelItem4)
        val labelItem5 = multiItView.findViewById<TextView>(R.id.labelItem5)
        val labelSecondItem = multiItView.findViewById<TextView>(R.id.labelSecondItem)

        val deleteMulti = multiItView.findViewById<ImageView>(R.id.deleteitem)
    }
}

class NineItemAdapter(private var listItem: MutableList<AdapterItemModel>?,
                     private  var libelTitle: String = "Pesticide",
                     private  var libelTitle2: String = "Toxicicologie",
                     private  var libelTitle3: String = "Nom commercial",
                     private  var libelTitle4: String = "Matières Actives",
                     private  var libelTitle5: String = "Dose",
                     private  var libelTitle6: String = "Unite Dose",
                     private  var libelTitle7: String = "Quantité",
                     private  var libelTitle8: String = "Unite Quantité",
                     private  var valeurTitle: String = "Fréquence"
) : RecyclerView.Adapter<NineItemAdapter.NineItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NineItemHolder {
        return NineItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.nine_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: NineItemHolder, position: Int) {
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

        if(!libelTitle5.isNullOrEmpty()){
            holder.item_title5.text = libelTitle5
        }else holder.item_title5.visibility = View.GONE

        if(!libelTitle5.isNullOrEmpty()){
            holder.item_title5.text = libelTitle5
        }else holder.item_title5.visibility = View.GONE

        if(!libelTitle6.isNullOrEmpty()){
            holder.item_title6.text = libelTitle6
        }else holder.item_title6.visibility = View.GONE

        if(!libelTitle7.isNullOrEmpty()){
            holder.item_title7.text = libelTitle7
        }else holder.item_title7.visibility = View.GONE

        if(!libelTitle8.isNullOrEmpty()){
            holder.item_title8.text = libelTitle8
        }else holder.item_title8.visibility = View.GONE

        if(!valeurTitle.isNullOrEmpty()){
            holder.second_title.text = valeurTitle
        }else holder.second_title.visibility = View.GONE

        // ALL VALUE OF ITEM

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
            holder.labelItem5.text = multiItModel.value4
        }else holder.labelItem5.visibility = View.GONE

        if(!multiItModel.value5.isNullOrEmpty()) {
            holder.labelItem6.text = multiItModel.value5
        }else holder.labelItem6.visibility = View.GONE

        if(!multiItModel.value6.isNullOrEmpty()) {
            holder.labelItem7.text = multiItModel.value6
        }else holder.labelItem7.visibility = View.GONE

        if(!multiItModel.value7.isNullOrEmpty()) {
            holder.labelItem8.text = multiItModel.value7
        }else holder.labelItem8.visibility = View.GONE

        if(!multiItModel.value8.isNullOrEmpty()) {
            holder.labelSecondItem.text = multiItModel.value8
        }else holder.labelSecondItem.visibility = View.GONE



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


    class NineItemHolder(multiItView: View) : RecyclerView.ViewHolder(multiItView) {

        val item_title = multiItView.item_title
        val item_title2 = multiItView.findViewById<TextView>(R.id.item_title2)
        val item_title3 = multiItView.findViewById<TextView>(R.id.item_title3)
        val item_title4 = multiItView.findViewById<TextView>(R.id.item_title4)
        val item_title5 = multiItView.findViewById<TextView>(R.id.item_title5)
        val item_title6 = multiItView.findViewById<TextView>(R.id.item_title6)
        val item_title7 = multiItView.findViewById<TextView>(R.id.item_title7)
        val item_title8 = multiItView.findViewById<TextView>(R.id.item_title8)
        val second_title = multiItView.findViewById<TextView>(R.id.second_title)

        val labelItem = multiItView.findViewById<TextView>(R.id.labelItem)
        val labelItem2 = multiItView.findViewById<TextView>(R.id.labelItem2)
        val labelItem3 = multiItView.findViewById<TextView>(R.id.labelItem3)
        val labelItem4 = multiItView.findViewById<TextView>(R.id.labelItem4)
        val labelItem5 = multiItView.findViewById<TextView>(R.id.labelItem5)
        val labelItem6 = multiItView.findViewById<TextView>(R.id.labelItem6)
        val labelItem7 = multiItView.findViewById<TextView>(R.id.labelItem7)
        val labelItem8 = multiItView.findViewById<TextView>(R.id.labelItem8)
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
        holder.item_limit.text =  multiItModel.limited_count
        holder.item_insection.text =  multiItModel.totalinsection
        holder.item_distrib.setText(multiItModel.qte_distribue)

        holder.item_distrib.doOnTextChanged() { text, start, before, count ->
            if(text.isNullOrEmpty()) return@doOnTextChanged

            listItem!![position].qte_distribue = text.toString()

        }

    }


    fun getArbreListAdded(): MutableList<ArbreModel> = listItem!!

    fun setDataToRvItem(list: MutableList<ArbreModel>){
        listItem?.clear()
        listItem?.addAll(list)
        notifyDataSetChanged()
    }


    override fun getItemCount() = listItem?.size ?: 0


    class DistribArbreHolder(multiItView: View) : RecyclerView.ViewHolder(multiItView) {

        val item_id = multiItView.findViewById<TextView>(R.id.item_dist_id)
        val item_title = multiItView.findViewById<TextView>(R.id.item_dist_title)
        val item_limit = multiItView.findViewById<TextView>(R.id.item_dist_limit)
        val item_insection = multiItView.findViewById<TextView>(R.id.item_insection)
        val item_distrib = multiItView.findViewById<EditText>(R.id.item_dist_quant)
    }
}

class EvaluationPostPlantAdapter(private var listItem: MutableList<ListeEspeceArbrePostPlantModel>) : RecyclerView.Adapter<EvaluationPostPlantAdapter.EvaluationPostPlantHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvaluationPostPlantHolder {
        return EvaluationPostPlantHolder(LayoutInflater.from(parent.context).inflate(R.layout.evaluat_postplant_item_list, parent, false))
    }


    override fun onBindViewHolder(holder: EvaluationPostPlantHolder, position: Int) {
        val multiItModel = listItem!![position]

        Commons.addNotZeroAtFirstToET(holder.item_quant_plante)
        Commons.addNotZeroAtFirstToET(holder.item_quant_surve)

        holder.item_arbre_id.text = multiItModel.arbre_id.toString()
        holder.item_dist_title.text =  multiItModel.nom_arbre
        holder.item_qte_recu.text =  multiItModel.qte_recu
        holder.item_quant_plante.setText("${multiItModel.qte_plant}")
        holder.item_quant_surve.setText("${multiItModel.qte_survec}")
        holder.item_quant_comment.setText(multiItModel.commentaire)

        holder.item_quant_plante.doOnTextChanged() { text, start, before, count ->
            if(text.isNullOrEmpty()) return@doOnTextChanged
            listItem!![position].qte_plant = text.toString()
            holder.item_quant_surve.setText(text.toString())
        }
        holder.item_quant_surve.doOnTextChanged() { text, start, before, count ->
            if(text.isNullOrEmpty()) return@doOnTextChanged
            listItem!![position].qte_survec = text.toString()
        }
        holder.item_quant_comment.doOnTextChanged() { text, start, before, count ->
            if(text.isNullOrEmpty()) return@doOnTextChanged
            listItem!![position].commentaire = text.toString()
        }

    }


    fun getArbreListAdded(): MutableList<ListeEspeceArbrePostPlantModel> = listItem!!

    fun setDataToRvItem(list: MutableList<ListeEspeceArbrePostPlantModel>){
        listItem?.clear()
        listItem?.addAll(list)
        notifyDataSetChanged()
    }


    override fun getItemCount() = listItem?.size ?: 0


    class EvaluationPostPlantHolder(multiItView: View) : RecyclerView.ViewHolder(multiItView) {

        val item_arbre_id = multiItView.findViewById<TextView>(R.id.item_arbre_id)
        val item_dist_title = multiItView.findViewById<TextView>(R.id.item_dist_title)
        val item_qte_recu = multiItView.findViewById<TextView>(R.id.item_qte_recu)
        val item_quant_plante = multiItView.findViewById<AppCompatEditText>(R.id.item_quant_plante)
        val item_quant_surve = multiItView.findViewById<AppCompatEditText>(R.id.item_quant_surve)
        val item_quant_comment = multiItView.findViewById<EditText>(R.id.item_quant_comment)
    }
}
