package db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images_entity")
data class ImagesEntity (
    @PrimaryKey
    @ColumnInfo(name = "original_path")
    var originalPath : String,
    @ColumnInfo(name = "temporal_path")
    var temporalPath : String
)