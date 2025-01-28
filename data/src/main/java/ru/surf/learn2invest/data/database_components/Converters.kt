package ru.surf.learn2invest.data.database_components

import androidx.room.TypeConverter
import java.util.Date

/**
 * Функции для конвертации даты в БД и наоборот
 */
internal object Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}