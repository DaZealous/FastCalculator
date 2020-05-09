package DataDao

import androidx.room.*
import db.ImagesEntity

@Dao
interface ImagesDao {
    @Query("SELECT * FROM images_entity")
    fun getAll() : List<ImagesEntity>

    @Query("SELECT * FROM images_entity WHERE temporal_path LIKE :path")
    fun getImage(path : String) : ImagesEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addImage(imagesEntity : ImagesEntity)

    @Delete
    fun deleteImage(vararg imagesEntity: ImagesEntity)
}