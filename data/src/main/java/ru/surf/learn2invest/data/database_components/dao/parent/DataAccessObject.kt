package ru.surf.learn2invest.data.database_components.dao.parent

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy


/**
 * Родительский интерфейс всех DAO с базовыми методами
 */
@Dao
internal interface DataAccessObject<T> {

    /**
     * Вставка && Обновление в базе данных одного или нескольких объектов
     * @param entities [Список объектов, которые нуно положить в бд]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)// insert && update
    suspend fun insertAll(vararg entities: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)// insert && update
    suspend fun insertAll(entities: List<T>)

    /**
     * Удаление из базы данных одного объекта
     */
    @Delete
    suspend fun delete(entity: T)

    @Delete
    suspend fun deleteAll(entity: List<T>)

}