package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.model.BadgeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class OneMinuteRepository(private val database: AppDatabase) {

  private val challengeDao = database.challengeDao()
  private val completionDao = database.completionDao()
  private val chainDao = database.chainDao()
  private val familyCircleDao = database.familyCircleDao()
  private val notificationDao = database.notificationDao()

  val allChallenges: Flow<List<ChallengeEntity>> = challengeDao.getAllChallenges()
  val dailyChallenge: Flow<ChallengeEntity?> = challengeDao.getDailyChallenge()
  val allCompletions: Flow<List<CompletionEntity>> = completionDao.getAllCompletions()
  val totalCompletionsCount: Flow<Int> = completionDao.getCompletionCount()
  val allChains: Flow<List<ChainEntity>> = chainDao.getAllChains()
  val primaryFamilyCircle: Flow<FamilyCircleEntity?> = familyCircleDao.getPrimaryFamilyCircle()
  val familyResponses: Flow<List<FamilyResponseEntity>> = familyCircleDao.getAllFamilyResponses()
  val availableMemoryMonths: Flow<List<String>> = familyCircleDao.getAvailableMemoryMonths()
  val allNotifications: Flow<List<AppNotificationEntity>> = notificationDao.getAllNotifications()
  val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCount()

  fun getChallengesByCategory(category: String): Flow<List<ChallengeEntity>> =
    challengeDao.getChallengesByCategory(category)

  fun searchChallenges(query: String): Flow<List<ChallengeEntity>> =
    challengeDao.searchChallenges(query)

  fun getChainParticipants(chainId: String): Flow<List<ChainParticipantEntity>> =
    chainDao.getParticipantsForChain(chainId)

  fun getFamilyResponsesForMonth(monthYear: String): Flow<List<FamilyResponseEntity>> =
    familyCircleDao.getResponsesForMonth(monthYear)

  suspend fun getChallengeById(id: String): ChallengeEntity? = withContext(Dispatchers.IO) {
    challengeDao.getChallengeById(id)
  }

  suspend fun saveCompletion(completion: CompletionEntity): Long = withContext(Dispatchers.IO) {
    val id = completionDao.insertCompletion(completion)
    
    // Add a congratulatory notification
    notificationDao.insertNotification(
      AppNotificationEntity(
        title = "Challenge Crushed! ⚡",
        message = "You finished '${completion.challengeTitle}' in ${completion.secondsTaken}s! Keep the streak alive.",
        type = "DAILY_DROP",
        relatedActionId = completion.challengeId
      )
    )
    id
  }

  suspend fun createOrJoinChain(
    originChallengeId: String,
    challengeTitle: String,
    category: String,
    creatorName: String,
    friendNames: List<String>
  ) = withContext(Dispatchers.IO) {
    val chainId = "chain_${UUID.randomUUID().toString().take(6)}"
    val code = "1M-${(1000..9999).random()}"
    val chain = ChainEntity(
      id = chainId,
      originChallengeId = originChallengeId,
      challengeTitle = challengeTitle,
      category = category,
      creatorName = creatorName,
      inviteCode = code,
      totalParticipants = 1 + friendNames.size,
      depthGenerations = 2,
      citiesReached = 2,
      milestoneReached = "Spark Ignited 🔥",
      status = "ACTIVE"
    )
    chainDao.insertChain(chain)

    // Add root participant (User)
    chainDao.insertParticipant(
      ChainParticipantEntity(
        chainId = chainId,
        name = creatorName,
        relation = "You (Originator)",
        generation = 1,
        parentName = "Self",
        responseSnippet = "Completed in 54s!",
        avatarColorHex = "#4F46E5",
        location = "Your City"
      )
    )

    // Add initial friends invited
    val colors = listOf("#FF6B6B", "#10B981", "#F59E0B", "#8B5CF6")
    friendNames.forEachIndexed { index, friend ->
      chainDao.insertParticipant(
        ChainParticipantEntity(
          chainId = chainId,
          name = friend,
          relation = "Friend (Gen 2)",
          generation = 2,
          parentName = creatorName,
          responseSnippet = "Challenged! Ready to beat the clock ⏱️",
          avatarColorHex = colors[index % colors.size],
          location = listOf("Brooklyn", "Austin", "Seattle", "Chicago")[index % 4]
        )
      )
    }

    notificationDao.insertNotification(
      AppNotificationEntity(
        title = "Challenge Chain Launched! 🚀",
        message = "Your challenge '$challengeTitle' was sent to ${friendNames.joinToString(", ")}. Code: $code",
        type = "CHAIN_REACTION",
        relatedActionId = chainId
      )
    )
  }

  suspend fun simulateChainGrowth(chainId: String) = withContext(Dispatchers.IO) {
    val simulatedNames = listOf(
      Pair("Sofia (via Sam)", "Austin"),
      Pair("Marcus (via Liam)", "Denver"),
      Pair("Chloe (via Sam)", "Miami"),
      Pair("Tyler (via Emma)", "Toronto")
    )
    val randomFriend = simulatedNames.random()
    val participant = ChainParticipantEntity(
      chainId = chainId,
      name = randomFriend.first,
      relation = "Friend of Friend",
      generation = 3,
      parentName = "Friend",
      responseSnippet = "Completed with photo! 'Epic challenge!'",
      avatarColorHex = listOf("#EC4899", "#3B82F6", "#14B8A6").random(),
      location = randomFriend.second
    )
    chainDao.insertParticipant(participant)

    notificationDao.insertNotification(
      AppNotificationEntity(
        title = "Viral Chain Expanded! 🌐",
        message = "${randomFriend.first} in ${randomFriend.second} just joined your chain!",
        type = "CHAIN_REACTION",
        relatedActionId = chainId
      )
    )
  }

  suspend fun addFamilyResponse(
    authorName: String,
    authorRole: String,
    avatarEmoji: String,
    challengeTitle: String,
    category: String,
    responseFormat: String,
    textContent: String,
    mediaUri: String = "",
    drawingPointsJson: String = ""
  ) = withContext(Dispatchers.IO) {
    val date = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    val response = FamilyResponseEntity(
      circleId = "family_primary",
      challengeTitle = challengeTitle,
      category = category,
      authorName = authorName,
      authorRole = authorRole,
      avatarEmoji = avatarEmoji,
      responseFormat = responseFormat,
      textContent = textContent,
      mediaUri = mediaUri,
      drawingPointsJson = drawingPointsJson,
      monthYear = date,
      reactionsCount = (1..5).random(),
      stickerTag = listOf("❤️ Heartwarming", "😂 Funniest", "⭐ Creative", "✨ Memory Gold").random()
    )
    familyCircleDao.insertResponse(response)

    notificationDao.insertNotification(
      AppNotificationEntity(
        title = "New Family Moment 🏡",
        message = "$authorName ($authorRole) posted a new response to '$challengeTitle'",
        type = "FAMILY_MOMENT"
      )
    )
  }

  suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
    notificationDao.markAllAsRead()
  }

  suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
    val challengeCount = challengeDao.getChallengeById("ch_daily_today")
    if (challengeCount != null) return@withContext

    val initialChallenges = listOf(
      ChallengeEntity(
        id = "ch_daily_today",
        title = "Draw a Super-Pet in 60 Seconds",
        description = "Give any pet or animal a totally ridiculous superhero costume and power. What is their crime-fighting name?",
        category = "CREATIVITY",
        suggestedFormat = "DRAWING",
        targetAudience = "All",
        timeLimitSeconds = 60,
        isDaily = true,
        dailyDate = "2026-09-11",
        tips = "Use bold colors! Don't worry about perfection, quick doodles are the funniest!",
        featuredOrder = 1,
        popularityCount = 3840
      ),
      ChallengeEntity(
        id = "ch_funny_01",
        title = "Explain Your Job/School to an Alien",
        description = "You have 60 seconds to explain what humans do at work or school to an extraterrestrial who has never seen Earth.",
        category = "FUNNY",
        suggestedFormat = "TEXT",
        targetAudience = "All",
        timeLimitSeconds = 60,
        tips = "Make it absurdly literal. E.g. 'We sit in rolling chairs and stare at glowing rectangles!'",
        featuredOrder = 2,
        popularityCount = 2120
      ),
      ChallengeEntity(
        id = "ch_photo_01",
        title = "Micro Scavenger: Find The Brightest Yellow",
        description = "Look around your space and photograph the most vibrant yellow item you can find within 60 seconds.",
        category = "PHOTOGRAPHY",
        suggestedFormat = "PHOTO",
        targetAudience = "All",
        timeLimitSeconds = 60,
        tips = "Focus closely with good natural light or lamp shine!",
        featuredOrder = 3,
        popularityCount = 1890
      ),
      ChallengeEntity(
        id = "ch_family_01",
        title = "Family Legend: The Funniest Kitchen Disaster",
        description = "Share one memorable family cooking failure, burnt pancake, or funny holiday dish that everyone still jokes about.",
        category = "FAMILY",
        suggestedFormat = "TEXT",
        targetAudience = "Family",
        timeLimitSeconds = 60,
        tips = "Tag who was responsible!",
        featuredOrder = 4,
        popularityCount = 1540
      ),
      ChallengeEntity(
        id = "ch_friends_01",
        title = "Rate Our Friendship In 3 Emojis",
        description = "Pick 3 emojis that capture the chaotic, sweet, or hilarious essence of your friendship and explain in one sentence.",
        category = "FRIENDS",
        suggestedFormat = "TEXT",
        targetAudience = "Teens & Adults",
        timeLimitSeconds = 60,
        tips = "Send the chain link to 3 friends right after!",
        featuredOrder = 5,
        popularityCount = 2780
      ),
      ChallengeEntity(
        id = "ch_knowledge_01",
        title = "Rapid-Fire 3 Mind-Blowing Facts",
        description = "Write down 3 facts you know that sound completely made up but are actually 100% true.",
        category = "KNOWLEDGE",
        suggestedFormat = "TEXT",
        targetAudience = "All",
        timeLimitSeconds = 60,
        tips = "Think about deep sea creatures, outer space, or history oddities!",
        featuredOrder = 6,
        popularityCount = 1940
      ),
      ChallengeEntity(
        id = "ch_kindness_01",
        title = "Secret Sunshine Note",
        description = "Spend 60 seconds writing a sincere message of encouragement or thanks to someone you appreciate.",
        category = "KINDNESS",
        suggestedFormat = "TEXT",
        targetAudience = "All",
        timeLimitSeconds = 60,
        tips = "You can copy and send it directly to them right after!",
        featuredOrder = 7,
        popularityCount = 3100
      ),
      ChallengeEntity(
        id = "ch_games_01",
        title = "Rapid-Fire Food Blitz: Letter 'P'",
        description = "How many edible foods starting with 'P' can you type before the 60-second buzzer sounds?",
        category = "QUICK_GAMES",
        suggestedFormat = "TEXT",
        targetAudience = "All",
        timeLimitSeconds = 60,
        tips = "Pizza, pineapple, pancakes... keep going!",
        featuredOrder = 8,
        popularityCount = 4210
      ),
      ChallengeEntity(
        id = "ch_memories_01",
        title = "Favorite Toy From Childhood",
        description = "What was that one toy or game you could not put down when you were a kid? Doodle it or describe it!",
        category = "MEMORIES",
        suggestedFormat = "DRAWING",
        targetAudience = "All",
        timeLimitSeconds = 60,
        tips = "Capture the nostalgic feeling in a quick sketch or story.",
        featuredOrder = 9,
        popularityCount = 1630
      ),
      ChallengeEntity(
        id = "ch_school_01",
        title = "Invent a Classroom Gadget",
        description = "If you could invent one crazy high-tech device for a classroom or study desk, what would it do in 60s?",
        category = "SCHOOL",
        suggestedFormat = "DRAWING",
        targetAudience = "Kids & Teens",
        timeLimitSeconds = 60,
        tips = "Pencil-sharpening laser? Instant snack tray?",
        featuredOrder = 10,
        popularityCount = 1450
      )
    )
    challengeDao.insertChallenges(initialChallenges)

    // Seed Family Circle
    val circle = FamilyCircleEntity(
      id = "family_primary",
      circleName = "The Horizon Family Circle",
      joinCode = "FAMILY-STARS-42",
      memberCount = 5
    )
    familyCircleDao.insertCircle(circle)

    val familyResponses = listOf(
      FamilyResponseEntity(
        circleId = "family_primary",
        challengeTitle = "What was the sweetest surprise this week?",
        category = "FAMILY",
        authorName = "Mom (Sarah)",
        authorRole = "Mom",
        avatarEmoji = "👩‍🍳",
        responseFormat = "TEXT",
        textContent = "Coming home after a long Tuesday to see the living room tidied and a handwritten card on the table!",
        monthYear = "September 2026",
        reactionsCount = 4,
        stickerTag = "❤️ Sweetest"
      ),
      FamilyResponseEntity(
        circleId = "family_primary",
        challengeTitle = "Draw our family pet's dream vacation",
        category = "CREATIVITY",
        authorName = "Leo",
        authorRole = "Kid (Age 9)",
        avatarEmoji = "👦",
        responseFormat = "DRAWING",
        textContent = "Buster the dog relaxing on a beach made entirely of bacon and tennis balls!",
        monthYear = "September 2026",
        reactionsCount = 5,
        stickerTag = "😂 Funniest"
      ),
      FamilyResponseEntity(
        circleId = "family_primary",
        challengeTitle = "Favorite family road trip memory",
        category = "MEMORIES",
        authorName = "Dad (David)",
        authorRole = "Dad",
        avatarEmoji = "👨‍🔧",
        responseFormat = "TEXT",
        textContent = "When we got lost near the Redwood coast and ended up finding that tiny blueberry pie shack right at sunset.",
        monthYear = "August 2026",
        reactionsCount = 4,
        stickerTag = "✨ Golden Memory"
      ),
      FamilyResponseEntity(
        circleId = "family_primary",
        challengeTitle = "One word to describe each family member",
        category = "FAMILY",
        authorName = "Maya",
        authorRole = "Teen (Age 15)",
        avatarEmoji = "👧",
        responseFormat = "TEXT",
        textContent = "Dad: Punmaster. Mom: Superwoman. Leo: Energizer Bunny. Me: The DJ of the car.",
        monthYear = "August 2026",
        reactionsCount = 6,
        stickerTag = "⭐ Classic"
      )
    )
    familyCircleDao.insertResponses(familyResponses)

    // Seed Notifications
    val notifications = listOf(
      AppNotificationEntity(
        title = "Welcome to One Minute! ⚡",
        message = "Today's 60-second challenge 'Draw a Super-Pet' is live! Beat the clock to start your streak.",
        type = "DAILY_DROP",
        isRead = false
      ),
      AppNotificationEntity(
        title = "Challenge Chains Ready 🌐",
        message = "Complete any challenge to ignite your first Viral Challenge Chain with 3 friends!",
        type = "CHAIN_REACTION",
        isRead = false
      ),
      AppNotificationEntity(
        title = "New Memory in Family Circle 🏡",
        message = "Leo just added a doodle to the September 2026 Memory Book!",
        type = "FAMILY_MOMENT",
        isRead = true
      )
    )
    notificationDao.insertNotifications(notifications)
  }
}
