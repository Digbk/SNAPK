package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.model.BadgeItem
import com.example.data.model.ChallengeCategory
import com.example.data.model.ResponseFormat
import com.example.data.repository.OneMinuteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DrawStroke(
  val points: List<Pair<Float, Float>>,
  val color: Color,
  val strokeWidth: Float
)

data class UserProfileState(
  val name: String = "",
  val handle: String = "",
  val avatarEmoji: String = "⚡",
  val bio: String = "Daily 60-second speed runner!",
  val joinedDate: String = "September 2026",
  val isProfileConfigured: Boolean = false
)

class OneMinuteViewModel(application: Application) : AndroidViewModel(application) {

  private val prefs = application.getSharedPreferences("one_minute_user_prefs", android.content.Context.MODE_PRIVATE)
  private val repository: OneMinuteRepository

  // User Profile State backed by SharedPreferences
  private val _userProfile = MutableStateFlow(
    UserProfileState(
      name = prefs.getString("full_name", "") ?: "",
      handle = prefs.getString("handle", "") ?: "",
      avatarEmoji = prefs.getString("avatar_emoji", "⚡") ?: "⚡",
      bio = prefs.getString("bio", "Daily 60-second speed runner!") ?: "Daily 60-second speed runner!",
      joinedDate = prefs.getString("joined_date", "September 2026") ?: "September 2026",
      isProfileConfigured = prefs.getBoolean("is_profile_configured", false)
    )
  )
  val userProfile: StateFlow<UserProfileState> = _userProfile.asStateFlow()

  init {
    val database = AppDatabase.getDatabase(application)
    repository = OneMinuteRepository(database)
    viewModelScope.launch {
      repository.seedInitialDataIfEmpty()
    }
  }

