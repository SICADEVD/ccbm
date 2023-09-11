package ci.projccb.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.OmbrageAdapter.OmbrageHolder
import ci.projccb.mobile.models.OmbrageVarieteModel
import ci.projccb.mobile.tools.Commons
import com.blankj.utilcode.util.LogUtils
import kotlinx.android.synthetic.main.ombrage_items_list.view.*

class OmbrageAdapter(private var ombrages: MutableList<OmbrageVarieteModel>?) : RecyclerView.Adapter<OmbrageHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OmbrageHolder {
        return OmbrageHolder(LayoutInflater.from(parent.context).inflate(R.layout.ombrage_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: OmbrageHolder, position: Int) {
        val ombrageModel = ombrages!![position]

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
        val varieteLabel = ombrageView.labelOmbrageItem
        val nombreLabel = ombrageView.nombreOmbrageItem
        val deleteOmbre = ombrageView.deleteOmbrageItem
    }
}
