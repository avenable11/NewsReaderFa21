package edu.ivytech.newsreaderfa21.database

import androidx.room.TypeConverter
import java.util.*

class ArticleTypeConverters {
    @TypeConverter
    fun fromDate(date: Date?) : Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(date: Long?) : Date? {
        return date?.let{ Date(it) }
    }

    @TypeConverter
    fun fromUUID(uuid:UUID) : String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(id:String?) : UUID? {
        return UUID.fromString(id)
    }
}