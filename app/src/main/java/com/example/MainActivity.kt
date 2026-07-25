package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.UserEntity
import com.example.data.UserRole
import com.example.ui.AppViewModel
import com.example.ui.components.AuthDialog
import com.example.ui.components.CartAndCheckoutDialog
import com.example.ui.components.HeaderAndTopBar
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CustomerHomeScreen
import com.example.ui.screens.DriverAppScreen
import com.example.ui.screens.MerchantAppScreen
import com.example.ui.theme.EgyptEmerald
import com.example.ui.theme.WithEgyptTheme

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val user by viewModel.userState.collectAsStateWithLifecycle()
            val stores by viewModel.storesState.collectAsStateWithLifecycle()
            val products by viewModel.allProductsState.collectAsStateWithLifecycle()
            val activeOrders by viewModel.activeOrdersState.collectAsStateWithLifecycle()
            val allOrders by viewModel.allOrdersState.collectAsStateWithLifecycle()
            val cartItems by viewModel.cartItemsState.collectAsStateWithLifecycle()
            val driverDocs by viewModel.driverDocsState.collectAsStateWithLifecycle()
            val notificationSnack by viewModel.notificationSnack.collectAsStateWithLifecycle()

            val appliedDiscount by viewModel.appliedDiscountEgp.collectAsStateWithLifecycle()
            val promoMessage by viewModel.promoMessage.collectAsStateWithLifecycle()

            var showCartDialog by remember { mutableStateOf(false) }
            var showAuthDialog by remember { mutableStateOf(false) }

            val currentUser = user ?: UserEntity()
            val isRtl = currentUser.isRtlLanguage
            val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(notificationSnack) {
                notificationSnack?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    viewModel.clearNotification()
                }
            }

            WithEgyptTheme {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            HeaderAndTopBar(
                                currentRole = currentUser.currentRole,
                                isRtl = isRtl,
                                cartItemCount = cartItems.sumOf { it.quantity },
                                onRoleSelected = { viewModel.switchRole(it) },
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                onOpenCart = { showCartDialog = true },
                                onOpenLogin = { showAuthDialog = true }
                            )
                        },
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState) { data ->
                                Snackbar(
                                    snackbarData = data,
                                    containerColor = EgyptEmerald,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentUser.currentRole,
                                label = "role_navigation"
                            ) { role ->
                                when (role) {
                                    UserRole.CUSTOMER -> CustomerHomeScreen(
                                        viewModel = viewModel,
                                        stores = stores,
                                        products = products,
                                        activeOrders = activeOrders,
                                        allOrders = allOrders,
                                        isRtl = isRtl,
                                        onOpenCart = { showCartDialog = true }
                                    )

                                    UserRole.DRIVER -> DriverAppScreen(
                                        viewModel = viewModel,
                                        driverDocs = driverDocs,
                                        orders = allOrders,
                                        isRtl = isRtl
                                    )

                                    UserRole.MERCHANT -> MerchantAppScreen(
                                        viewModel = viewModel,
                                        products = products,
                                        orders = allOrders,
                                        isRtl = isRtl
                                    )

                                    UserRole.ADMIN -> AdminDashboardScreen(
                                        viewModel = viewModel,
                                        orders = allOrders,
                                        isRtl = isRtl
                                    )
                                }
                            }
                        }

                        // Shopping Cart & Payment Dialog
                        if (showCartDialog) {
                            CartAndCheckoutDialog(
                                viewModel = viewModel,
                                cartItems = cartItems,
                                appliedDiscount = appliedDiscount,
                                promoMessage = promoMessage,
                                isRtl = isRtl,
                                onDismiss = { showCartDialog = false }
                            )
                        }

                        // Auth / Sign-in Dialog
                        if (showAuthDialog) {
                            AuthDialog(
                                viewModel = viewModel,
                                isRtl = isRtl,
                                onDismiss = { showAuthDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
