package ci.progbandama.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.adapters.MatiereAdapter.MatiereHolder
import ci.progbandama.mobile.databinding.MatiereItemsListBinding
import ci.progbandama.mobile.tools.Commons
import com.blankj.utilcode.util.LogUtils

class MatiereAdapter(var matieresList: MutableList<String>): RecyclerView.Adapter<MatiereHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatiereHolder {
        return MatiereHolder(
            MatiereItemsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            LayoutInflater.from(parent.context).inflate(R.layout.matiere_items_list, parent, false)
        )
    }


    override fun onBindViewHolder(holder: MatiereHolder, position: Int) {
        val matiere = matieresList[position]
        holder.matiereLabel.text = matiere

        holder.matiereDelete.setOnClickListener {
            LogUtils.e(Commons.TAG, "position $position")
            LogUtils.e(Commons.TAG, "Adapter position ${holder.adapterPosition}")

            try {
                if (matieresList.size == 1) {
                    matieresList.removeAt(0)
                    notifyItemRemoved(0)
                } else {
                    matieresList.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    fun getMatieresAdded() = matieresList



    fun deleteEcole(matierePosition: Int) {
        notifyItemRemoved(matierePosition)
        matieresList.removeAt(matierePosition)
    }


    override fun getItemCount(): Int = matieresList.size


    class MatiereHolder(matiereHolder: MatiereItemsListBinding) : RecyclerView.ViewHolder(matiereHolder.root) {
        val matiereLabel = matiereHolder.labelMatiereNomItems
        val matiereDelete = matiereHolder.imageDeleteMatiereItems
    }

}
