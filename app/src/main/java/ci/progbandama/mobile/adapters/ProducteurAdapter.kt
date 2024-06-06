package ci.progbandama.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.adapters.ProducteurAdapter.ProducteurHolder
import ci.progbandama.mobile.interfaces.RecyclerItemListener
import ci.progbandama.mobile.models.ProducteurModel
import kotlinx.android.synthetic.main.producteur_items_list.view.*

class ProducteurAdapter(private var producteurs: List<ProducteurModel>?) : RecyclerView.Adapter<ProducteurHolder>() {


    lateinit var producteurListener: RecyclerItemListener<ProducteurModel>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProducteurHolder {
        return ProducteurHolder(LayoutInflater.from(parent.context).inflate(R.layout.producteur_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: ProducteurHolder, position: Int) {
        val producteurModel = producteurs!![position]

        //LogUtils.e("TAG -> ${producteurModel.nom}")

        holder.producteurNomLabel.text = producteurModel.nom
        holder.labelProducteurPrenoms.text = producteurModel.prenoms
        holder.labelProducteurCode.text = producteurModel.uid.toString()

        if (producteurModel.isSynced) holder.imgSyncedStatus.setImageResource(R.drawable.ic_sync_donz)
        else holder.imgSyncedStatus.setImageResource(R.drawable.ic_sync_error)
    }



    override fun getItemCount() = producteurs?.size ?: 0


    class ProducteurHolder(producteurView: View) : RecyclerView.ViewHolder(producteurView) {
        val producteurNomLabel = producteurView.labelProducteurNom
        val labelProducteurPrenoms = producteurView.labelProducteurPrenoms
        val labelProducteurCode = producteurView.labelProducteurCode
        val imgSyncedStatus = producteurView.imgSynced
    }
}