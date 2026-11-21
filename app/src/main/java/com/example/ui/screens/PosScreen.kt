package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.local.BrandEntity
import com.example.data.local.ModelWithDetails
import com.example.data.local.PieceVariantEntity
import com.example.ui.CartItem
import com.example.ui.PosFilterState
import com.example.ui.components.BrandBadge
import com.example.ui.components.BrandFilterRow
import com.example.ui.components.CategoryFilterRow
import com.example.ui.theme.AlertRed
import com.example.ui.theme.WarningAmber
import com.example.util.CurrencyFormatter
import com.example.util.CurrencyScale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    brands: List<BrandEntity>,
    models: List<ModelWithDetails>,
    filter: PosFilterState,
    cartItems: List<CartItem>,
    discountSyp: Long,
    currencyScale: CurrencyScale,
    onSearchChange: (String) -> Unit,
    onSelectBrand: (Long?) -> Unit,
    onSelectCategory: (String) -> Unit,
    onToggleCurrencyScale: () -> Unit,
    onAddToCart: (ModelWithDetails, PieceVariantEntity) -> Unit,
    onUpdateCartQty: (Long, Int) -> Unit,
    onRemoveCartItem: (Long) -> Unit,
    onClearCart: () -> Unit,
    onDiscountChange: (Long) -> Unit,
    onCheckout: (customerName: String, paidSyp: Long?, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCartSheet by remember { mutableStateOf(false) }
    val cartCount = cartItems.sumOf { it.quantity }
    val subtotal = cartItems.sumOf { it.lineTotalSyp }
    val effectiveDiscount = discountSyp.coerceIn(0L, subtotal)
    val total = max(0L, subtotal - effectiveDiscount)
    val cartQtyMap = remember(cartItems) { cartItems.associate { it.pieceId to it.quantity } }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 800.dp

        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                PosCatalogPane(
                    brands = brands,
                    models = models,
                    filter = filter,
                    cartQtyMap = cartQtyMap,
                    currencyScale = currencyScale,
                    onSearchChange = onSearchChange,
                    onSelectBrand = onSelectBrand,
                    onSelectCategory = onSelectCategory,
                    onToggleCurrencyScale = onToggleCurrencyScale,
                    onAddToCart = onAddToCart,
                    modifier = Modifier
                        .weight(1.35f)
                        .fillMaxHeight()
                )
                Surface(
                    modifier = Modifier
                        .weight(0.95f)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 1.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    CartCheckoutContent(
                        cartItems = cartItems,
                        subtotal = subtotal,
                        discountSyp = effectiveDiscount,
                        total = total,
                        currencyScale = currencyScale,
                        onUpdateQty = onUpdateCartQty,
                        onRemoveItem = onRemoveCartItem,
                        onClearCart = onClearCart,
                        onDiscountChange = onDiscountChange,
                        onConfirmCheckout = onCheckout
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                PosCatalogPane(
                    brands = brands,
                    models = models,
                    filter = filter,
                    cartQtyMap = cartQtyMap,
                    currencyScale = currencyScale,
                    onSearchChange = onSearchChange,
                    onSelectBrand = onSelectBrand,
                    onSelectCategory = onSelectCategory,
                    onToggleCurrencyScale = onToggleCurrencyScale,
                    onAddToCart = onAddToCart,
                    bottomExtraPadding = if (cartCount > 0) 96.dp else 16.dp,
                    modifier = Modifier.fillMaxSize()
                )

                AnimatedVisibility(
                    visible = cartCount > 0,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Surface(
                        onClick = { showCartSheet = true },
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shadowElevation = 6.dp,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pos_cart_bar")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.ShoppingCart,
                                            contentDescription = "سلة المبيعات",
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "$cartCount قطعة في الفاتورة",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Text(
                                        text = CurrencyFormatter.format(total, currencyScale),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "إتمام البيع",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCartSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showCartSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            CartCheckoutContent(
                cartItems = cartItems,
                subtotal = subtotal,
                discountSyp = effectiveDiscount,
                total = total,
                currencyScale = currencyScale,
                onUpdateQty = onUpdateCartQty,
                onRemoveItem = onRemoveCartItem,
                onClearCart = {
                    onClearCart()
                    showCartSheet = false
                },
                onDiscountChange = onDiscountChange,
                onConfirmCheckout = { customer, paid, note ->
                    onCheckout(customer, paid, note)
                    showCartSheet = false
                }
            )
        }
    }
}

@Composable
private fun PosCatalogPane(
    brands: List<BrandEntity>,
    models: List<ModelWithDetails>,
    filter: PosFilterState,
    cartQtyMap: Map<Long, Int>,
    currencyScale: CurrencyScale,
    onSearchChange: (String) -> Unit,
    onSelectBrand: (Long?) -> Unit,
    onSelectCategory: (String) -> Unit,
    onToggleCurrencyScale: () -> Unit,
    onAddToCart: (ModelWithDetails, PieceVariantEntity) -> Unit,
    bottomExtraPadding: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 14.dp)) {
        Spacer(modifier = Modifier.height(10.dp))

        // M3 Pill Search Bar + Tonal Currency Scale Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 1.dp
            ) {
                TextField(
                    value = filter.searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            "ابحث برقم الموديل أو الاسم أو اللون...",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "بحث",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (filter.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "مسح البحث")
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pos_search_input")
                )
            }

            FilledTonalButton(
                onClick = onToggleCurrencyScale,
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("pos_currency_toggle")
            ) {
                Icon(
                    imageVector = Icons.Filled.CurrencyExchange,
                    contentDescription = "تبديل صيغة الليرة",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (currencyScale == CurrencyScale.OLD_SYP) "قديمة" else "جديدة",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        BrandFilterRow(
            brands = brands,
            selectedBrandId = filter.selectedBrandId,
            onSelectBrand = onSelectBrand
        )
        Spacer(modifier = Modifier.height(4.dp))
        CategoryFilterRow(
            selectedCategory = filter.selectedCategory,
            onSelectCategory = onSelectCategory
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (models.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                OutlinedCard(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.PointOfSale,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = "لا توجد أصناف مطابقة للبحث",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = bottomExtraPadding),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(models, key = { it.model.id }) { item ->
                    PosModelCard(
                        modelDetails = item,
                        cartQtyMap = cartQtyMap,
                        currencyScale = currencyScale,
                        onAddPiece = { piece -> onAddToCart(item, piece) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PosModelCard(
    modelDetails: ModelWithDetails,
    cartQtyMap: Map<Long, Int>,
    currencyScale: CurrencyScale,
    onAddPiece: (PieceVariantEntity) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pos_model_card_${modelDetails.model.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "#${modelDetails.model.modelCode}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = modelDetails.model.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (modelDetails.model.fabricInfo.isNotBlank() || modelDetails.model.category.isNotBlank()) {
                            Text(
                                text = listOf(modelDetails.model.category, modelDetails.model.fabricInfo)
                                    .filter { it.isNotBlank() }
                                    .joinToString(" • "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                BrandBadge(brand = modelDetails.brand)
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (modelDetails.pieces.isEmpty()) {
                Text(
                    text = "لا توجد قطع مضافة لهذا الموديل بعد",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modelDetails.pieces.forEach { piece ->
                        val inCartQty = cartQtyMap[piece.id] ?: 0
                        val isOut = piece.stockQty <= 0
                        val isLow = piece.stockQty in 1..piece.minStockAlert

                        Surface(
                            modifier = Modifier
                                .testTag("pos_piece_chip_${piece.id}")
                                .clickable(enabled = !isOut) { onAddPiece(piece) },
                            shape = RoundedCornerShape(14.dp),
                            color = when {
                                isOut -> MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
                                inCartQty > 0 -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                width = if (inCartQty > 0) 1.5.dp else 1.dp,
                                color = when {
                                    isOut -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    inCartQty > 0 -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.outlineVariant
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "${piece.color} • ${piece.sizeLabel}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isOut) MaterialTheme.colorScheme.onSurfaceVariant
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = CurrencyFormatter.format(piece.priceSyp, currencyScale),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOut) MaterialTheme.colorScheme.onSurfaceVariant
                                            else MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = if (isOut) "نفذ" else "متوفر: ${piece.stockQty}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = when {
                                                isOut -> AlertRed
                                                isLow -> WarningAmber
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    }
                                }
                                if (inCartQty > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text("+$inCartQty", modifier = Modifier.padding(horizontal = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
