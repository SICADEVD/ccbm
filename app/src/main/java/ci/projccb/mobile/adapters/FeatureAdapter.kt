package ci.projccb.mobile.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.DashboardAgentActivity
import ci.projccb.mobile.models.FeatureModel
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.redirectMenu

class FeatureAdapter(
    private var activity: Activity,
    private var listOfFeatures: MutableList<FeatureModel>
):
    RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder>() {

    private var selectedItem = -1

    fun setPositionSelected(value:Int){
        selectedItem = value
    }

    class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val mainerScroll : CardView = itemView.findViewById(R.id.mainerScroll)
//        val image_current : AppCompatImageView = itemView.findViewById(R.id.image_current)
//        val feature_tv_main : TextView = itemView.findViewById(R.id.feature_tv_main)
//
//        val btn_add : AppCompatTextView = itemView.findViewById(R.id.btn_add)
//        val btn_edit : AppCompatTextView = itemView.findViewById(R.id.btn_update)
//        val btn_sync : AppCompatTextView = itemView.findViewById(R.id.btn_sync)
//        val btn_draft : AppCompatTextView = itemView.findViewById(R.id.btn_draft)

            val tv_current_sync : TextView = itemView.findViewById(R.id.labelCount)
            val tv_current_draft : TextView = itemView.findViewById(R.id.labelDraftCount)
            val image_current : AppCompatImageView = itemView.findViewById(R.id.imageViewFeature)
            val feature_tv_main : TextView = itemView.findViewById(R.id.textViewFeature)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeatureAdapter.FeatureViewHolder {
        //val inflater = LayoutInflater.from(parent.context).inflate(R.layout.dashbord_item_present, parent,false)
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent,false)
        return FeatureViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: FeatureAdapter.FeatureViewHolder, position: Int) {

        val currentFeature = listOfFeatures.get(position)

//        if(selectedItem != -1){
//            val alpha = if (position === selectedItem) 1.0f else 0.1f
//            holder.mainerScroll.alpha = alpha
//        }

        holder.tv_current_sync.text = currentFeature.countSync.toString()
        holder.tv_current_draft.text = currentFeature.countDraft.toString()
        holder.image_current.setImageResource(currentFeature.icon)
        Commons.modifyIcColor(activity, holder.image_current, R.color.black)
        holder.feature_tv_main.text = currentFeature.title.toString().replace("_", " ")

        val passToModifList = listOf<String>(
            "INSPECTION",
            "PRODUCTEUR",
            "PARCELLE"
        )

        holder.itemView.setOnClickListener {

            if(passToModifList.toString().toLowerCase().contains(currentFeature.type.toString(), ignoreCase = true)){
                redirectMenu(currentFeature.type.toString(), "SYNC_UPDATE", activity)
            }else{
                redirectMenu(currentFeature.type.toString(), "ADD", activity)
            }
            (activity as DashboardAgentActivity).hideExpandFromAdapter()
        }
    }


    fun updateFeatures(list: MutableList<FeatureModel>) {
        this.listOfFeatures.clear()
        this.listOfFeatures.addAll(list)
        notifyDataSetChanged()
    }

    fun removeAllFeatures() {
        this.listOfFeatures.clear()
        notifyDataSetChanged()
    }

    fun getListFeatures(): MutableList<FeatureModel> {
        return this.listOfFeatures
    }


    override fun getItemCount(): Int {
        return  listOfFeatures.size
    }
}