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
import com.example.queueapp.R
import com.example.queueapp.notification.NotificationHelper
import com.example.queueapp.ui.screens.*
import com.example.queueapp.viewmodel.AppViewModel

object Routes {
    const val ROLE_SELECTION = "role_selection"
    const val CLIENT_MAP     = "client_map"
    const val ENTITY_DETAIL  = "entity_detail/{entityId}"
    const val TICKET_INFO    = "ticket_info/{deskId}"
    const val MY_TICKETS     = "my_tickets"
    const val MANAGER_HOME   = "manager_home"
    const val CREATE_ENTITY  = "create_entity"
    const val ENTITY_DESKS   = "entity_desks/{entityId}"
    const val ADD_DESK       = "add_desk/{entityId}"

    fun entityDetail(entityId: String) = "entity_detail/$entityId"
    fun ticketInfo(deskId: String)     = "ticket_info/$deskId"
    fun entityDesks(entityId: String)  = "entity_desks/$entityId"
    fun addDesk(entityId: String)      = "add_desk/$entityId"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    context: Context,
    viewModel: AppViewModel = viewModel()
) {
    // Collect notification events from ViewModel and fire system notifications
    LaunchedEffect(Unit) {
        viewModel.notificationEvents.collect { event ->
            val title   = context.getString(R.string.notif_title)
            val message = context.getString(R.string.notif_message, event.remainingCount, event.deskName)
            NotificationHelper.sendNotification(context, title, message)
        }
    }

    NavHost(navController = navController, startDestination = Routes.ROLE_SELECTION) {

        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onClientSelected  = { navController.navigate(Routes.CLIENT_MAP) },
                onManagerSelected = { navController.navigate(Routes.MANAGER_HOME) }
            )
        }

        composable(Routes.CLIENT_MAP) {
            ClientMapScreen(
                viewModel     = viewModel,
                onEntityClick = { navController.navigate(Routes.entityDetail(it)) },
                onMyTickets   = { navController.navigate(Routes.MY_TICKETS) },
                onBackClick   = { navController.popBackStack() }
            )
        }

        composable(
            route     = Routes.ENTITY_DETAIL,
            arguments = listOf(navArgument("entityId") { type = NavType.StringType })
        ) { back ->
            EntityDetailScreen(
                entityId     = back.arguments!!.getString("entityId")!!,
                viewModel    = viewModel,
                onTicketTaken = { deskId -> navController.navigate(Routes.ticketInfo(deskId)) },
                onBackClick  = { navController.popBackStack() }
            )
        }

        composable(
            route     = Routes.TICKET_INFO,
            arguments = listOf(navArgument("deskId") { type = NavType.StringType })
        ) { back ->
            TicketInfoScreen(
                deskId      = back.arguments!!.getString("deskId")!!,
                viewModel   = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.MY_TICKETS) {
            MyTicketsScreen(
                viewModel     = viewModel,
                onTicketClick = { deskId -> navController.navigate(Routes.ticketInfo(deskId)) },
                onBackClick   = { navController.popBackStack() }
            )
        }

        composable(Routes.MANAGER_HOME) {
            ManagerHomeScreen(
                viewModel      = viewModel,
                onCreateEntity = { navController.navigate(Routes.CREATE_ENTITY) },
                onEntityClick  = { navController.navigate(Routes.entityDesks(it)) },
                onBackClick    = { navController.popBackStack() }
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
        ) { back ->
            EntityDesksScreen(
                entityId    = back.arguments!!.getString("entityId")!!,
                viewModel   = viewModel,
                onAddDesk   = { entityId -> navController.navigate(Routes.addDesk(entityId)) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route     = Routes.ADD_DESK,
            arguments = listOf(navArgument("entityId") { type = NavType.StringType })
        ) { back ->
            AddDeskScreen(
                entityId    = back.arguments!!.getString("entityId")!!,
                viewModel   = viewModel,
                onSaved     = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
