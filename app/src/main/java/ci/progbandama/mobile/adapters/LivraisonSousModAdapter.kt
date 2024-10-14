package ci.progbandama.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.databinding.InfosLivraiisonCentralItemsListBinding
import ci.progbandama.mobile.databinding.InfosLivraiisonItemsListBinding
import ci.progbandama.mobile.interfaces.RecyclerItemListener
import ci.progbandama.mobile.models.LivraisonSousModel

class LivraisonSousModAdapter(private var livraisonSModList: MutableList<LivraisonSousModel>?) : RecyclerView.Adapter<LivraisonSousModAdapter.LivraisonSModHolder>() {


    lateinit var cultureProducteurListener: RecyclerItemListener<LivraisonSousModel>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivraisonSModHolder {
        return LivraisonSModHolder(
            InfosLivraiisonItemsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            LayoutInflater.from(parent.context).inflate(R.layout.infos_livraiison_items_list, parent, false)
        )
    }


    override fun onBindViewHolder(holder: LivraisonSModHolder, position: Int) {
        val livraisonSModel = livraisonSModList!![position]
        holder.labelLivraisModProducteurItem.text = livraisonSModel.producteurIdName
        holder.labelLivraisModParcelleItem.text = livraisonSModel.parcelleIdName
        holder.labelLivraisModQuantity.text = livraisonSModel.quantityNb.toString()

        holder.removerBtn.setOnClickListener {
            if (livraisonSModList!!.size == 1) {
                notifyItemRemoved(0)
                livraisonSModList!!.removeAt(0)
            } else {
                notifyItemRemoved(position)
                livraisonSModList!!.removeAt(position)
            }
        }

    }


    override fun getItemCount() = livraisonSModList?.size ?: 0


    class LivraisonSModHolder(var livraisonSModView: InfosLivraiisonItemsListBinding) : RecyclerView.ViewHolder(livraisonSModView.root) {
        val labelLivraisModProducteurItem = livraisonSModView.labelLivraisModProducteurItem
        val labelLivraisModParcelleItem = livraisonSModView.labelLivraisModParcelleItem
        val labelLivraisModQuantity = livraisonSModView.labelLivraisModQuantity
        val removerBtn = livraisonSModView.deleteInfoLivraisSModItem
    }
}
