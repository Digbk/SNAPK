package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
  @Query("SELECT * FROM challenges ORDER BY featuredOrder ASC")
  fun getAllChallenges(): Flow<List<ChallengeEntity>>

  @Query("SELECT * FROM challenges WHERE isDaily = 1 LIMIT 1")
  fun getDailyChallenge(): Flow<ChallengeEntity?>

  @Query("SELECT * FROM challenges WHERE id = :id LIMIT 1")
  suspend fun getChallengeById(id: String): ChallengeEntity?

  @Query("SELECT * FROM challenges WHERE category = :category")
  fun getChallengesByCategory(category: String): Flow<List<ChallengeEntity>>

  @Query("SELECT * FROM challenges WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
  fun searchChallenges(query: String): Flow<List<ChallengeEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChallenges(challenges: List<ChallengeEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChallenge(challenge: ChallengeEntity)
}

@Dao
interface CompletionDao {
  @Query("SELECT * FROM completions ORDER BY completedAt DESC")
  fun getAllCompletions(): Flow<List<CompletionEntity>>

  @Query("SELECT * FROM completions WHERE challengeId = :challengeId LIMIT 1")
  fun getCompletionForChallenge(challengeId: String): Flow<CompletionEntity?>

  @Query("SELECT COUNT(*) FROM completions")
  fun getCompletionCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCompletion(completion: CompletionEntity): Long
}

@Dao
interface ChainDao {
  @Query("SELECT * FROM challenge_chains ORDER BY createdAt DESC")
  fun getAllChains(): Flow<List<ChainEntity>>

  @Query("SELECT * FROM challenge_chains WHERE id = :chainId LIMIT 1")
  fun getChainById(chainId: String): Flow<ChainEntity?>

  @Query("SELECT * FROM chain_participants WHERE chainId = :chainId ORDER BY generation ASC, completedAt ASC")
  fun getParticipantsForChain(chainId: String): Flow<List<ChainParticipantEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChain(chain: ChainEntity)

  @Update
  suspend fun updateChain(chain: ChainEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertParticipants(participants: List<ChainParticipantEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertParticipant(participant: ChainParticipantEntity)
}

@Dao
interface FamilyCircleDao {
  @Query("SELECT * FROM family_circles LIMIT 1")
  fun getPrimaryFamilyCircle(): Flow<FamilyCircleEntity?>

  @Query("SELECT * FROM family_responses ORDER BY timestamp DESC")
  fun getAllFamilyResponses(): Flow<List<FamilyResponseEntity>>

  @Query("SELECT * FROM family_responses WHERE monthYear = :monthYear ORDER BY timestamp DESC")
  fun getResponsesForMonth(monthYear: String): Flow<List<FamilyResponseEntity>>

  @Query("SELECT DISTINCT monthYear FROM family_responses ORDER BY timestamp DESC")
  fun getAvailableMemoryMonths(): Flow<List<String>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCircle(circle: FamilyCircleEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertResponse(response: FamilyResponseEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertResponses(responses: List<FamilyResponseEntity>)
}

@Dao
interface NotificationDao {
  @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
  fun getAllNotifications(): Flow<List<AppNotificationEntity>>

  @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
  fun getUnreadCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: AppNotificationEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotifications(notifications: List<AppNotificationEntity>)

  @Query("UPDATE app_notifications SET isRead = 1")
  suspend fun markAllAsRead()
}
