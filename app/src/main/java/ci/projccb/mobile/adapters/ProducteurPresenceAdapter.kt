package ci.projccb.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.ProducteurPresenceAdapter.ProducteurPresenceHolder
import ci.projccb.mobile.models.ProducteurModel
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.producteur_presence_items_list.view.*

class ProducteurPresenceAdapter(private var producteursPresence: MutableList<ProducteurModel>?) : RecyclerView.Adapter<ProducteurPresenceHolder>() {


    companion object {
        const val TAG = "ProducteurPresenceAdapter::class"
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProducteurPresenceHolder {
        return ProducteurPresenceHolder(LayoutInflater.from(parent.context).inflate(R.layout.producteur_presence_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: ProducteurPresenceHolder, position: Int) {
        val producteurModel = producteursPresence!![position]

        holder.producteurNomLabel.text = "${producteurModel.nom} ${producteurModel.prenoms}"

        if(producteursPresence!!.size > 0){

        }

        holder.producteurDelete.setOnClickListener {
            ToastUtils.showShort(producteurModel.nom)

            //LogUtils.e(TAG, holder.adapterPosition)
            //LogUtils.e(TAG, position)

            producteursPresence?.removeAt(holder.adapterPosition)
            notifyDataSetChanged()
        }
    }


    fun getProducteursSelected(): MutableList<ProducteurModel> = producteursPresence!!


    override fun getItemCount() = producteursPresence?.size ?: 0


    class ProducteurPresenceHolder(producteurPresenceView: View) : RecyclerView.ViewHolder(producteurPresenceView) {
        val producteurNomLabel = producteurPresenceView.labelProducteurPresence
        val producteurDelete = producteurPresenceView.deleteProducteurPresence
    }
}