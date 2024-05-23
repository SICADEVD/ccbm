package ci.projccb.mobile.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.adapters.FormationAdapter.FormationHolder
import ci.projccb.mobile.models.FormationModel
import ci.projccb.mobile.repositories.databases.CcbRoomDatabase
import ci.projccb.mobile.tools.Commons.Companion.limitListByCount
import ci.projccb.mobile.tools.Commons.Companion.toModifString
import ci.projccb.mobile.tools.Constants
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.formation_items_list.view.*


class FormationAdapter(private var acti: Activity ,private var formations: List<FormationModel>?) : RecyclerView.Adapter<FormationHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormationHolder {
        return FormationHolder(LayoutInflater.from(parent.context).inflate(R.layout.formation_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: FormationHolder, position: Int) {
        val formationModel = formations!![position]

        val listTypeFormation = CcbRoomDatabase.getDatabase(acti)?.typeFormationDao()?.getAll(
            SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        val listThemeFormation = CcbRoomDatabase.getDatabase(acti)?.themeFormationDao()?.getAll(
            SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())
        val listSousThemeFormation = CcbRoomDatabase.getDatabase(acti)?.sousThemeFormationDao()?.getAll(
            SPUtils.getInstance().getInt(Constants.AGENT_ID).toString())

        val typeTok = object : TypeToken<MutableList<String>>(){}.type

        val themeFit = GsonUtils.fromJson<MutableList<String>>(formationModel.themeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
        val sousThemeFit = GsonUtils.fromJson<MutableList<String>>(formationModel.sousThemeStr, typeTok)?.map { it.split("-")?.let { if(it.size > 1) it.get(1) else it.get(0) } }
        val typeF = listTypeFormation?.filter { formationModel.typeFormationStr.contains(it.id.toString()) == true }?.map { "${it.nom}" }
        val themeF = listThemeFormation?.filter {  themeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }
        val sousThemeF = listSousThemeFormation?.filter { sousThemeFit?.contains(it.id.toString()) == true }?.map { "${it.nom}" }

        val content = "Modules: \n${typeF?.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${typeF?.size}"}\nThemes: \n${themeF.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${themeF?.size}"}\nSous Themes: \n${sousThemeF.limitListByCount(2).toModifString(true, "\n", "-")+"\n-Total: ${sousThemeF?.size}"}"

        holder.formationThemeLabel.setText(content)
        holder.formationDate.text = formationModel.multiStartDate

        if (formationModel.isSynced) holder.imgSyncedFormation.setImageResource(R.drawable.ic_sync_donz)
        else holder.imgSyncedFormation.setImageResource(R.drawable.ic_sync_error)
    }



    override fun getItemCount() = formations?.size ?: 0


    class FormationHolder(formationView: View) : RecyclerView.ViewHolder(formationView) {
        val formationThemeLabel = formationView.labelThemeFormations
        val formationDate = formationView.labelDateFormations
        val formationLieu = formationView.labelLieuFormations
        val imgSyncedFormation = formationView.imgSyncedDoneFormations
    }
}
