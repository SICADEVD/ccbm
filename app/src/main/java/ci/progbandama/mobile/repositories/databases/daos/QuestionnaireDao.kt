package ci.progbandama.mobile.repositories.databases.daos

import androidx.room.*
import ci.progbandama.mobile.models.*

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface QuestionnaireDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(questionnairesModel: InspectionQuestionnairesModel)

    @Transaction
    @Query("SELECT * FROM questionnaire")
    fun getAll(): MutableList<InspectionQuestionnairesModel>

    @Transaction
    @Query("DELETE FROM questionnaire")
    fun deleteAll()
}
