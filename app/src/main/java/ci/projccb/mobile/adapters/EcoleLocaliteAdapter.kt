package ci.projccb.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.EcoleLocaliteAdapter.*
import ci.projccb.mobile.tools.Commons
import com.blankj.utilcode.util.LogUtils
import kotlinx.android.synthetic.main.school_items_list.view.*

class EcoleLocaliteAdapter(val ecolesList: MutableList<String>): RecyclerView.Adapter<EcolelocaliteHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EcolelocaliteHolder {
        return EcolelocaliteHolder(LayoutInflater.from(parent.context).inflate(R.layout.school_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: EcolelocaliteHolder, position: Int) {
        val ecole = ecolesList[position]
        holder.ecoleLabel.text = ecole

        holder.ecoleDelete.setOnClickListener {
            LogUtils.e(Commons.TAG, "OKOKOKOKOK")
        }
    }

    fun deleteEcole(ecolePosition: Int) {
        notifyItemRemoved(ecolePosition)
        ecolesList.removeAt(ecolePosition)
    }


    override fun getItemCount(): Int = ecolesList.size


    class EcolelocaliteHolder(var ecoleHolder: View): RecyclerView.ViewHolder(ecoleHolder) {
        val ecoleLabel = ecoleHolder.labelEcoleItems
        val ecoleDelete = ecoleHolder.imageDeleteEcoleItems
    }
}