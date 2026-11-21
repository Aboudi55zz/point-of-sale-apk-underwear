package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.components.BrandBadge
import com.example.ui.components.BrandFilterRow
import com.example.ui.components.CategoryFilterRow
import com.example.ui.theme.AlertRed
import com.example.ui.theme.WarningAmber
import com.example.util.CurrencyFormatter
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

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
            Spacer(modifier = Modifier.height(8.dp))

            // KPI Summary Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InventoryStatCard(
                    label = "الماركات",
                    value = "${brands.size}",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                    highlightColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                InventoryStatCard(
                    label = "الموديلات",
                    value = "${allModels.size}",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f),
                    highlightColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
                InventoryStatCard(
                    label = "القطع",
                    value = "$totalPiecesCount",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                    highlightColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
                InventoryStatCard(
                    label = "نواقص",
                    value = "$lowStockCount",
                    modifier = Modifier.weight(1f),
                    containerColor = if (lowStockCount > 0) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    highlightColor = if (lowStockCount > 0) WarningAmber else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    modifier = Modifier.testTag("inv_tab_models")
                ) {
                    Text("الموديلات والقطع")
                }
                SegmentedButton(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    modifier = Modifier.testTag("inv_tab_brands")
                ) {
                    Text("إدارة الماركات")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedTab == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = filter.searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("بحث في الموديلات والقطع...", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "بحث") },
                        trailingIcon = {
                            if (filter.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "مسح")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).testTag("inv_search_input")
                    )
                    FilterChip(
                        selected = filter.lowStockOnly,
                        onClick = onToggleLowStock,
                        label = { Text("نواقص") },
                        leadingIcon = {
                            Icon(Icons.Filled.WarningAmber, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.testTag("inv_low_stock_chip")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                BrandFilterRow(brands = brands, selectedBrandId = filter.selectedBrandId, onSelectBrand = onSelectBrand)
                Spacer(modifier = Modifier.height(4.dp))
                CategoryFilterRow(selectedCategory = filter.selectedCategory, onSelectCategory = onSelectCategory)
                Spacer(modifier = Modifier.height(8.dp))

                if (filteredModels.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.Inventory2,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("لا توجد موديلات مطابقة", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
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
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(brands, key = { it.id }) { brand ->
                        val brandModels = allModels.filter { it.brand.id == brand.id }
                        val brandPiecesCount = brandModels.sumOf { it.pieces.size }
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("brand_card_${brand.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    BrandBadge(brand = brand)
                                    if (brand.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = brand.notes,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "${brandModels.size} موديل • $brandPiecesCount قطعة",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            editingBrand = brand
                                            showBrandDialog = true
                                        },
                                        modifier = Modifier.testTag("edit_brand_${brand.id}")
                                    ) {
                                        Icon(Icons.Filled.Edit, contentDescription = "تعديل الماركة")
                                    }
                                    IconButton(
                                        onClick = { deletingBrand = brand },
                                        modifier = Modifier.testTag("delete_brand_${brand.id}")
                                    ) {
                                        Icon(
                                            Icons.Filled.DeleteOutline,
                                            contentDescription = "حذف الماركة",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (selectedTab == 1) {
                    editingBrand = null
                    showBrandDialog = true
                } else {
                    editingModel = null
                    showModelDialog = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .testTag("inventory_add_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (selectedTab == 1) Icons.Filled.Storefront else Icons.Filled.Add,
                    contentDescription = null
                )
                Text(if (selectedTab == 1) "إضافة ماركة" else "إضافة موديل")
            }
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

@Composable
private fun InventoryStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    highlightColor: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(1.dp, highlightColor.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = highlightColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun InventoryModelCard(
    modelDetails: ModelWithDetails,
    currencyScale: CurrencyScale,
    onEditModel: () -> Unit,
    onDeleteModel: () -> Unit,
    onAddPiece: () -> Unit,
    onEditPiece: (PieceVariantEntity) -> Unit,
    onDeletePiece: (PieceVariantEntity) -> Unit,
    onAdjustStock: (Long, Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inv_model_card_${modelDetails.model.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BrandBadge(brand = modelDetails.brand)
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "#${modelDetails.model.modelCode}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = modelDetails.model.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = listOf(modelDetails.model.category, modelDetails.model.fabricInfo)
                            .filter { it.isNotBlank() }
                            .joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(
                        onClick = onEditModel,
                        modifier = Modifier.testTag("edit_model_${modelDetails.model.id}")
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "تعديل الموديل")
                    }
                    IconButton(
                        onClick = onDeleteModel,
                        modifier = Modifier.testTag("delete_model_${modelDetails.model.id}")
                    ) {
                        Icon(
                            Icons.Filled.DeleteOutline,
                            contentDescription = "حذف الموديل",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            Spacer(modifier = Modifier.height(8.dp))

            modelDetails.pieces.forEach { piece ->
                val isOut = piece.stockQty <= 0
                val isLow = piece.stockQty in 1..piece.minStockAlert

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .testTag("inv_piece_row_${piece.id}"),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${piece.color} • ${piece.sizeLabel}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = CurrencyFormatter.format(piece.priceSyp, currencyScale),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            FilledTonalIconButton(
                                onClick = { onAdjustStock(piece.id, -1) },
                                modifier = Modifier.size(34.dp).testTag("stock_dec_${piece.id}")
                            ) {
                                Icon(Icons.Filled.Remove, contentDescription = "إنقاص المخزون", modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "${piece.stockQty}",
                                style = MaterialTheme.typography.titleSmall,
                                color = when {
                                    isOut -> AlertRed
                                    isLow -> WarningAmber
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            FilledTonalIconButton(
                                onClick = { onAdjustStock(piece.id, 1) },
                                modifier = Modifier.size(34.dp).testTag("stock_inc_${piece.id}")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "زيادة المخزون", modifier = Modifier.size(16.dp))
                            }
                            OutlinedButton(
                                onClick = { onAdjustStock(piece.id, 12) },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("+12", style = MaterialTheme.typography.labelSmall)
                            }
                            IconButton(
                                onClick = { onEditPiece(piece) },
                                modifier = Modifier.size(34.dp).testTag("edit_piece_${piece.id}")
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "تعديل القطعة", modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { onDeletePiece(piece) },
                                modifier = Modifier.size(34.dp).testTag("delete_piece_${piece.id}")
                            ) {
                                Icon(
                                    Icons.Filled.DeleteOutline,
                                    contentDescription = "حذف القطعة",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            FilledTonalButton(
                onClick = onAddPiece,
                modifier = Modifier.fillMaxWidth().testTag("add_piece_to_model_${modelDetails.model.id}"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة قطعة (لون / قياس / سعر)")
            }
        }
    }
}
