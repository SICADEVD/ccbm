package ci.projccb.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.interfaces.RecyclerItemListener
import ci.projccb.mobile.models.LivraisonCentralSousModel
import kotlinx.android.synthetic.main.infos_livraiison_central_items_list.view.labelLivraisModCertifItem
import kotlinx.android.synthetic.main.infos_livraiison_central_items_list.view.labelLivraisModQuantity
import kotlinx.android.synthetic.main.infos_livraiison_central_items_list.view.labelLivraisModTypeItem
import kotlinx.android.synthetic.main.infos_livraiison_items_list.view.*
class LivraisonCentralSousModAdapter(private var livraisonCentralSModList: MutableList<LivraisonCentralSousModel>?) : RecyclerView.Adapter<LivraisonCentralSousModAdapter.LivraisonSModHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivraisonSModHolder {
        return LivraisonSModHolder(LayoutInflater.from(parent.context).inflate(R.layout.infos_livraiison_central_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: LivraisonSModHolder, position: Int) {
        val livraisonSModel = livraisonCentralSModList!![position]
        holder.labelLivraisModProducteurItem.text = livraisonSModel.producteurIdName
        holder.labelLivraisModTypeItem.text = livraisonSModel.typeproduit
        holder.labelLivraisModCertifItem.text = livraisonSModel.certificat
        holder.labelLivraisModQuantity.text = livraisonSModel.quantite

        holder.removerBtn.setOnClickListener {
            if (livraisonCentralSModList!!.size == 1) {
                notifyItemRemoved(0)
                livraisonCentralSModList!!.removeAt(0)
            } else {
                notifyItemRemoved(position)
                livraisonCentralSModList!!.removeAt(position)
            }
        }

    }


    override fun getItemCount() = livraisonCentralSModList?.size ?: 0

    fun setItems(livraisonCentralSModListitems: MutableList<LivraisonCentralSousModel>) {
        livraisonCentralSModList?.addAll(livraisonCentralSModListitems)
        notifyDataSetChanged()
    }


    class LivraisonSModHolder(var livraisonSModView: View) : RecyclerView.ViewHolder(livraisonSModView) {
        val labelLivraisModProducteurItem = livraisonSModView.labelLivraisModProducteurItem
        val labelLivraisModTypeItem = livraisonSModView.labelLivraisModTypeItem
        val labelLivraisModCertifItem = livraisonSModView.labelLivraisModCertifItem
        val labelLivraisModQuantity = livraisonSModView.labelLivraisModQuantity
        val removerBtn = livraisonSModView.deleteInfoLivraisSModItem
    }
}
