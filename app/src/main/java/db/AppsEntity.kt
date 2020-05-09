package db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apps_entity")
data class AppsEntity (
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    var packageName : String
)