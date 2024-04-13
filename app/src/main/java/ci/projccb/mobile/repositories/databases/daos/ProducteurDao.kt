package ci.projccb.mobile.repositories.databases.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import ci.projccb.mobile.models.ParcelleModel
import ci.projccb.mobile.models.ProdExt
import ci.projccb.mobile.models.ProducteurMenageModel
import ci.projccb.mobile.models.ProducteurModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface ProducteurDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(producteurModel: ProducteurModel)

    @Transaction
    @Query("SELECT * FROM producteur WHERE id = :producteurID")
    fun getProducteur(producteurID: Int?) : ProducteurModel

    @Transaction
    @Query("SELECT * FROM producteur WHERE uid = :producteurUID")
    fun getProducteurByUID(producteurUID: Int?) : ProducteurModel

    @Transaction
    @Query("SELECT * FROM producteur WHERE id = :producteurID")
    fun getProducteurByID(producteurID: Int?) : ProducteurModel

    @Transaction
    @Query("SELECT * FROM producteur WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<ProducteurModel>

    @Transaction
    @Query("SELECT * FROM producteur WHERE localitesId = :localite")
    fun getProducteursByLocalite(localite: String?): MutableList<ProducteurModel>

    @Transaction
    @Query("UPDATE producteur SET id = :id, isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncData(id: Int, synced: Boolean, localID: Int)

    @Transaction
    @Query("UPDATE producteur SET isSynced = :synced, origin = 'remote' WHERE uid = :localID")
    fun syncDataOnExist(synced: Int, localID: Int)

    @Transaction
    @Query("SELECT * FROM producteur WHERE (isSynced = 0 AND localitesId = :localiteUid AND origin = 'local' AND agentId = :agentId)")
    fun getProducteursUnSynchronizedLocal(localiteUid: String?, agentId: String?): MutableList<ProducteurModel>

    @Transaction
    @Query("SELECT * FROM producteur WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAll(agentID: String?): MutableList<ProducteurModel>

    @Transaction
    @Query("SELECT * FROM producteur WHERE isSynced = 1 AND agentId = :agentID")
    fun getSyncedAll(agentID: String?): MutableList<ProducteurModel>

    @Transaction
    @Query("DELETE FROM producteur WHERE agentId = :agentID")
    fun deleteAgentDatas(agentID: String?)

    @Transaction
    @Query("DELETE FROM producteur")
    fun deleteAll()
    @Transaction
    @Query("SELECT * FROM producteur WHERE isSynced = 0 AND agentId = :agentID")
    fun getUnSyncedAllLive(agentID: String?): LiveData<MutableList<ProducteurModel>>

    @Transaction
    @Query("SELECT p.uid, p.id, p.nom || ' ' || p.prenoms AS fullName, loc.nom AS localite, p.isSynced, p.nom, p.prenoms, p.codeProd FROM producteur AS p INNER JOIN localite AS loc ON p.localitesId = loc.id WHERE p.isSynced = 1 AND (fullName LIKE '%' || :search || '%' OR loc.nom LIKE '%' || :search || '%') ORDER BY p.uid DESC LIMIT 100")
    fun findProdByName(search: String): MutableList<ProdExt>

    @Transaction
    @Query("SELECT p.uid, p.id, p.nom || ' ' || p.prenoms AS fullName, par.id AS parceId, par.uid AS parceUid, p.isSynced, p.nom, p.prenoms, par.codeParc, par.superficie, par.anneeCreation, par.anneeRegenerer FROM producteur AS p INNER JOIN parcelle AS par ON p.id = par.producteurId WHERE p.isSynced = 1 AND (fullName LIKE '%' || :search || '%' OR par.codeParc LIKE '%' || :search || '%' OR par.superficie LIKE '%' || :search || '%' OR par.anneeCreation LIKE '%' || :search || '%') ORDER BY p.uid DESC LIMIT 100")
    fun findByName(search: String): MutableList<ProdExt>

    @Transaction
    @Query("SELECT * FROM producteur WHERE isSynced = 1 AND agentId = :agentID ORDER BY id DESC LIMIT :limit")
    fun getSyncedLimit(agentID: String, limit: Int): MutableList<ProducteurModel>

}