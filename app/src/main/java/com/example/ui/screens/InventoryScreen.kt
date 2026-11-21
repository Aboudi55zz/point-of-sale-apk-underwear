package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.BrandEntity
import com.example.data.local.ModelWithDetails
import com.example.data.local.PieceVariantEntity
import com.example.data.local.ProductModelEntity
import com.example.ui.InventoryFilterState
import com.example.ui.components.BrandFilterRow
import com.example.ui.components.CategoryFilterRow
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import com.example.util.CurrencyScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    brands: List<BrandEntity>,
    allModels: List<ModelWithDetails>,
    filteredModels: List<ModelWithDetails>,
    filter: InventoryFilterState,
    currencyScale: CurrencyScale,
    onSearchChange: (String) -> Unit,
    onSelectBrand: (Long?) -> Unit,
    onSelectCategory: (String) -> Unit,
    onToggleLowStock: () -> Unit,
    onAddBrand: (name: String, notes: String, colorHex: Long) -> Unit,
    onUpdateBrand: (BrandEntity) -> Unit,
    onDeleteBrand: (BrandEntity) -> Unit,
    onAddModel: (Long, String, String, String, String, String?, String?, Long?, Int?) -> Unit,
    onUpdateModel: (ProductModelEntity) -> Unit,
    onDeleteModel: (ProductModelEntity) -> Unit,
    onAddPiece: (Long, String, String, Long, Int, Int, String) -> Unit,
    onUpdatePiece: (PieceVariantEntity) -> Unit,
    onDeletePiece: (PieceVariantEntity) -> Unit,
    onAdjustStock: (Long, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 = Models (List-Detail), 1 = Brands, 2 = Restock / Low Stock Queue
    var selectedTab by remember { mutableIntStateOf(0) }
    var inspectedModelId by remember { mutableStateOf<Long?>(null) }

    var showBrandDialog by remember { mutableStateOf(false) }
    var editingBrand by remember { mutableStateOf<BrandEntity?>(null) }
    var deletingBrand by remember { mutableStateOf<BrandEntity?>(null) }

    var showModelDialog by remember { mutableStateOf(false) }
    var editingModel by remember { mutableStateOf<ProductModelEntity?>(null) }
    var deletingModel by remember { mutableStateOf<ProductModelEntity?>(null) }

    var pieceDialogTargetModel by remember { mutableStateOf<ModelWithDetails?>(null) }
    var editingPiece by remember { mutableStateOf<PieceVariantEntity?>(null) }
    var deletingPiece by remember { mutableStateOf<PieceVariantEntity?>(null) }

    val totalPiecesCount = remember(allModels) { allModels.sumOf { m -> m.pieces.sumOf { it.stockQty } } }
    val totalVariantsCount = remember(allModels) { allModels.sumOf { it.pieces.size } }
    val lowStockEntries = remember(allModels) {
        allModels.flatMap { m ->
            m.pieces.filter { it.stockQty <= it.minStockAlert }.map { p -> m to p }
        }.sortedBy { it.second.stockQty }
    }
    val lowStockCount = lowStockEntries.size
    val totalInventoryValueSyp = remember(allModels) {
        allModels.sumOf { m -> m.pieces.sumOf { it.priceSyp * it.stockQty } }
    }
    val hasActiveFilters = filter.searchQuery.isNotBlank() ||
        filter.selectedBrandId != null ||
        filter.selectedCategory != "الكل" ||
        filter.lowStockOnly

    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCanonicalTwoPane = maxWidth >= 840.dp
        val isWideBrands = maxWidth >= 700.dp

        // Auto-select first model on wide List-Detail screen when nothing valid is selected
        LaunchedEffect(isCanonicalTwoPane, filteredModels) {
            if (isCanonicalTwoPane) {
                if (inspectedModelId == null || filteredModels.none { it.model.id == inspectedModelId }) {
                    inspectedModelId = filteredModels.firstOrNull()?.model?.id
                }
            }
        }

        val inspectedModel = remember(allModels, inspectedModelId) {
            allModels.firstOrNull { it.model.id == inspectedModelId }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Google Material Design 3 PrimaryTabRow
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    PrimaryTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == 0) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2,
                                    contentDescription = null
                                )
                            },
                            text = {
                                Text(
                                    text = "الموديلات (${allModels.size})",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.testTag("inv_tab_models")
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == 1) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                                    contentDescription = null
                                )
                            },
                            text = {
                                Text(
                                    text = "الماركات (${brands.size})",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.testTag("inv_tab_brands")
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (lowStockCount > 0) {
                                            Badge(
                                                containerColor = WarningAmber,
                                                contentColor = Color.White
                                            ) {
                                                Text("$lowStockCount")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (selectedTab == 2) Icons.Filled.WarningAmber else Icons.Outlined.WarningAmber,
                                        contentDescription = null
                                    )
                                }
                            },
                            text = {
                                Text(
                                    text = "النواقص",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.testTag("inv_tab_restock")
                        )
                    }
                }

                when (selectedTab) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 1160.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(horizontal = 14.dp)
                        ) {
                            Spacer(modifier = Modifier.height(10.dp))

                            // M3 Pill Search Bar + Low-Stock Filter Chip
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                                    tonalElevation = 1.dp
                                ) {
                                    TextField(
                                        value = filter.searchQuery,
                                        onValueChange = onSearchChange,
                                        placeholder = {
                                            Text(
                                                "ابحث برقم الموديل، الاسم، اللون...",
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
                                                    Icon(Icons.Filled.Clear, contentDescription = "مسح")
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
                                            .testTag("inv_search_input")
                                    )
                                }

                                FilterChip(
                                    selected = filter.lowStockOnly,
                                    onClick = onToggleLowStock,
                                    label = {
                                        Text(
                                            text = if (lowStockCount > 0) "نواقص ($lowStockCount)" else "نواقص",
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Filled.WarningAmber,
                                            contentDescription = null,
                                            tint = if (filter.lowStockOnly || lowStockCount > 0) WarningAmber
                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = WarningAmberContainer,
                                        selectedLabelColor = WarningAmber
                                    ),
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("inv_low_stock_chip")
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            BrandFilterRow(
                                brands = brands,
                                selectedBrandId = filter.selectedBrandId,
                                onSelectBrand = onSelectBrand
                            )
                            CategoryFilterRow(
                                selectedCategory = filter.selectedCategory,
                                onSelectCategory = onSelectCategory
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            if (isCanonicalTwoPane) {
                                // Canonical M3 List-Detail Two-Pane Layout
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    LazyColumn(
                                        contentPadding = PaddingValues(bottom = 96.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier
                                            .weight(0.45f)
                                            .fillMaxHeight()
                                    ) {
                                        item(key = "kpi_strip_wide") {
                                            InventoryM3KpiStrip(
                                                modelsCount = allModels.size,
                                                variantsCount = totalVariantsCount,
                                                totalPiecesCount = totalPiecesCount,
                                                lowStockCount = lowStockCount,
                                                totalInventoryValueSyp = totalInventoryValueSyp,
                                                currencyScale = currencyScale,
                                                onRestockClick = { selectedTab = 2 }
                                            )
                                        }
                                        item(key = "results_wide") {
                                            ResultsHeaderBar(
                                                shownCount = filteredModels.size,
                                                totalCount = allModels.size,
                                                hasActiveFilters = hasActiveFilters,
                                                onClearFilters = {
                                                    onSearchChange("")
                                                    onSelectBrand(null)
                                                    onSelectCategory("الكل")
                                                    if (filter.lowStockOnly) onToggleLowStock()
                                                }
                                            )
                                        }
                                        if (filteredModels.isEmpty()) {
                                            item(key = "empty_wide") {
                                                InventoryM3EmptyState(
                                                    title = if (hasActiveFilters) "لا توجد نتائج مطابقة" else "المخزون فارغ",
                                                    subtitle = if (hasActiveFilters) "جرّب مسح الفلاتر أو البحث برقم موديل آخر."
                                                    else "ابدأ بإضافة أول موديل وقطعة لمتجرك.",
                                                    primaryActionLabel = "إضافة موديل",
                                                    onPrimaryAction = {
                                                        editingModel = null
                                                        showModelDialog = true
                                                    },
                                                    secondaryActionLabel = if (hasActiveFilters) "مسح الفلاتر" else null,
                                                    onSecondaryAction = if (hasActiveFilters) {
                                                        {
                                                            onSearchChange("")
                                                            onSelectBrand(null)
                                                            onSelectCategory("الكل")
                                                            if (filter.lowStockOnly) onToggleLowStock()
                                                        }
                                                    } else null
                                                )
                                            }
                                        } else {
                                            items(filteredModels, key = { it.model.id }) { item ->
                                                InventoryM3ModelCard(
                                                    modelDetails = item,
                                                    currencyScale = currencyScale,
                                                    isSelected = inspectedModelId == item.model.id,
                                                    onClick = { inspectedModelId = item.model.id },
                                                    onQuickAddPiece = {
                                                        inspectedModelId = item.model.id
                                                        editingPiece = null
                                                        pieceDialogTargetModel = item
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // Detail Pane
                                    ElevatedCard(
                                        shape = RoundedCornerShape(24.dp),
                                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                                        modifier = Modifier
                                            .weight(0.55f)
                                            .fillMaxHeight()
                                            .padding(bottom = 14.dp)
                                    ) {
                                        val active = inspectedModel
                                        if (active != null) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .verticalScroll(rememberScrollState())
                                                    .padding(18.dp)
                                            ) {
                                                ModelDetailInspector(
                                                    modelDetails = active,
                                                    currencyScale = currencyScale,
                                                    onEditModel = {
                                                        editingModel = active.model
                                                        showModelDialog = true
                                                    },
                                                    onDeleteModel = { deletingModel = active.model },
                                                    onAddPiece = {
                                                        editingPiece = null
                                                        pieceDialogTargetModel = active
                                                    },
                                                    onEditPiece = { piece ->
                                                        editingPiece = piece
                                                        pieceDialogTargetModel = active
                                                    },
                                                    onDeletePiece = { piece -> deletingPiece = piece },
                                                    onAdjustStock = onAdjustStock
                                                )
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(24.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                InventoryM3EmptyState(
                                                    title = "اختر موديلاً لعرض قطعه",
                                                    subtitle = "اضغط على أي موديل من القائمة لتعديل قطعه ومقاساته وأرصدته."
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Phone Compact Layout: List + M3 ModalBottomSheet for Piece Management
                                LazyColumn(
                                    contentPadding = PaddingValues(bottom = 96.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    item(key = "kpi_strip_phone") {
                                        InventoryM3KpiStrip(
                                            modelsCount = allModels.size,
                                            variantsCount = totalVariantsCount,
                                            totalPiecesCount = totalPiecesCount,
                                            lowStockCount = lowStockCount,
                                            totalInventoryValueSyp = totalInventoryValueSyp,
                                            currencyScale = currencyScale,
                                            onRestockClick = { selectedTab = 2 }
                                        )
                                    }
                                    item(key = "results_phone") {
                                        ResultsHeaderBar(
                                            shownCount = filteredModels.size,
                                            totalCount = allModels.size,
                                            hasActiveFilters = hasActiveFilters,
                                            onClearFilters = {
                                                onSearchChange("")
                                                onSelectBrand(null)
                                                onSelectCategory("الكل")
                                                if (filter.lowStockOnly) onToggleLowStock()
                                            }
                                        )
                                    }
                                    if (filteredModels.isEmpty()) {
                                        item(key = "empty_phone") {
                                            InventoryM3EmptyState(
                                                title = if (hasActiveFilters) "لا توجد نتائج مطابقة" else "المخزون فارغ",
                                                subtitle = if (hasActiveFilters) "جرّب مسح الفلاتر أو البحث برقم موديل آخر."
                                                else "ابدأ بإضافة أول موديل وقطعة لمتجرك.",
                                                primaryActionLabel = "إضافة موديل",
                                                onPrimaryAction = {
                                                    editingModel = null
                                                    showModelDialog = true
                                                },
                                                secondaryActionLabel = if (hasActiveFilters) "مسح الفلاتر" else null,
                                                onSecondaryAction = if (hasActiveFilters) {
                                                    {
                                                        onSearchChange("")
                                                        onSelectBrand(null)
                                                        onSelectCategory("الكل")
                                                        if (filter.lowStockOnly) onToggleLowStock()
                                                    }
                                                } else null
                                            )
                                        }
                                    } else {
                                        items(filteredModels, key = { it.model.id }) { item ->
                                            InventoryM3ModelCard(
                                                modelDetails = item,
                                                currencyScale = currencyScale,
                                                isSelected = inspectedModelId == item.model.id,
                                                onClick = { inspectedModelId = item.model.id },
                                                onQuickAddPiece = {
                                                    editingPiece = null
                                                    pieceDialogTargetModel = item
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Brands Tab
                        LazyColumn(
                            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 980.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            val brandRows = if (isWideBrands) brands.chunked(2) else brands.map { listOf(it) }
                            items(brandRows, key = { row -> row.joinToString("_") { it.id.toString() } }) { rowBrands ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowBrands.forEach { brand ->
                                        val brandModels = allModels.filter { it.brand.id == brand.id }
                                        BrandM3Card(
                                            brand = brand,
                                            modelsCount = brandModels.size,
                                            variantsCount = brandModels.sumOf { it.pieces.size },
                                            totalStockQty = brandModels.sumOf { m -> m.pieces.sumOf { it.stockQty } },
                                            lowStockVariants = brandModels.sumOf { m ->
                                                m.pieces.count { it.stockQty <= it.minStockAlert }
                                            },
                                            onBrowseBrand = {
                                                onSelectBrand(brand.id)
                                                selectedTab = 0
                                            },
                                            onEdit = {
                                                editingBrand = brand
                                                showBrandDialog = true
                                            },
                                            onDelete = { deletingBrand = brand },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (isWideBrands && rowBrands.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // Restock / Low Stock Queue Tab
                        LazyColumn(
                            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 900.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            if (lowStockEntries.isEmpty()) {
                                item(key = "restock_empty") {
                                    InventoryM3EmptyState(
                                        icon = Icons.Filled.CheckCircleOutline,
                                        title = "جميع القطع متوفرة بكميات ممتازة",
                                        subtitle = "لا توجد أي قطعة دون حد التنبيه حالياً.",
                                        primaryActionLabel = "العودة للموديلات",
                                        onPrimaryAction = { selectedTab = 0 }
                                    )
                                }
                            } else {
                                item(key = "restock_header") {
                                    Text(
                                        text = "قائمة النواقص والتزويد السريع (${lowStockEntries.size} قطعة)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                                items(lowStockEntries, key = { it.second.id }) { (modelDetails, piece) ->
                                    RestockQueueCard(
                                        modelDetails = modelDetails,
                                        piece = piece,
                                        currencyScale = currencyScale,
                                        onAdjustStock = { delta -> onAdjustStock(piece.id, delta) },
                                        onEditPiece = {
                                            editingPiece = piece
                                            pieceDialogTargetModel = modelDetails
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (selectedTab != 2) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (selectedTab == 1) {
                            editingBrand = null
                            showBrandDialog = true
                        } else {
                            editingModel = null
                            showModelDialog = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.Storefront else Icons.Filled.Add,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = if (selectedTab == 1) "إضافة ماركة" else "إضافة موديل",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .testTag("inventory_add_fab")
                )
            }
        }

        // Phone Detail ModalBottomSheet
        if (!isCanonicalTwoPane && selectedTab == 0 && inspectedModel != null) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { inspectedModelId = null },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, bottom = 28.dp)
                ) {
                    ModelDetailInspector(
                        modelDetails = inspectedModel,
                        currencyScale = currencyScale,
                        onEditModel = {
                            editingModel = inspectedModel.model
                            showModelDialog = true
                        },
                        onDeleteModel = { deletingModel = inspectedModel.model },
                        onAddPiece = {
                            editingPiece = null
                            pieceDialogTargetModel = inspectedModel
                        },
                        onEditPiece = { piece ->
                            editingPiece = piece
                            pieceDialogTargetModel = inspectedModel
                        },
                        onDeletePiece = { piece -> deletingPiece = piece },
                        onAdjustStock = onAdjustStock
                    )
                }
            }
        }
    }

    // CRUD Dialogs
    if (showBrandDialog) {
        BrandEditorDialog(
            initialBrand = editingBrand,
            onDismiss = { showBrandDialog = false },
            onSave = { name, notes, colorHex ->
                val existing = editingBrand
                if (existing == null) {
                    onAddBrand(name, notes, colorHex)
                } else {
                    onUpdateBrand(existing.copy(name = name, notes = notes, accentColorHex = colorHex))
                }
                showBrandDialog = false
            }
        )
    }

    deletingBrand?.let { brand ->
        ConfirmDeleteDialog(
            title = "حذف الماركة",
            message = "هل تريد حذف ماركة \"${brand.name}\"؟ سيتم حذف جميع الموديلات والقطع التابعة لها.",
            onDismiss = { deletingBrand = null },
            onConfirm = {
                onDeleteBrand(brand)
                deletingBrand = null
            }
        )
    }

    if (showModelDialog) {
        ModelEditorDialog(
            brands = brands,
            initialModel = editingModel,
            defaultBrandId = filter.selectedBrandId,
            currencyScale = currencyScale,
            onDismiss = { showModelDialog = false },
            onSave = { brandId, code, name, cat, fabric, c, s, price, stock ->
                val existing = editingModel
                if (existing == null) {
                    onAddModel(brandId, code, name, cat, fabric, c, s, price, stock)
                } else {
                    onUpdateModel(
                        existing.copy(
                            brandId = brandId,
                            modelCode = code,
                            name = name,
                            category = cat,
                            fabricInfo = fabric
                        )
                    )
                }
                showModelDialog = false
            }
        )
    }

    deletingModel?.let { model ->
        ConfirmDeleteDialog(
            title = "حذف الموديل",
            message = "هل تريد حذف الموديل #${model.modelCode} (${model.name}) وجميع القطع التابعة له؟",
            onDismiss = { deletingModel = null },
            onConfirm = {
                if (inspectedModelId == model.id) inspectedModelId = null
                onDeleteModel(model)
                deletingModel = null
            }
        )
    }

    pieceDialogTargetModel?.let { targetModel ->
        PieceEditorDialog(
            modelLabel = "${targetModel.brand.name} • #${targetModel.model.modelCode} ${targetModel.model.name}",
            initialPiece = editingPiece,
            currencyScale = currencyScale,
            onDismiss = {
                pieceDialogTargetModel = null
                editingPiece = null
            },
            onSave = { color, size, priceSyp, stockQty, minAlert, sku ->
                val existing = editingPiece
                if (existing == null) {
                    onAddPiece(targetModel.model.id, color, size, priceSyp, stockQty, minAlert, sku)
                } else {
                    onUpdatePiece(
                        existing.copy(
                            color = color,
                            sizeLabel = size,
                            priceSyp = priceSyp,
                            stockQty = stockQty,
                            minStockAlert = minAlert,
                            skuBarcode = sku
                        )
                    )
                }
                pieceDialogTargetModel = null
                editingPiece = null
            }
        )
    }

    deletingPiece?.let { piece ->
        ConfirmDeleteDialog(
            title = "حذف القطعة",
            message = "هل تريد حذف قطعة (${piece.color} • ${piece.sizeLabel}) من المخزون؟",
            onDismiss = { deletingPiece = null },
            onConfirm = {
                onDeletePiece(piece)
                deletingPiece = null
            }
        )
    }
}

@Composable
private fun ResultsHeaderBar(
    shownCount: Int,
    totalCount: Int,
    hasActiveFilters: Boolean,
    onClearFilters: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "عرض $shownCount من أصل $totalCount موديل (اضغط لإدارة القطع)",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (hasActiveFilters) {
            TextButton(onClick = onClearFilters) {
                Icon(Icons.Filled.FilterAltOff, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("مسح الفلاتر")
            }
        }
    }
}
