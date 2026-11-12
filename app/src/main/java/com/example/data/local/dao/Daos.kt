package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CropEntity
import com.example.data.local.entity.FarmEntity
import com.example.data.local.entity.FieldEntity
import com.example.data.local.entity.MonitoringLogEntity
import com.example.data.local.entity.ReminderEntity
import com.example.data.local.entity.ScanEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    fun getActiveUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)
}

@Dao
interface CropDao {
    @Query("SELECT * FROM crops ORDER BY name ASC")
    fun getAllCrops(): Flow<List<CropEntity>>

    @Query("SELECT * FROM crops WHERE id = :id LIMIT 1")
    suspend fun getCropById(id: String): CropEntity?

    @Query("SELECT * FROM crops WHERE category = :category ORDER BY name ASC")
    fun getCropsByCategory(category: String): Flow<List<CropEntity>>

    @Query("SELECT * FROM crops WHERE name LIKE '%' || :query || '%' OR localNameNepali LIKE '%' || :query || '%'")
    fun searchCrops(query: String): Flow<List<CropEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrops(crops: List<CropEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrop(crop: CropEntity)

    @Query("DELETE FROM crops WHERE id = :id")
    suspend fun deleteCropById(id: String)
}

@Dao
interface FarmDao {
    @Query("SELECT * FROM farms ORDER BY createdAt DESC")
    fun getAllFarms(): Flow<List<FarmEntity>>

    @Query("SELECT * FROM farms WHERE userId = :userId ORDER BY createdAt DESC")
    fun getFarmsForUser(userId: String): Flow<List<FarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarm(farm: FarmEntity): Long

    @Query("DELETE FROM farms WHERE id = :id")
    suspend fun deleteFarm(id: Long)

    @Query("SELECT * FROM fields WHERE farmId = :farmId")
    fun getFieldsForFarm(farmId: Long): Flow<List<FieldEntity>>

    @Query("SELECT * FROM fields")
    fun getAllFields(): Flow<List<FieldEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertField(field: FieldEntity): Long

    @Query("SELECT * FROM monitoring_logs WHERE fieldId = :fieldId ORDER BY id DESC")
    fun getLogsForField(fieldId: Long): Flow<List<MonitoringLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonitoringLog(log: MonitoringLogEntity)
}

@Dao
interface ScanDao {
    @Query("SELECT * FROM scan_results ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanEntity>>

    @Query("SELECT * FROM scan_results WHERE id = :id LIMIT 1")
    suspend fun getScanById(id: String): ScanEntity?

    @Query("SELECT * FROM scan_results WHERE cropName LIKE '%' || :query || '%' OR conditionName LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchScans(query: String): Flow<List<ScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanEntity)

    @Query("DELETE FROM scan_results WHERE id = :id")
    suspend fun deleteScanById(id: String)
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY date ASC, time ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: Long)
}
