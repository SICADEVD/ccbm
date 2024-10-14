package ci.progbandama.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.adapters.CultureProducteurAdapter.CultureProducteurHolder
import ci.progbandama.mobile.databinding.CultureItemsListBinding
import ci.progbandama.mobile.interfaces.RecyclerItemListener
import ci.progbandama.mobile.models.CultureProducteurModel

class CultureProducteurAdapter(private var producteurCultures: List<CultureProducteurModel>?) : RecyclerView.Adapter<CultureProducteurHolder>() {


    lateinit var cultureProducteurListener: RecyclerItemListener<CultureProducteurModel>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CultureProducteurHolder {
        return CultureProducteurHolder(
            CultureItemsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            LayoutInflater.from(parent.context).inflate(R.layout.culture_items_list, parent, false)
        )
    }


    override fun onBindViewHolder(holder: CultureProducteurHolder, position: Int) {
        val cultureProducteurModel = producteurCultures!![position]
        holder.cultureLabel.text = cultureProducteurModel.label
        holder.cultureSuperficie.text = cultureProducteurModel.superficie.toString()

    }


    override fun getItemCount() = producteurCultures?.size ?: 0


    class CultureProducteurHolder(var cultureProducteurView: CultureItemsListBinding) : RecyclerView.ViewHolder(cultureProducteurView.root) {
        val cultureLabel = cultureProducteurView.labelCultureItem
        val cultureSuperficie = cultureProducteurView.superficieCultureItem
    }
}
