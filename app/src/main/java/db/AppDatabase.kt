package db

import DataDao.AppsDao
import DataDao.DocumentsDao
import DataDao.ImagesDao
import DataDao.VideosDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ImagesEntity::class, VideosEntity::class, DocumentEntity::class, AppsEntity::class], version = 3)

abstract class AppDatabase : RoomDatabase(){
    abstract fun imagesDao() : ImagesDao
    abstract fun videosDao() : VideosDao
    abstract fun documentsDao() : DocumentsDao
    abstract fun appsDao() : AppsDao

    companion object{
        @Volatile private var instance : AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context : Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, AppDatabase::class.java, "files_database.db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
}