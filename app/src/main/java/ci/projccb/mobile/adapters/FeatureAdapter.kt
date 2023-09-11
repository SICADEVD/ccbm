package ci.projccb.mobile.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.DashboardAgentActivity
import ci.projccb.mobile.activities.forms.CalculEstimationActivity
import ci.projccb.mobile.activities.forms.FormationActivity
import ci.projccb.mobile.activities.forms.InspectionActivity
import ci.projccb.mobile.activities.forms.LivraisonActivity
import ci.projccb.mobile.activities.forms.ParcelleActivity
import ci.projccb.mobile.activities.forms.ProducteurActivity
import ci.projccb.mobile.activities.forms.ProducteurMenageActivity
import ci.projccb.mobile.activities.forms.SsrtClmsActivity
import ci.projccb.mobile.activities.forms.SuiviApplicationActivity
import ci.projccb.mobile.activities.forms.SuiviParcelleActivity
import ci.projccb.mobile.activities.forms.UniteAgricoleProducteurActivity
import ci.projccb.mobile.activities.lists.DatasDraftedListActivity
import ci.projccb.mobile.activities.lists.FormationsListActivity
import ci.projccb.mobile.activities.lists.LivraisonsListActivity
import ci.projccb.mobile.activities.lists.MenageresListActivity
import ci.projccb.mobile.activities.lists.ParcellesListActivity
import ci.projccb.mobile.activities.lists.ProducteursListActivity
import ci.projccb.mobile.activities.lists.SuiviPacellesListActivity
import ci.projccb.mobile.activities.lists.UpdateContentsListActivity
import ci.projccb.mobile.models.FeatureModel
import ci.projccb.mobile.tools.Commons
import ci.projccb.mobile.tools.Commons.Companion.redirectMenu
import com.blankj.utilcode.util.ActivityUtils
import com.squareup.picasso.Picasso

class FeatureAdapter(
    private var activity: Activity,
    private var listOfFeatures: MutableList<FeatureModel>
):
    RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder>() {
    class FeatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image_current : AppCompatImageView = itemView.findViewById(R.id.image_current)
        val tv_current_sync : TextView = itemView.findViewById(R.id.tv_current_sync)
        val tv_current_draft : TextView = itemView.findViewById(R.id.tv_current_draft)
        val feature_tv_main : TextView = itemView.findViewById(R.id.feature_tv_main)

        val btn_add : AppCompatImageView = itemView.findViewById(R.id.add_new_item)
        val btn_edit : AppCompatImageView = itemView.findViewById(R.id.btn_edit)
        val btn_sync : AppCompatImageView = itemView.findViewById(R.id.btn_sync)
        val btn_draft : AppCompatImageView = itemView.findViewById(R.id.btn_draft)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeatureAdapter.FeatureViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.dashbord_item_present, parent,false)
        return FeatureViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: FeatureAdapter.FeatureViewHolder, position: Int) {

        val currentFeature = listOfFeatures.get(position)

        holder.tv_current_sync.text = currentFeature.countSync.toString()
        holder.tv_current_draft.text = currentFeature.countDraft.toString()
        holder.feature_tv_main.text = currentFeature.title.toString()

        Picasso.get()
            .load(currentFeature.image)
            .fit()
            .placeholder(currentFeature.placeholder)
            .into(holder.image_current);

        if(currentFeature.canAdd){
            holder.btn_add.visibility = VISIBLE
            holder.btn_add.setOnClickListener {
                redirectMenu(currentFeature.type.toString(), "ADD", activity)
            }
        }

        if(currentFeature.canViewUpdate){
            holder.btn_edit.visibility = VISIBLE
            holder.btn_edit.setOnClickListener {
                redirectMenu(currentFeature.type.toString(), "UPDATE", activity)
            }
        }

        if(currentFeature.canViewSync){
            holder.btn_sync.visibility = VISIBLE
            holder.btn_sync.setOnClickListener {
                redirectMenu(currentFeature.type.toString(), "DATAS", activity)
            }
        }

        if(currentFeature.canViewDraft){
            holder.btn_draft.visibility = VISIBLE
            holder.btn_draft.setOnClickListener {
                redirectMenu(currentFeature.type.toString(), "DRAFTS", activity)
            }
        }
    }



    fun updateFeature(list: ArrayList<FeatureModel>) {
        this.listOfFeatures = list
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return  listOfFeatures.size
    }
}