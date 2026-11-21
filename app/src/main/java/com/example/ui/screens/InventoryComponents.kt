package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.BrandEntity
import com.example.data.local.ModelWithDetails
import com.example.data.local.PieceVariantEntity
import com.example.ui.components.BrandBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import com.example.util.CurrencyFormatter
import com.example.util.CurrencyScale

@Composable
fun InventoryM3KpiStrip(
    modelsCount: Int,
    variantsCount: Int,
    totalPiecesCount: Int,
    lowStockCount: Int,
    totalInventoryValueSyp: Long,
    currencyScale: CurrencyScale,
    onRestockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Primary tonal KPI: Total Value
        Surface(
            modifier = Modifier.weight(1.45f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "قيمة المخزون",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
                Text(
                    text = CurrencyFormatter.format(totalInventoryValueSyp, currencyScale),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$totalPiecesCount قطعة فعلية",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                    maxLines = 1
                )
            }
        }

        // Secondary tonal KPI: Models & Variants
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Category,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "الموديلات",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
                Text(
                    text = "$modelsCount",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "$variantsCount صنف فرعي",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.78f),
                    maxLines = 1
                )
            }
        }

        // Alert KPI: Low Stock (tappable)
        val hasAlert = lowStockCount > 0
        Surface(
            modifier = Modifier
                .weight(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onRestockClick),
            shape = RoundedCornerShape(16.dp),
            color = if (hasAlert) WarningAmberContainer else SuccessGreenContainer,
            contentColor = if (hasAlert) WarningAmber else SuccessGreen
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (hasAlert) Icons.Filled.WarningAmber else Icons.Filled.CheckCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "النواقص",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                Text(
                    text = "$lowStockCount",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if (hasAlert) "عرض الكل" else "ممتاز",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InventoryM3ModelCard(
    modelDetails: ModelWithDetails,
    currencyScale: CurrencyScale,
    isSelected: Boolean,
    onClick: () -> Unit,
    onQuickAddPiece: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brandAccent = Color(modelDetails.brand.accentColorHex)
    val totalStock = remember(modelDetails.pieces) { modelDetails.pieces.sumOf { it.stockQty } }
    val lowCount = remember(modelDetails.pieces) {
        modelDetails.pieces.count { it.stockQty <= it.minStockAlert }
    }
    val outCount = remember(modelDetails.pieces) {
        modelDetails.pieces.count { it.stockQty <= 0 }
    }
    val minPrice = remember(modelDetails.pieces) { modelDetails.pieces.minOfOrNull { it.priceSyp } }
    val maxPrice = remember(modelDetails.pieces) { modelDetails.pieces.maxOfOrNull { it.priceSyp } }

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "m3_model_card_bg"
    )

    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("inv_model_card_${modelDetails.model.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isSelected) 3.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .then(
                    if (isSelected) {
                        Modifier.border(
                            1.5.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(18.dp)
                        )
                    } else Modifier
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // M3 ListItem Top Row: Leading Avatar + Title/Meta + Trailing Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading M3 Monogram / Model Code Container
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = brandAccent.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, brandAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = modelDetails.model.modelCode,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = brandAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = modelDetails.model.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = modelDetails.brand.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = brandAccent
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = buildString {
                                append(modelDetails.model.category)
                                if (modelDetails.model.fabricInfo.isNotBlank()) {
                                    append(" • ")
                                    append(modelDetails.model.fabricInfo)
                                }
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Trailing Price & Stock Summary
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    if (minPrice != null) {
                        Text(
                            text = if (maxPrice != null && maxPrice != minPrice) {
                                "${CurrencyFormatter.format(minPrice, currencyScale)}+"
                            } else {
                                CurrencyFormatter.format(minPrice, currencyScale)
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = when {
                            outCount > 0 -> AlertRedContainer
                            lowCount > 0 -> WarningAmberContainer
                            else -> SuccessGreenContainer
                        }
                    ) {
                        Text(
                            text = when {
                                outCount > 0 -> "$outCount نفد • $totalStock"
                                lowCount > 0 -> "$lowCount ناقص • $totalStock"
                                else -> "$totalStock قطعة"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                outCount > 0 -> AlertRed
                                lowCount > 0 -> WarningAmber
                                else -> SuccessGreen
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Compact Variant Preview Chips + Manage Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val previewPieces = modelDetails.pieces.take(3)
                    previewPieces.forEach { p ->
                        val pLow = p.stockQty <= p.minStockAlert
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (pLow) WarningAmberContainer.copy(alpha = 0.65f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(resolveColorSwatch(p.color))
                                        .border(
                                            0.75.dp,
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                            CircleShape
                                        )
                                )
                                Text(
                                    text = "${p.sizeLabel} (${p.stockQty})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (pLow) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    if (modelDetails.pieces.size > 3) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                        ) {
                            Text(
                                text = "+${modelDetails.pieces.size - 3} أخرى",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                FilledTonalIconButton(
                    onClick = onQuickAddPiece,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("add_piece_to_model_${modelDetails.model.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "إضافة قطعة",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModelDetailInspector(
    modelDetails: ModelWithDetails,
    currencyScale: CurrencyScale,
    onEditModel: () -> Unit,
    onDeleteModel: () -> Unit,
    onAddPiece: () -> Unit,
    onEditPiece: (PieceVariantEntity) -> Unit,
    onDeletePiece: (PieceVariantEntity) -> Unit,
    onAdjustStock: (Long, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalStock = remember(modelDetails.pieces) { modelDetails.pieces.sumOf { it.stockQty } }
    val totalValue = remember(modelDetails.pieces) {
        modelDetails.pieces.sumOf { it.priceSyp * it.stockQty }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Detail Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BrandBadge(brand = modelDetails.brand)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "#${modelDetails.model.modelCode}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = modelDetails.model.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    if (modelDetails.model.fabricInfo.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = modelDetails.model.fabricInfo,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Text(
                    text = modelDetails.model.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilledTonalIconButton(
                    onClick = onEditModel,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("edit_model_${modelDetails.model.id}")
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "تعديل الموديل")
                }
                FilledTonalIconButton(
                    onClick = onDeleteModel,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = AlertRedContainer,
                        contentColor = AlertRed
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("delete_model_${modelDetails.model.id}")
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "حذف الموديل")
                }
            }
        }

        // Model Summary Tonal Strip
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "الأصناف / المقاسات",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${modelDetails.pieces.size} صنف",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "إجمالي الرصيد",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$totalStock قطعة",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "القيمة بالمخزون",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormatter.format(totalValue, currencyScale),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "القطع والألوان والمقاسات",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            FilledTonalButton(
                onClick = onAddPiece,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة قطعة", fontWeight = FontWeight.Bold)
            }
        }

        if (modelDetails.pieces.isEmpty()) {
            OutlinedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "لا توجد قطع مضافة لهذا الموديل",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                modelDetails.pieces.forEach { piece ->
                    PieceM3ListItem(
                        piece = piece,
                        currencyScale = currencyScale,
                        onAdjustStock = { delta -> onAdjustStock(piece.id, delta) },
                        onEdit = { onEditPiece(piece) },
                        onDelete = { onDeletePiece(piece) }
                    )
                }
            }
        }
    }
}

@Composable
fun PieceM3ListItem(
    piece: PieceVariantEntity,
    currencyScale: CurrencyScale,
    onAdjustStock: (Int) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isOut = piece.stockQty <= 0
    val isLow = !isOut && piece.stockQty <= piece.minStockAlert

    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                isOut -> AlertRed.copy(alpha = 0.5f)
                isLow -> WarningAmber.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.32f)
            }
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = when {
                isOut -> AlertRedContainer.copy(alpha = 0.32f)
                isLow -> WarningAmberContainer.copy(alpha = 0.32f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(resolveColorSwatch(piece.color))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                CircleShape
                            )
                    )
                    Column {
                        Text(
                            text = "${piece.color} • ${piece.sizeLabel}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (piece.skuBarcode.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QrCode2,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = piece.skuBarcode,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = CurrencyFormatter.format(piece.priceSyp, currencyScale),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onAdjustStock(-1) },
                                enabled = piece.stockQty > 0,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Filled.Remove, contentDescription = "إنقاص", modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "${piece.stockQty}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = when {
                                    isOut -> AlertRed
                                    isLow -> WarningAmber
                                    else -> SuccessGreen
                                },
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = { onAdjustStock(1) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "زيادة",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    FilledTonalButton(
                        onClick = { onAdjustStock(12) },
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text("+12 دزينة", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("edit_piece_${piece.id}")
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "تعديل القطعة",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("delete_piece_${piece.id}")
                    ) {
                        Icon(
                            Icons.Filled.DeleteOutline,
                            contentDescription = "حذف القطعة",
                            tint = AlertRed,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestockQueueCard(
    modelDetails: ModelWithDetails,
    piece: PieceVariantEntity,
    currencyScale: CurrencyScale,
    onAdjustStock: (Int) -> Unit,
    onEditPiece: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOut = piece.stockQty <= 0
    val ratio = if (piece.minStockAlert > 0) {
        (piece.stockQty.toFloat() / (piece.minStockAlert * 2f)).coerceIn(0f, 1f)
    } else 0f

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BrandBadge(brand = modelDetails.brand)
                        Text(
                            text = "#${modelDetails.model.modelCode}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "${modelDetails.model.name} (${piece.color} • ${piece.sizeLabel})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = CurrencyFormatter.format(piece.priceSyp, currencyScale),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = if (isOut) AlertRedContainer else WarningAmberContainer
                ) {
                    Text(
                        text = if (isOut) "نفد (0)" else "متبقي ${piece.stockQty} / ${piece.minStockAlert}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isOut) AlertRed else WarningAmber,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = if (isOut) AlertRed else WarningAmber,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = { onAdjustStock(12) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تزويد +12", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onAdjustStock(24) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تزويد +24", fontWeight = FontWeight.Bold)
                }
                FilledTonalIconButton(
                    onClick = onEditPiece,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "تعديل القطعة")
                }
            }
        }
    }
}

@Composable
fun BrandM3Card(
    brand: BrandEntity,
    modelsCount: Int,
    variantsCount: Int,
    totalStockQty: Int,
    lowStockVariants: Int,
    onBrowseBrand: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = Color(brand.accentColorHex)
    val initialChar = brand.name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "B"

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = accent,
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initialChar,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = brand.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = brand.notes.ifBlank { "كتالوج الشركة المسجل بالمخزون" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (lowStockVariants > 0) {
                    Surface(
                        shape = CircleShape,
                        color = WarningAmberContainer
                    ) {
                        Text(
                            text = "$lowStockVariants ناقص",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BrandMetricTile("الموديلات", "$modelsCount", Modifier.weight(1f))
                BrandMetricTile("الأصناف", "$variantsCount", Modifier.weight(1f))
                BrandMetricTile("الرصيد", "$totalStockQty", Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = onBrowseBrand,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = accent.copy(alpha = 0.14f),
                        contentColor = accent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("browse_brand_${brand.id}")
                ) {
                    Text("استعراض الموديلات", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                FilledTonalIconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("edit_brand_${brand.id}")
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "تعديل الماركة")
                }
                FilledTonalIconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = AlertRedContainer,
                        contentColor = AlertRed
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("delete_brand_${brand.id}")
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "حذف الماركة")
                }
            }
        }
    }
}

@Composable
private fun BrandMetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
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
fun InventoryM3EmptyState(
    icon: ImageVector = Icons.Filled.Checkroom,
    title: String,
    subtitle: String,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (primaryActionLabel != null || secondaryActionLabel != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (secondaryActionLabel != null && onSecondaryAction != null) {
                        OutlinedButton(
                            onClick = onSecondaryAction,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Text(secondaryActionLabel)
                        }
                    }
                    if (primaryActionLabel != null && onPrimaryAction != null) {
                        Button(
                            onClick = onPrimaryAction,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Text(primaryActionLabel)
                        }
                    }
                }
            }
        }
    }
}

private fun resolveColorSwatch(colorLabel: String): Color {
    val normalized = colorLabel.trim()
    return when {
        normalized.contains("أبيض") || normalized.contains("ابيض") -> Color(0xFFF7F7F5)
        normalized.contains("أسود") || normalized.contains("اسود") -> Color(0xFF1F2428)
        normalized.contains("سكري") -> Color(0xFFF3E9D2)
        normalized.contains("كحلي") -> Color(0xFF1A3254)
        normalized.contains("رمادي") || normalized.contains("حديدي") -> Color(0xFF7D8590)
        normalized.contains("فوشي") -> Color(0xFFD81B60)
        normalized.contains("زهر") -> Color(0xFFF48FB1)
        normalized.contains("أحمر") || normalized.contains("احمر") -> Color(0xFFC62828)
        normalized.contains("أزرق") || normalized.contains("ازرق") -> Color(0xFF1E88E5)
        normalized.contains("أخضر") || normalized.contains("اخضر") -> Color(0xFF2E7D32)
        else -> Color(0xFFC88A2E)
    }
}
