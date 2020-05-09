package db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents_entity")
data class DocumentEntity (
    @PrimaryKey
    @ColumnInfo(name = "original_path")
    var originalPath : String,
    @ColumnInfo(name = "temporal_path")
    var temporalPath : String
)