package ci.progbandama.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.databinding.InsecteItemsListBinding
import ci.progbandama.mobile.models.InsecteRavageurModel

class InsecteAdapter(var insectesList: MutableList<InsecteRavageurModel>) : RecyclerView.Adapter<InsecteAdapter.InsecteHolder>() {


    class InsecteHolder(insecteView: InsecteItemsListBinding) : RecyclerView.ViewHolder(insecteView.root) {
        var labelInsecteNom = insecteView.labelInsecteNomItems
        var labelInsecteQuantite = insecteView.labelInsecteQuantiteItems
        var labelInsecteDelete = insecteView.imageDeleteInsecteItems
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsecteHolder {
        return InsecteHolder(
            InsecteItemsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            LayoutInflater.from(parent.context).inflate(R.layout.insecte_items_list, parent, false)
        )
    }


    override fun onBindViewHolder(holder: InsecteHolder, position: Int) {
        val insecte = insectesList[position]

        holder.labelInsecteNom.text = insecte.nom
        holder.labelInsecteQuantite.text = insecte.quantite

        holder.labelInsecteDelete.setOnClickListener {
            try {
                if (insectesList.size == 0) {
                    insectesList.removeAt(0)
                    notifyItemRemoved(0)
                } else {
                    insectesList.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    fun getInsectesAdded() = insectesList


    override fun getItemCount(): Int = insectesList.size
}
