package com.example.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.local.entities.CompletionEntity
import com.example.ui.screens.*
import com.example.ui.viewmodel.OneMinuteViewModel

sealed class Screen(
  val route: String,
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  data object Home : Screen("home", "Today", Icons.Filled.Home, Icons.Outlined.Home)
  data object Explore : Screen("explore", "Discover", Icons.Filled.Search, Icons.Outlined.Search)
  data object Chains : Screen("chains", "Chains", Icons.Filled.Share, Icons.Outlined.Share)
  data object Family : Screen("family", "Family", Icons.Filled.FamilyRestroom, Icons.Outlined.FamilyRestroom)
  data object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)

  data object CompleteChallenge : Screen("complete_challenge", "Challenge", Icons.Filled.Timer, Icons.Outlined.Timer)
  data object ShareCard : Screen("share_card", "Share", Icons.Filled.Share, Icons.Outlined.Share)
  data object MemoryBook : Screen("memory_book", "Memory Book", Icons.Filled.Book, Icons.Outlined.Book)
  data object Friends : Screen("friends", "Friends", Icons.Filled.People, Icons.Outlined.People)
  data object Notifications : Screen("notifications", "Notifications", Icons.Filled.Notifications, Icons.Outlined.Notifications)
}

@Composable
fun AppNavigation(viewModel: OneMinuteViewModel) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  val bottomNavItems = listOf(
    Screen.Home,
    Screen.Explore,
    Screen.Chains,
    Screen.Family,
    Screen.Profile
  )

  var completedEntityForShare by remember { mutableStateOf<CompletionEntity?>(null) }
  val showOnboarding by viewModel.showOnboarding.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()

  if (!userProfile.isProfileConfigured) {
    ProfileSetupScreen(
      onSetupComplete = { name, handle, avatar ->
        viewModel.completeProfileSetup(name, handle, avatar)
      }
    )
    return
  }

  val showBottomBar = currentRoute in bottomNavItems.map { it.route }

  Scaffold(
    bottomBar = {
      if (showBottomBar) {
        NavigationBar(
          modifier = Modifier.testTag("bottom_navigation_bar"),
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = NavigationBarDefaults.Elevation
        ) {
          bottomNavItems.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
              icon = {
                Icon(
                  imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                  contentDescription = screen.title
                )
              },
              label = {
                Text(
                  screen.title,
                  fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 12.sp
                )
              },
              selected = selected,
              onClick = {
                navController.navigate(screen.route) {
                  popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                  }
                  launchSingleTop = true
                  restoreState = true
                }
              },
              modifier = Modifier.testTag("nav_item_${screen.route}")
            )
          }
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = Screen.Home.route,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      enterTransition = { fadeIn() },
      exitTransition = { fadeOut() }
    ) {
      composable(Screen.Home.route) {
        HomeScreen(
          viewModel = viewModel,
          onOpenChallenge = { challenge ->
            viewModel.openChallenge(challenge)
            navController.navigate(Screen.CompleteChallenge.route)
          },
          onNavigateToChains = {
            navController.navigate(Screen.Chains.route)
          },
          onNavigateToFamily = {
            navController.navigate(Screen.Family.route)
          },
          onNavigateToNotifications = {
            navController.navigate(Screen.Notifications.route)
          },
          onNavigateToCategoryExplore = { cat ->
            viewModel.selectCategoryFilter(cat)
            navController.navigate(Screen.Explore.route)
          }
        )
      }

      composable(Screen.Explore.route) {
        ExploreScreen(
          viewModel = viewModel,
          onOpenChallenge = { challenge ->
            viewModel.openChallenge(challenge)
            navController.navigate(Screen.CompleteChallenge.route)
          }
        )
      }

      composable(Screen.Chains.route) {
        ChallengeChainScreen(
          viewModel = viewModel,
          onNavigateBack = {
            navController.popBackStack()
          }
        )
      }

      composable(Screen.Family.route) {
        FamilyCircleScreen(
          viewModel = viewModel,
          onNavigateBack = {
            navController.popBackStack()
          },
          onOpenMemoryBook = {
            navController.navigate(Screen.MemoryBook.route)
          }
        )
      }

      composable(Screen.Profile.route) {
        ProfileScreen(
          viewModel = viewModel,
          onNavigateToNotifications = {
            navController.navigate(Screen.Notifications.route)
          },
          onNavigateToFriends = {
            navController.navigate(Screen.Friends.route)
          },
          onShowOnboarding = {
            viewModel.openOnboarding()
          }
        )
      }

      composable(Screen.CompleteChallenge.route) {
        CompleteChallengeScreen(
          viewModel = viewModel,
          onNavigateBack = {
            navController.popBackStack()
          },
          onChallengeCompleted = { completion ->
            completedEntityForShare = completion
            navController.navigate(Screen.ShareCard.route) {
              popUpTo(Screen.CompleteChallenge.route) { inclusive = true }
            }
          }
        )
      }

      composable(Screen.ShareCard.route) {
        ShareCardScreen(
          viewModel = viewModel,
          completion = completedEntityForShare,
          onNavigateHome = {
            navController.navigate(Screen.Home.route) {
              popUpTo(Screen.Home.route) { inclusive = true }
            }
          },
          onNavigateToChains = {
            navController.navigate(Screen.Chains.route)
          }
        )
      }

      composable(Screen.MemoryBook.route) {
        MemoryBookScreen(
          viewModel = viewModel,
          onNavigateBack = {
            navController.popBackStack()
          }
        )
      }

      composable(Screen.Friends.route) {
        FriendsScreen(
          viewModel = viewModel,
          onNavigateBack = {
            navController.popBackStack()
          }
        )
      }

      composable(Screen.Notifications.route) {
        NotificationsScreen(
          viewModel = viewModel,
          onNavigateBack = {
            navController.popBackStack()
          }
        )
      }
    }
  }

  if (showOnboarding) {
    OnboardingDialog(
      onDismiss = { viewModel.dismissOnboarding() }
    )
  }
}
