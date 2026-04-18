package com.example.queueapp.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.queueapp.notification.NotificationHelper
import com.example.queueapp.ui.screens.*
import com.example.queueapp.viewmodel.AppViewModel

object Routes {
    const val ROLE_SELECTION  = "role_selection"
    const val CLIENT_MAP      = "client_map"
    const val ENTITY_DETAIL   = "entity_detail/{entityId}"
    const val TICKET_INFO     = "ticket_info"
    const val MANAGER_HOME    = "manager_home"
    const val CREATE_ENTITY   = "create_entity"
    const val ENTITY_DESKS    = "entity_desks/{entityId}"
    const val ADD_DESK        = "add_desk/{entityId}"

    fun entityDetail(entityId: String) = "entity_detail/$entityId"
    fun entityDesks(entityId: String)  = "entity_desks/$entityId"
    fun addDesk(entityId: String)      = "add_desk/$entityId"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    context: Context,
    viewModel: AppViewModel = viewModel()
) {
    // Observe notification events emitted by the ViewModel
    LaunchedEffect(Unit) {
        viewModel.notificationEvents.collect { event ->
            NotificationHelper.sendNotification(context, event.title, event.message)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.ROLE_SELECTION
    ) {
        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onClientSelected  = { navController.navigate(Routes.CLIENT_MAP) },
                onManagerSelected = { navController.navigate(Routes.MANAGER_HOME) }
            )
        }

        composable(Routes.CLIENT_MAP) {
            ClientMapScreen(
                viewModel   = viewModel,
                onEntityClick = { entityId ->
                    navController.navigate(Routes.entityDetail(entityId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route     = Routes.ENTITY_DETAIL,
            arguments = listOf(navArgument("entityId") { type = NavType.StringType })
        ) { backStack ->
            val entityId = backStack.arguments?.getString("entityId") ?: return@composable
            EntityDetailScreen(
                entityId  = entityId,
                viewModel = viewModel,
                onTicketTaken = { navController.navigate(Routes.TICKET_INFO) },
                onBackClick   = { navController.popBackStack() }
            )
        }

        composable(Routes.TICKET_INFO) {
            TicketInfoScreen(
                viewModel   = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.MANAGER_HOME) {
            ManagerHomeScreen(
                viewModel          = viewModel,
                onCreateEntity     = { navController.navigate(Routes.CREATE_ENTITY) },
                onEntityClick      = { entityId -> navController.navigate(Routes.entityDesks(entityId)) },
                onBackClick        = { navController.popBackStack() }
            )
        }

        composable(Routes.CREATE_ENTITY) {
            CreateEntityScreen(
                viewModel   = viewModel,
                onSaved     = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route     = Routes.ENTITY_DESKS,
            arguments = listOf(navArgument("entityId") { type = NavType.StringType })
        ) { backStack ->
            val entityId = backStack.arguments?.getString("entityId") ?: return@composable
            EntityDesksScreen(
                entityId  = entityId,
                viewModel = viewModel,
                onAddDesk = { navController.navigate(Routes.addDesk(entityId)) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route     = Routes.ADD_DESK,
            arguments = listOf(navArgument("entityId") { type = NavType.StringType })
        ) { backStack ->
            val entityId = backStack.arguments?.getString("entityId") ?: return@composable
            AddDeskScreen(
                entityId  = entityId,
                viewModel = viewModel,
                onSaved     = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
