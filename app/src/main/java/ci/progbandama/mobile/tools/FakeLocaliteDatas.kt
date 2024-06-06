package ci.progbandama.mobile.tools

import android.content.Context
import ci.progbandama.mobile.R
import ci.progbandama.mobile.models.LocaliteModel
import ci.progbandama.mobile.repositories.databases.ProgBandRoomDatabase
import com.blankj.utilcode.util.SPUtils

/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 11/06/2022.
 **/
object FakeLocaliteDatas {

    fun saveLocalite(number: Int, context: Context) {
        var localitModel: LocaliteModel?
        localitModel = LocaliteModel(
            uid = 0,
            id = 0,
            nom = "localite $number",
            cooperativeId = SPUtils.getInstance().getInt(Constants.AGENT_COOP_ID, 1).toString(),
            type = "Village",
            sousPref = "Sous Prefecture $number",
            pop = "$number",
            centreYesNo = "oui",
            typeCentre = "Publique",
            centreNom = "Centre $number",
            ecoleYesNo = context.getString(R.string.non),
            ecoleNbre = "0",
            nomsEcolesStringify = "[]",
            source = "Marigot",
            cieYesNo = "oui",
            marcheYesNo = "oui",
            dayMarche = "Lundi",
            dechetYesNo = "oui",
            comite = "$number",
            femmeAsso = "$number",
            jeuneAsso = "$number",
            latitude = "-3.404033",
            longitude = "4.65875858",
            isSynced = false,
            agentId = SPUtils.getInstance().getInt(Constants.AGENT_ID, 0).toString()
        )

        ProgBandRoomDatabase.getDatabase(context)?.localiteDoa()?.insert(localitModel)
    }

}
