package DataDao

import android.content.pm.ApplicationInfo
import androidx.room.*
import db.AppsEntity
import db.ImagesEntity
import db.VideosEntity

@Dao
interface AppsDao {
    @Query("SELECT * FROM apps_entity")
    fun getAll() : List<AppsEntity>

    @Query("SELECT * FROM apps_entity WHERE package_name LIKE :info")
    fun getAppInfo(info : String) : AppsEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAppInfo(info: AppsEntity)

    @Delete
    fun deleteAppInfo(vararg info: AppsEntity)
}