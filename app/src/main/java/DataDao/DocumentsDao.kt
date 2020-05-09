package DataDao

import androidx.room.*
import db.DocumentEntity
import db.ImagesEntity

@Dao
interface DocumentsDao {
    @Query("SELECT * FROM documents_entity")
    fun getAll() : List<DocumentEntity>

    @Query("SELECT * FROM documents_entity WHERE temporal_path LIKE :path")
    fun getDocument(path : String) : DocumentEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addDocument(documentEntity: DocumentEntity)

    @Delete
    fun deleteDocument(vararg documentEntity: DocumentEntity)
}