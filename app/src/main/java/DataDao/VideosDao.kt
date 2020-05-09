package DataDao

import androidx.room.*
import db.ImagesEntity
import db.VideosEntity

@Dao
interface VideosDao {
    @Query("SELECT * FROM videos_entity")
    fun getAll() : List<VideosEntity>

    @Query("SELECT * FROM videos_entity WHERE temporal_path LIKE :path")
    fun getVideo(path : String) : VideosEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addVideo(videosEntity: VideosEntity)

    @Delete
    fun deleteVideo(vararg videosEntity: VideosEntity)
}