package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.AppDatabaseRepository
import javax.inject.Inject

class ClearAppDatabaseUseCase @Inject constructor(private val appDatabaseRepository: AppDatabaseRepository) {
    suspend operator fun invoke() = appDatabaseRepository.clearAllTables()
}