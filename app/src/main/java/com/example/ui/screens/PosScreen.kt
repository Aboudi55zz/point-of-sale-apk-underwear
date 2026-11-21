package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.ui.theme.SuccessGreen
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
        val isWide = maxWidth >= 760.dp

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
                    tonalElevation = 2.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
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
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pos_cart_bar")
                            .clickable { showCartSheet = true },
                        shape = RoundedCornerShape(18.dp),
                        color = Color.Transparent,
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ShoppingCart,
                                        contentDescription = "سلة المبيعات",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Column {
                                    Text(
                                        text = "$cartCount قطعة في الفاتورة",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        text = CurrencyFormatter.format(total, currencyScale),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "إتمام البيع",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
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
            sheetState = sheetState
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
    bottomExtraPadding: androidx.compose.ui.unit.Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 14.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = filter.searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("ابحث برقم الموديل أو الاسم أو اللون...", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "بحث") },
                trailingIcon = {
                    if (filter.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "مسح البحث")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("pos_search_input")
            )
            Surface(
                modifier = Modifier
                    .testTag("pos_currency_toggle")
                    .clickable(onClick = onToggleCurrencyScale),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CurrencyExchange,
                        contentDescription = "تبديل صيغة الليرة",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (currencyScale == CurrencyScale.OLD_SYP) "قديمة" else "جديدة",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.PointOfSale,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "لا توجد أصناف مطابقة للبحث",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pos_model_card_${modelDetails.model.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "#${modelDetails.model.modelCode}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = modelDetails.model.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                BrandBadge(brand = modelDetails.brand)
            }

            if (modelDetails.model.fabricInfo.isNotBlank() || modelDetails.model.category.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = listOf(modelDetails.model.category, modelDetails.model.fabricInfo)
                        .filter { it.isNotBlank() }
                        .joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isOut -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                inCartQty > 0 -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            },
                            border = BorderStroke(
                                width = if (inCartQty > 0) 1.5.dp else 1.dp,
                                color = when {
                                    isOut -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    inCartQty > 0 -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CartCheckoutContent(
    cartItems: List<CartItem>,
    subtotal: Long,
    discountSyp: Long,
    total: Long,
    currencyScale: CurrencyScale,
    onUpdateQty: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onClearCart: () -> Unit,
    onDiscountChange: (Long) -> Unit,
    onConfirmCheckout: (customerName: String, paidSyp: Long?, note: String) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var discountInput by remember(discountSyp, currencyScale) {
        mutableStateOf(if (discountSyp == 0L) "" else CurrencyFormatter.toInputString(discountSyp, currencyScale))
    }
    var paidInput by remember(total, currencyScale) {
        mutableStateOf(CurrencyFormatter.toInputString(total, currencyScale))
    }

    val paidOldSyp = CurrencyFormatter.parseInputToOldSyp(paidInput, currencyScale) ?: total
    val changeSyp = (paidOldSyp - total).coerceAtLeast(0L)
    val quickCashAmountsOld = remember(total) {
        listOf(50_000L, 100_000L, 200_000L, 500_000L)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "فاتورة البيع الحالية (${cartItems.sumOf { it.quantity }})",
                style = MaterialTheme.typography.titleLarge
            )
            if (cartItems.isNotEmpty()) {
                TextButton(
                    onClick = onClearCart,
                    modifier = Modifier.testTag("clear_cart_button")
                ) {
                    Text("إفراغ السلة", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("السلة فارغة", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "اضغط على أي قطعة لإضافتها إلى الفاتورة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            cartItems.forEach { item ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${item.modelName} (#${item.modelCode})",
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${item.brandName} • ${item.color} • ${item.sizeLabel}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyFormatter.format(item.lineTotalSyp, currencyScale),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            FilledTonalIconButton(
                                onClick = { onUpdateQty(item.pieceId, -1) },
                                modifier = Modifier.size(36.dp).testTag("cart_dec_${item.pieceId}")
                            ) {
                                Icon(Icons.Filled.Remove, contentDescription = "إنقاص الكمية", modifier = Modifier.size(18.dp))
                            }
                            Text(
                                text = "${item.quantity}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            FilledTonalIconButton(
                                onClick = { onUpdateQty(item.pieceId, 1) },
                                modifier = Modifier.size(36.dp).testTag("cart_inc_${item.pieceId}")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "زيادة الكمية", modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { onRemoveItem(item.pieceId) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Filled.DeleteOutline,
                                    contentDescription = "حذف من السلة",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = discountInput,
                    onValueChange = { raw ->
                        discountInput = raw
                        val parsed = CurrencyFormatter.parseInputToOldSyp(raw, currencyScale) ?: 0L
                        onDiscountChange(parsed)
                    },
                    label = { Text("الحسم (${currencyScale.shortUnit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("checkout_discount_input")
                )
                OutlinedTextField(
                    value = paidInput,
                    onValueChange = { paidInput = it },
                    label = { Text("المبلغ المقبوض") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("checkout_paid_input")
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = { paidInput = CurrencyFormatter.toInputString(total, currencyScale) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("المبلغ تماماً", style = MaterialTheme.typography.labelMedium)
                }
                quickCashAmountsOld.forEach { amt ->
                    OutlinedButton(
                        onClick = { paidInput = CurrencyFormatter.toInputString(amt, currencyScale) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(CurrencyFormatter.format(amt, currencyScale), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("اسم الزبون (اختياري)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("ملاحظة") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الإجمالي النهائي", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = CurrencyFormatter.format(total, currencyScale),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (changeSyp > 0L) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("الباقي للزبون", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = CurrencyFormatter.format(changeSyp, currencyScale),
                                style = MaterialTheme.typography.titleMedium,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { onConfirmCheckout(customerName, paidOldSyp, note) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_checkout_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "تأكيد البيع وإصدار الفاتورة",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
