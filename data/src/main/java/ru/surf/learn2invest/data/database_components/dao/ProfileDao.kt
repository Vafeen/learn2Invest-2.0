package ru.surf.learn2invest.data.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.data.database_components.dao.implementation.FlowGetAllImplementation
import ru.surf.learn2invest.data.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.data.database_components.entity.ProfileEntity


@Dao
internal interface ProfileDao : DataAccessObject<ProfileEntity>,
    FlowGetAllImplementation<ProfileEntity> {

    /**
     * Получение всех сущностей в виде Flow
     */
    @Query("select * from profileentity")
    override fun getAllAsFlow(): Flow<List<ProfileEntity>>
}

