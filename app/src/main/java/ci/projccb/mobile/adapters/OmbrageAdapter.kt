package ci.projccb.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.OmbrageAdapter.OmbrageHolder
import ci.projccb.mobile.models.OmbrageVarieteModel
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.tools.Commons
import com.blankj.utilcode.util.LogUtils
import kotlinx.android.synthetic.main.activite_items_list.view.deleteCultureItem
import kotlinx.android.synthetic.main.activite_items_list.view.libelle
import kotlinx.android.synthetic.main.activite_items_list.view.valuetext
import kotlinx.android.synthetic.main.ombrage_items_list.view.*

class OmbrageAdapter(private var ombrages: MutableList<OmbrageVarieteModel>?, private  var libelTitle: String = "Libéllé", private  var valeurTitle: String = "Valeur") : RecyclerView.Adapter<OmbrageHolder>() {


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
