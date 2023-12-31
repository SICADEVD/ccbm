package ci.projccb.mobile.repositories.databases.daos

import androidx.room.*
import ci.projccb.mobile.models.TypeProduitModel

/**
 *  Created by didierboka.developer on 18/12/2021
 *  mail for work:   (didierboka.developer@gmail.com)
 */

@Dao
interface TypeProduitDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(typeProduitModel: TypeProduitModel)

    @Transaction
    @Query("SELECT * FROM type_produit WHERE agentId = :agentID")
    fun getAll(agentID: String?): MutableList<TypeProduitModel>

    @Transaction
    @Query("DELETE FROM type_produit")
    fun deleteAll()
}

/*
cours_eaux
eaux_usees
garde_machines
lieu_formations
nationalites
niveaux
ordures_menageres
sources_eaux
sources_energies
type_localites
type_machines
type_pieces
varietes_cacao
 */
