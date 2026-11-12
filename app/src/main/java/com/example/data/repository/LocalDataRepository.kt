package com.example.data.repository

import com.example.data.local.dao.CropDao
import com.example.data.local.dao.FarmDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.CropEntity
import com.example.data.local.entity.FarmEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.Crop
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * LocalDataRepository - Single source of truth for offline-first User, Farm, and Crop persistence.
 */
class LocalDataRepository(
    private val userDao: UserDao,
    private val farmDao: FarmDao,
    private val cropDao: CropDao
) {
    // -------------------------------------------------------------------------
    // User Operations
    // -------------------------------------------------------------------------
    val activeUser: Flow<UserProfile?> = userDao.getActiveUser().map { entity ->
        entity?.toDomainModel()
    }

    suspend fun saveUser(user: UserProfile) {
        userDao.insertUser(user.toEntity())
    }

    // -------------------------------------------------------------------------
    // Farm Operations
    // -------------------------------------------------------------------------
    val allFarms: Flow<List<FarmEntity>> = farmDao.getAllFarms()

    fun getFarmsForUser(userId: String): Flow<List<FarmEntity>> {
        return farmDao.getFarmsForUser(userId)
    }

    suspend fun createFarm(farm: FarmEntity): Long {
        return farmDao.insertFarm(farm)
    }

    suspend fun deleteFarm(farmId: Long) {
        farmDao.deleteFarm(farmId)
    }

    // -------------------------------------------------------------------------
    // Crop Operations (Offline Catalog & Regional Expansion)
    // -------------------------------------------------------------------------
    val allCrops: Flow<List<Crop>> = cropDao.getAllCrops().map { list ->
        list.map { it.toDomainModel() }
    }

    fun searchCrops(query: String): Flow<List<Crop>> {
        return cropDao.searchCrops(query).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    suspend fun seedInitialCropsIfEmpty(crops: List<Crop>) {
        val entities = crops.map { it.toEntity() }
        cropDao.insertCrops(entities)
    }
}

// -----------------------------------------------------------------------------
// Mapping Extensions between Room Entities and Domain Models
// -----------------------------------------------------------------------------

fun UserEntity.toDomainModel(): UserProfile {
    return UserProfile(
        id = this.id,
        name = this.name,
        email = this.email,
        phone = this.phone,
        country = this.country,
        province = this.province,
        district = this.district,
        preferredLanguage = this.preferredLanguage,
        farmingType = this.farmingType,
        mainCrops = if (this.mainCropsCsv.isNotBlank()) this.mainCropsCsv.split("||") else emptyList(),
        isGuest = this.isGuest
    )
}

fun UserProfile.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name,
        email = this.email,
        phone = this.phone,
        country = this.country,
        province = this.province,
        district = this.district,
        preferredLanguage = this.preferredLanguage,
        farmingType = this.farmingType,
        mainCropsCsv = this.mainCrops.joinToString("||"),
        isGuest = this.isGuest
    )
}

fun CropEntity.toDomainModel(): Crop {
    return Crop(
        id = this.id,
        name = this.name,
        localNameNepali = this.localNameNepali,
        localNameHindi = this.localNameHindi,
        scientificName = this.scientificName,
        category = this.category,
        supportedCountries = if (this.supportedCountriesCsv.isNotBlank()) this.supportedCountriesCsv.split("||") else listOf("Nepal"),
        iconEmoji = this.iconEmoji,
        description = this.description,
        growingSeason = this.growingSeason,
        commonDiseases = if (this.commonDiseasesCsv.isNotBlank()) this.commonDiseasesCsv.split("||") else emptyList(),
        commonPests = if (this.commonPestsCsv.isNotBlank()) this.commonPestsCsv.split("||") else emptyList(),
        nutrientProblems = if (this.nutrientProblemsCsv.isNotBlank()) this.nutrientProblemsCsv.split("||") else emptyList(),
        status = this.status
    )
}

fun Crop.toEntity(): CropEntity {
    return CropEntity(
        id = this.id,
        name = this.name,
        localNameNepali = this.localNameNepali,
        localNameHindi = this.localNameHindi,
        scientificName = this.scientificName,
        category = this.category,
        supportedCountriesCsv = this.supportedCountries.joinToString("||"),
        iconEmoji = this.iconEmoji,
        description = this.description,
        growingSeason = this.growingSeason,
        commonDiseasesCsv = this.commonDiseases.joinToString("||"),
        commonPestsCsv = this.commonPests.joinToString("||"),
        nutrientProblemsCsv = this.nutrientProblems.joinToString("||"),
        status = this.status
    )
}