  // Repository Flows
  val dailyChallenge = repository.dailyChallenge
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val allChallenges = repository.allChallenges
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val completions = repository.allCompletions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalCompletionsCount = repository.totalCompletionsCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val chains = repository.allChains
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val primaryFamilyCircle = repository.primaryFamilyCircle
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val familyResponses = repository.familyResponses
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val availableMemoryMonths = repository.availableMemoryMonths
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("September 2026", "August 2026"))

  val notifications = repository.allNotifications
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val unreadNotificationsCount = repository.unreadNotificationsCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  // Interactive 60-Second Challenge Execution State
  private val _activeChallenge = MutableStateFlow<ChallengeEntity?>(null)
  val activeChallenge: StateFlow<ChallengeEntity?> = _activeChallenge.asStateFlow()

  private val _timerSecondsRemaining = MutableStateFlow(60)
  val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

  private val _isTimerRunning = MutableStateFlow(false)
  val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

  private var timerJob: Job? = null

  private val _responseFormat = MutableStateFlow(ResponseFormat.TEXT)
  val responseFormat: StateFlow<ResponseFormat> = _responseFormat.asStateFlow()

  private val _textResponse = MutableStateFlow("")
  val textResponse: StateFlow<String> = _textResponse.asStateFlow()

  private val _photoUri = MutableStateFlow<String?>(null)
  val photoUri: StateFlow<String?> = _photoUri.asStateFlow()

  private val _drawingStrokes = MutableStateFlow<List<DrawStroke>>(emptyList())
  val drawingStrokes: StateFlow<List<DrawStroke>> = _drawingStrokes.asStateFlow()

  private val _selectedDrawingColor = MutableStateFlow(Color(0xFF4F46E5))
  val selectedDrawingColor: StateFlow<Color> = _selectedDrawingColor.asStateFlow()

  private val _selectedStrokeWidth = MutableStateFlow(8f)
  val selectedStrokeWidth: StateFlow<Float> = _selectedStrokeWidth.asStateFlow()

  private val _selectedQuickChoice = MutableStateFlow("")
  val selectedQuickChoice: StateFlow<String> = _selectedQuickChoice.asStateFlow()

  private val _selectedCardPreset = MutableStateFlow("VIBRANT")
  val selectedCardPreset: StateFlow<String> = _selectedCardPreset.asStateFlow()

  // Selected Chain Details
  private val _selectedChainId = MutableStateFlow<String?>(null)
  val selectedChainId: StateFlow<String?> = _selectedChainId.asStateFlow()

  val selectedChainParticipants: StateFlow<List<ChainParticipantEntity>> = combine(_selectedChainId, chains) { selId, chainList ->
    selId ?: chainList.firstOrNull()?.id
  }.flatMapLatest { id ->
    if (id != null) repository.getChainParticipants(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Search & Filter State
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategoryFilter = MutableStateFlow<ChallengeCategory?>(null)
  val selectedCategoryFilter: StateFlow<ChallengeCategory?> = _selectedCategoryFilter.asStateFlow()

  // Memory Book selected month
  private val _selectedMemoryMonth = MutableStateFlow("September 2026")
  val selectedMemoryMonth: StateFlow<String> = _selectedMemoryMonth.asStateFlow()

  // Onboarding state
  private val _showOnboarding = MutableStateFlow(false)
  val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

  // Streaks & Badges: starts strictly at 0 streak when there are no completions
  val streakDays: StateFlow<Int> = completions.map { list ->
    if (list.isEmpty()) {
      0
    } else {
      val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
      val uniqueDays = list.map { sdf.format(java.util.Date(it.completedAt)) }.distinct()
      uniqueDays.size
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val badges: StateFlow<List<BadgeItem>> = combine(completions, chains, familyResponses, streakDays) { comps, chs, fResps, streak ->
    listOf(
      BadgeItem(
        id = "first_minute",
        title = "First Minute",
        description = "Completed your very first 60-second challenge.",
        iconEmoji = "⚡",
        isUnlocked = comps.isNotEmpty(),
        unlockRequirement = "Complete 1 challenge"
      ),
      BadgeItem(
        id = "streak_master",
        title = "7-Day Flame",
        description = "Kept your 60-second daily streak alive for 7 days in a row.",
        iconEmoji = "🔥",
        isUnlocked = streak >= 7,
        unlockRequirement = "7-Day Streak"
      ),
      BadgeItem(
        id = "chain_igniter",
        title = "Chain Catalyst",
        description = "Started a Challenge Chain that spread across multiple friends.",
        iconEmoji = "🌐",
        isUnlocked = chs.any { it.totalParticipants >= 3 },
        unlockRequirement = "Reach 3+ friends in a chain"
      ),
      BadgeItem(
        id = "family_heart",
        title = "Family Chronicler",
        description = "Contributed memories and drawings to the Family Memory Book.",
        iconEmoji = "🏡",
        isUnlocked = fResps.any { it.authorName == _userProfile.value.name },
        unlockRequirement = "Post in Family Circle"
      ),
      BadgeItem(
        id = "speed_artist",
        title = "60s Michelangelo",
        description = "Completed a speed doodle challenge before the buzzer sounded.",
        iconEmoji = "🎨",
        isUnlocked = comps.any { it.formatUsed == ResponseFormat.DRAWING.name },
        unlockRequirement = "Complete a Drawing challenge"
      ),
      BadgeItem(
        id = "kind_spirit",
        title = "Secret Agent of Kindness",
        description = "Spread positive micro-actions to friends and family.",
        iconEmoji = "💛",
        isUnlocked = comps.any { it.category == "KINDNESS" },
        unlockRequirement = "Finish a Kindness prompt"
      ),
      BadgeItem(
        id = "shutterbug",
        title = "Flash Photographer",
        description = "Captured a creative photo angle in under 60 seconds.",
        iconEmoji = "📸",
        isUnlocked = comps.any { it.formatUsed == ResponseFormat.PHOTO.name },
        unlockRequirement = "Submit a Photo challenge"
      ),
      BadgeItem(
        id = "century_club",
        title = "Speed Legend",
        description = "Complete 25 one-minute challenges across all 10 categories.",
        iconEmoji = "👑",
        isUnlocked = comps.size >= 25,
        unlockRequirement = "Complete 25 challenges"
      )
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // --- ACTIONS ---

  fun openChallenge(challenge: ChallengeEntity) {
    _activeChallenge.value = challenge
    _timerSecondsRemaining.value = challenge.timeLimitSeconds
    _isTimerRunning.value = false
    timerJob?.cancel()
    _responseFormat.value = ResponseFormat.fromString(challenge.suggestedFormat)
    _textResponse.value = ""
    _photoUri.value = null
    _drawingStrokes.value = emptyList()
    _selectedQuickChoice.value = ""
  }

  fun startTimer() {
    if (_isTimerRunning.value) return
    _isTimerRunning.value = true
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (_isTimerRunning.value && _timerSecondsRemaining.value > 0) {
        delay(1000L)
        if (_timerSecondsRemaining.value > 0) {
          _timerSecondsRemaining.value -= 1
        }
      }
      _isTimerRunning.value = false
    }
  }

  fun pauseTimer() {
    _isTimerRunning.value = false
    timerJob?.cancel()
  }

  fun resetTimer() {
    _isTimerRunning.value = false
    timerJob?.cancel()
    _timerSecondsRemaining.value = _activeChallenge.value?.timeLimitSeconds ?: 60
  }

  fun setResponseFormat(format: ResponseFormat) {
    _responseFormat.value = format
  }

  fun setTextResponse(text: String) {
    _textResponse.value = text
  }

  fun setPhotoUri(uri: String?) {
    _photoUri.value = uri
  }

  fun addDrawingStroke(stroke: DrawStroke) {
    _drawingStrokes.value = _drawingStrokes.value + stroke
  }

  fun undoDrawingStroke() {
    if (_drawingStrokes.value.isNotEmpty()) {
      _drawingStrokes.value = _drawingStrokes.value.dropLast(1)
    }
  }

  fun clearDrawing() {
    _drawingStrokes.value = emptyList()
  }

  fun setSelectedDrawingColor(color: Color) {
    _selectedDrawingColor.value = color
  }

  fun setSelectedStrokeWidth(width: Float) {
    _selectedStrokeWidth.value = width
  }

  fun setSelectedQuickChoice(choice: String) {
    _selectedQuickChoice.value = choice
  }

  fun setCardPreset(preset: String) {
    _selectedCardPreset.value = preset
  }

  fun selectChain(chainId: String) {
    _selectedChainId.value = chainId
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun selectCategoryFilter(category: ChallengeCategory?) {
    _selectedCategoryFilter.value = category
  }

  fun setSelectedMemoryMonth(month: String) {
    _selectedMemoryMonth.value = month
  }

  fun dismissOnboarding() {
    _showOnboarding.value = false
  }

  fun openOnboarding() {
    _showOnboarding.value = true
  }

  fun completeProfileSetup(name: String, handle: String, avatar: String) {
    val cleanName = name.trim().ifEmpty { "Alex" }
    val formattedHandle = if (handle.trim().startsWith("@")) handle.trim() else "@${handle.trim().ifEmpty { "player1m" }}"
    
    prefs.edit()
      .putString("full_name", cleanName)
      .putString("handle", formattedHandle)
      .putString("avatar_emoji", avatar)
      .putBoolean("is_profile_configured", true)
      .apply()

    _userProfile.value = _userProfile.value.copy(
      name = cleanName,
      handle = formattedHandle,
      avatarEmoji = avatar,
      isProfileConfigured = true
    )
  }

  fun updateProfile(name: String, handle: String, avatar: String) {
    val cleanName = name.trim().ifEmpty { _userProfile.value.name.ifEmpty { "Alex" } }
    val rawHandle = handle.trim()
    val formattedHandle = if (rawHandle.startsWith("@")) rawHandle else "@${rawHandle.ifEmpty { "player1m" }}"

    prefs.edit()
      .putString("full_name", cleanName)
      .putString("handle", formattedHandle)
      .putString("avatar_emoji", avatar)
      .apply()

    _userProfile.value = _userProfile.value.copy(
      name = cleanName,
      handle = formattedHandle,
      avatarEmoji = avatar
    )
  }

  fun resetProfileSetup() {
    prefs.edit().clear().apply()
    _userProfile.value = UserProfileState(
      name = "",
      handle = "",
      avatarEmoji = "⚡",
      isProfileConfigured = false
    )
  }

  fun markNotificationsRead() {
    viewModelScope.launch {
      repository.markAllNotificationsAsRead()
    }
  }

  fun submitCurrentChallenge(onCompleted: (CompletionEntity) -> Unit) {
    val challenge = _activeChallenge.value ?: return
    pauseTimer()

    val secondsTaken = challenge.timeLimitSeconds - _timerSecondsRemaining.value
    val timeToSave = if (secondsTaken <= 0) 58 else secondsTaken

    val completion = CompletionEntity(
      challengeId = challenge.id,
      challengeTitle = challenge.title,
      category = challenge.category,
      formatUsed = _responseFormat.value.name,
      textContent = if (_responseFormat.value == ResponseFormat.QUICK_CHOICE) _selectedQuickChoice.value else _textResponse.value,
      mediaUri = _photoUri.value ?: "",
      drawingPointsJson = if (_drawingStrokes.value.isNotEmpty()) "has_drawing_${_drawingStrokes.value.size}_strokes" else "",
      secondsTaken = timeToSave,
      sharePreset = _selectedCardPreset.value
    )

    viewModelScope.launch {
      val id = repository.saveCompletion(completion)
      onCompleted(completion.copy(id = id))
    }
  }

  fun launchChallengeChain(
    friends: List<String>,
    challengeTitleOverride: String? = null,
    categoryOverride: String? = null,
    onLaunched: () -> Unit
  ) {
    val fallbackTitle = dailyChallenge.value?.title ?: "Draw a Super-Pet in 60 Seconds"
    val fallbackCategory = dailyChallenge.value?.category ?: "CREATIVITY"
    val challenge = _activeChallenge.value

    val targetTitle = challengeTitleOverride ?: challenge?.title ?: fallbackTitle
    val targetCategory = categoryOverride ?: challenge?.category ?: fallbackCategory
    val targetOriginId = challenge?.id ?: "ch_daily_today"

    viewModelScope.launch {
      repository.createOrJoinChain(
        originChallengeId = targetOriginId,
        challengeTitle = targetTitle,
        category = targetCategory,
        creatorName = _userProfile.value.name.ifBlank { "You" },
        friendNames = friends.filter { it.isNotBlank() }
      )
      onLaunched()
    }
  }

  fun simulateChainInvite() {
    val chainId = _selectedChainId.value ?: chains.value.firstOrNull()?.id ?: return
    viewModelScope.launch {
      repository.simulateChainGrowth(chainId)
    }
  }

  fun submitFamilyResponse(
    authorName: String,
    authorRole: String,
    avatarEmoji: String,
    content: String,
    format: ResponseFormat,
    mediaUri: String = ""
  ) {
    val challengeTitle = _activeChallenge.value?.title ?: "Family Daily Question"
    val category = _activeChallenge.value?.category ?: "FAMILY"

    viewModelScope.launch {
      repository.addFamilyResponse(
        authorName = authorName,
        authorRole = authorRole,
        avatarEmoji = avatarEmoji,
        challengeTitle = challengeTitle,
        category = category,
        responseFormat = format.name,
        textContent = content,
        mediaUri = mediaUri
      )
    }
  }
}
