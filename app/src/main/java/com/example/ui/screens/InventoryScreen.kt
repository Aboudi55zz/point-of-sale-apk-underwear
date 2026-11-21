package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    // 0 = Models & Pieces, 1 = Brands Management
    var selectedTab by remember { mutableIntStateOf(0) }

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
    val lowStockCount = remember(allModels) {
        allModels.sumOf { m -> m.pieces.count { it.stockQty <= it.minStockAlert } }
    }
    val totalInventoryValueSyp = remember(allModels) {
        allModels.sumOf { m -> m.pieces.sumOf { it.priceSyp * it.stockQty } }
    }
    val hasActiveFilters = filter.searchQuery.isNotBlank() ||
        filter.selectedBrandId != null ||
        filter.selectedCategory != "الكل" ||
        filter.lowStockOnly

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 760.dp

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 980.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 14.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        icon = {
                            SegmentedButtonDefaults.Icon(active = selectedTab == 0) {
                                Icon(Icons.Filled.Inventory2, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        },
                        modifier = Modifier.testTag("inv_tab_models")
                    ) {
                        Text("الموديلات (${allModels.size})")
                    }
                    SegmentedButton(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        icon = {
                            SegmentedButtonDefaults.Icon(active = selectedTab == 1) {
                                Icon(Icons.Filled.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        },
                        modifier = Modifier.testTag("inv_tab_brands")
                    ) {
                        Text("الماركات (${brands.size})")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (selectedTab == 0) {
                    // Search + Filters Docked Panel
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = filter.searchQuery,
                                    onValueChange = onSearchChange,
                                    placeholder = {
                                        Text(
                                            "بحث برقم الموديل، الاسم، اللون، الباركود...",
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
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.background,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("inv_search_input")
                                )

                                FilterChip(
                                    selected = filter.lowStockOnly,
                                    onClick = onToggleLowStock,
                                    label = {
                                        Text(
                                            text = if (lowStockCount > 0) "نواقص ($lowStockCount)" else "نواقص",
                                            fontWeight = FontWeight.SemiBold
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
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = WarningAmberContainer,
                                        selectedLabelColor = WarningAmber
                                    ),
                                    modifier = Modifier.testTag("inv_low_stock_chip")
                                )
                            }

                            BrandFilterRow(
                                brands = brands,
                                selectedBrandId = filter.selectedBrandId,
                                onSelectBrand = onSelectBrand
                            )
                            CategoryFilterRow(
                                selectedCategory = filter.selectedCategory,
                                onSelectCategory = onSelectCategory
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item(key = "inventory_hero_summary") {
                            InventoryHeroSummaryCard(
                                brandsCount = brands.size,
                                modelsCount = allModels.size,
                                totalPiecesCount = totalPiecesCount,
                                lowStockCount = lowStockCount,
                                totalInventoryValueSyp = totalInventoryValueSyp,
                                currencyScale = currencyScale,
                                lowStockFilterActive = filter.lowStockOnly,
                                onBrandsClick = { selectedTab = 1 },
                                onLowStockClick = onToggleLowStock
                            )
                        }

                        item(key = "inventory_results_bar") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "عرض ${filteredModels.size} من أصل ${allModels.size} موديل",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (hasActiveFilters) {
                                    TextButton(
                                        onClick = {
                                            onSearchChange("")
                                            onSelectBrand(null)
                                            onSelectCategory("الكل")
                                            if (filter.lowStockOnly) onToggleLowStock()
                                        }
                                    ) {
                                        Text("مسح الفلاتر")
                                    }
                                }
                            }
                        }

                        if (filteredModels.isEmpty()) {
                            item(key = "inventory_empty_state") {
                                InventoryEmptyState(
                                    hasActiveFilters = hasActiveFilters,
                                    onClearFilters = {
                                        onSearchChange("")
                                        onSelectBrand(null)
                                        onSelectCategory("الكل")
                                        if (filter.lowStockOnly) onToggleLowStock()
                                    },
                                    onAddModel = {
                                        editingModel = null
                                        showModelDialog = true
                                    }
                                )
                            }
                        } else if (isWide) {
                            val pairs = filteredModels.chunked(2)
                            items(pairs, key = { pair -> pair.joinToString("_") { it.model.id.toString() } }) { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    pair.forEach { item ->
                                        InventoryModelCard(
                                            modelDetails = item,
                                            currencyScale = currencyScale,
                                            onEditModel = {
                                                editingModel = item.model
                                                showModelDialog = true
                                            },
                                            onDeleteModel = { deletingModel = item.model },
                                            onAddPiece = {
                                                editingPiece = null
                                                pieceDialogTargetModel = item
                                            },
                                            onEditPiece = { piece ->
                                                editingPiece = piece
                                                pieceDialogTargetModel = item
                                            },
                                            onDeletePiece = { piece -> deletingPiece = piece },
                                            onAdjustStock = onAdjustStock,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        } else {
                            items(filteredModels, key = { it.model.id }) { item ->
                                InventoryModelCard(
                                    modelDetails = item,
                                    currencyScale = currencyScale,
                                    onEditModel = {
                                        editingModel = item.model
                                        showModelDialog = true
                                    },
                                    onDeleteModel = { deletingModel = item.model },
                                    onAddPiece = {
                                        editingPiece = null
                                        pieceDialogTargetModel = item
                                    },
                                    onEditPiece = { piece ->
                                        editingPiece = piece
                                        pieceDialogTargetModel = item
                                    },
                                    onDeletePiece = { piece -> deletingPiece = piece },
                                    onAdjustStock = onAdjustStock
                                )
                            }
                        }
                    }
                } else {
                    // Brands Management Tab
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val brandRows = if (isWide) brands.chunked(2) else brands.map { listOf(it) }
                        items(brandRows, key = { row -> row.joinToString("_") { it.id.toString() } }) { rowBrands ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowBrands.forEach { brand ->
                                    val brandModels = allModels.filter { it.brand.id == brand.id }
                                    val variantsCount = brandModels.sumOf { it.pieces.size }
                                    val brandStockQty = brandModels.sumOf { m -> m.pieces.sumOf { it.stockQty } }
                                    val brandLowStock = brandModels.sumOf { m ->
                                        m.pieces.count { it.stockQty <= it.minStockAlert }
                                    }
                                    BrandManagementCard(
                                        brand = brand,
                                        modelsCount = brandModels.size,
                                        variantsCount = variantsCount,
                                        totalStockQty = brandStockQty,
                                        lowStockVariants = brandLowStock,
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
                                if (isWide && rowBrands.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

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

    // Dialogs
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
