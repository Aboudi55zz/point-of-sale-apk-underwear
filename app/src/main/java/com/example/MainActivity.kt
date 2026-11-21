package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.PosRepository
import com.example.ui.PosViewModel
import com.example.ui.components.ReceiptDialog
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.PosScreen
import com.example.ui.screens.SalesHistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AppColorPalette
import com.example.ui.theme.SyriaPosTheme
import com.example.ui.theme.WarningAmber

enum class AppDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    POS("نقطة البيع", Icons.Filled.PointOfSale, Icons.Outlined.PointOfSale, "nav_pos"),
    INVENTORY("المخزون", Icons.Filled.Inventory2, Icons.Outlined.Inventory2, "nav_inventory"),
    INVOICES("الفواتير", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong, "nav_invoices"),
    SETTINGS("الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                SyriaPosApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyriaPosApp() {
    val context = LocalContext.current
    val repository = remember(context) {
        val db = AppDatabase.getInstance(context.applicationContext)
        PosRepository(db.posDao(), context.applicationContext)
    }
    val viewModel: PosViewModel = viewModel(factory = PosViewModel.factory(repository))

    val brands by viewModel.brands.collectAsStateWithLifecycle()
    val allModels by viewModel.allModels.collectAsStateWithLifecycle()
    val filteredPosModels by viewModel.filteredPosModels.collectAsStateWithLifecycle()
    val filteredInvModels by viewModel.filteredInventoryModels.collectAsStateWithLifecycle()
    val posFilter by viewModel.posFilter.collectAsStateWithLifecycle()
    val invFilter by viewModel.invFilter.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val discountSyp by viewModel.discountSyp.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val currencyScale by viewModel.currencyScale.collectAsStateWithLifecycle()
    val storeName by viewModel.storeName.collectAsStateWithLifecycle()
    val appPalette by viewModel.appPalette.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val lastCompletedInvoice by viewModel.lastCompletedInvoice.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    var currentDest by rememberSaveable { mutableStateOf(AppDestination.POS) }
    val snackbarHostState = remember { SnackbarHostState() }

    val cartTotalQty = remember(cartItems) { cartItems.sumOf { it.quantity } }
    val lowStockCount = remember(allModels) {
        allModels.sumOf { m -> m.pieces.count { it.stockQty <= it.minStockAlert } }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.consumeSnackbar()
        }
    }

    if (currentDest != AppDestination.POS) {
        BackHandler { currentDest = AppDestination.POS }
    }

    SyriaPosTheme(palette = appPalette, themeMode = themeMode) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useRail = maxWidth >= 640.dp

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    Column {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = when (currentDest) {
                                        AppDestination.POS -> storeName
                                        AppDestination.INVENTORY -> "إدارة المخزون والكتالوج"
                                        AppDestination.INVOICES -> "سجل الفواتير والمبيعات"
                                        AppDestination.SETTINGS -> "الإعدادات"
                                    },
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            actions = {
                                FilledTonalIconButton(
                                    onClick = {
                                        val entries = AppColorPalette.entries
                                        val next = entries[(entries.indexOf(appPalette) + 1) % entries.size]
                                        viewModel.setAppPalette(next)
                                    },
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .testTag("quick_palette_cycle")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Palette,
                                        contentDescription = "تبديل تناسق الألوان"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                },
                bottomBar = {
                    if (!useRail) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            AppDestination.entries.forEach { dest ->
                                val selected = currentDest == dest
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentDest = dest },
                                    icon = {
                                        DestinationBadgedIcon(
                                            dest = dest,
                                            selected = selected,
                                            cartTotalQty = cartTotalQty,
                                            lowStockCount = lowStockCount
                                        )
                                    },
                                    label = { Text(dest.title) },
                                    modifier = Modifier.testTag(dest.testTag)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useRail) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            header = {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    androidx.compose.foundation.layout.Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.PointOfSale,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            },
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            AppDestination.entries.forEach { dest ->
                                val selected = currentDest == dest
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { currentDest = dest },
                                    icon = {
                                        DestinationBadgedIcon(
                                            dest = dest,
                                            selected = selected,
                                            cartTotalQty = cartTotalQty,
                                            lowStockCount = lowStockCount
                                        )
                                    },
                                    label = { Text(dest.title) },
                                    modifier = Modifier.testTag(dest.testTag)
                                )
                            }
                        }
                    }

                    when (currentDest) {
                        AppDestination.POS -> PosScreen(
                            brands = brands,
                            models = filteredPosModels,
                            filter = posFilter,
                            cartItems = cartItems,
                            discountSyp = discountSyp,
                            currencyScale = currencyScale,
                            onSearchChange = viewModel::updatePosSearch,
                            onSelectBrand = viewModel::updatePosBrand,
                            onSelectCategory = viewModel::updatePosCategory,
                            onToggleCurrencyScale = viewModel::toggleCurrencyScale,
                            onAddToCart = viewModel::addToCart,
                            onUpdateCartQty = viewModel::updateCartQuantity,
                            onRemoveCartItem = viewModel::removeFromCart,
                            onClearCart = viewModel::clearCart,
                            onDiscountChange = viewModel::setDiscount,
                            onCheckout = viewModel::completeCheckout,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )

                        AppDestination.INVENTORY -> InventoryScreen(
                            brands = brands,
                            allModels = allModels,
                            filteredModels = filteredInvModels,
                            filter = invFilter,
                            currencyScale = currencyScale,
                            onSearchChange = viewModel::updateInvSearch,
                            onSelectBrand = viewModel::updateInvBrand,
                            onSelectCategory = viewModel::updateInvCategory,
                            onToggleLowStock = viewModel::toggleInvLowStock,
                            onAddBrand = viewModel::addBrand,
                            onUpdateBrand = viewModel::updateBrand,
                            onDeleteBrand = viewModel::deleteBrand,
                            onAddModel = viewModel::addModel,
                            onUpdateModel = viewModel::updateModel,
                            onDeleteModel = viewModel::deleteModel,
                            onAddPiece = viewModel::addPiece,
                            onUpdatePiece = viewModel::updatePiece,
                            onDeletePiece = viewModel::deletePiece,
                            onAdjustStock = viewModel::adjustPieceStock,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )

                        AppDestination.INVOICES -> SalesHistoryScreen(
                            invoices = invoices,
                            storeName = storeName,
                            currencyScale = currencyScale,
                            onDeleteInvoice = viewModel::deleteInvoice,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )

                        AppDestination.SETTINGS -> SettingsScreen(
                            storeName = storeName,
                            currencyScale = currencyScale,
                            appPalette = appPalette,
                            themeMode = themeMode,
                            brandCount = brands.size,
                            modelCount = allModels.size,
                            pieceCount = allModels.sumOf { it.pieces.size },
                            onSaveStoreName = viewModel::setStoreName,
                            onSelectCurrencyScale = viewModel::setCurrencyScale,
                            onSelectPalette = viewModel::setAppPalette,
                            onSelectThemeMode = viewModel::setThemeMode,
                            onResetDefaultCatalog = viewModel::resetDefaultCatalog,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            }
        }

        lastCompletedInvoice?.let { completed ->
            ReceiptDialog(
                invoiceWithItems = completed,
                storeName = storeName,
                currencyScale = currencyScale,
                onDismiss = viewModel::dismissCompletedInvoice
            )
        }
    }
}

@Composable
private fun DestinationBadgedIcon(
    dest: AppDestination,
    selected: Boolean,
    cartTotalQty: Int,
    lowStockCount: Int
) {
    val badgeCount = when (dest) {
        AppDestination.POS -> cartTotalQty
        AppDestination.INVENTORY -> lowStockCount
        else -> 0
    }
    if (badgeCount > 0) {
        BadgedBox(
            badge = {
                Badge(
                    containerColor = if (dest == AppDestination.INVENTORY) WarningAmber
                    else MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ) {
                    Text(if (badgeCount > 99) "99+" else "$badgeCount")
                }
            }
        ) {
            Icon(
                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                contentDescription = dest.title
            )
        }
    } else {
        Icon(
            imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
            contentDescription = dest.title
        )
    }
}
