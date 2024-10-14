package ci.progbandama.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.adapters.EcoleLocaliteAdapter.*
import ci.progbandama.mobile.databinding.SchoolItemsListBinding
import ci.progbandama.mobile.tools.Commons
import com.blankj.utilcode.util.LogUtils

class EcoleLocaliteAdapter(val ecolesList: MutableList<String>): RecyclerView.Adapter<EcolelocaliteHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EcolelocaliteHolder {
        return EcolelocaliteHolder(SchoolItemsListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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


    class EcolelocaliteHolder(var ecoleHolder: SchoolItemsListBinding): RecyclerView.ViewHolder(ecoleHolder.root) {
        val ecoleLabel = ecoleHolder.labelEcoleItems
        val ecoleDelete = ecoleHolder.imageDeleteEcoleItems
    }
}