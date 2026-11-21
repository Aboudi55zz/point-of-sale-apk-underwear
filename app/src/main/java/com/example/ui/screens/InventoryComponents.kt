package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
fun InventoryHeroSummaryCard(
    brandsCount: Int,
    modelsCount: Int,
    totalPiecesCount: Int,
    lowStockCount: Int,
    totalInventoryValueSyp: Long,
    currencyScale: CurrencyScale,
    lowStockFilterActive: Boolean,
    onBrandsClick: () -> Unit,
    onLowStockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val secondary = MaterialTheme.colorScheme.secondary

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            primary,
                            primary.copy(alpha = 0.88f),
                            tertiary
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.07f),
                    radius = size.height * 0.72f,
                    center = Offset(size.width * 0.12f, size.height * 0.15f)
                )
                drawCircle(
                    color = secondary.copy(alpha = 0.16f),
                    radius = size.height * 0.55f,
                    center = Offset(size.width * 0.92f, size.height * 0.95f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "إجمالي قيمة المخزون التقديرية",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.82f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = CurrencyFormatter.format(totalInventoryValueSyp, currencyScale),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
                    ) {
                        Box(
                            modifier = Modifier.padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Inventory2,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HeroMetricPill(
                        label = "الماركات",
                        value = "$brandsCount",
                        icon = Icons.Filled.Storefront,
                        onClick = onBrandsClick,
                        modifier = Modifier.weight(1f)
                    )
                    HeroMetricPill(
                        label = "الموديلات",
                        value = "$modelsCount",
                        icon = Icons.Filled.Category,
                        modifier = Modifier.weight(1f)
                    )
                    HeroMetricPill(
                        label = "القطع",
                        value = "$totalPiecesCount",
                        icon = Icons.Filled.Inventory2,
                        modifier = Modifier.weight(1f)
                    )
                    HeroMetricPill(
                        label = "نواقص",
                        value = "$lowStockCount",
                        icon = Icons.Filled.WarningAmber,
                        highlighted = lowStockFilterActive || lowStockCount > 0,
                        accentTint = if (lowStockCount > 0) Color(0xFFFFD580) else Color.White,
                        onClick = onLowStockClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroMetricPill(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    accentTint: Color = Color.White,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(14.dp),
        color = if (highlighted) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.12f),
        border = BorderStroke(
            width = if (highlighted) 1.2.dp else 0.8.dp,
            color = if (highlighted) accentTint.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.16f)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentTint.copy(alpha = 0.92f),
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accentTint,
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InventoryModelCard(
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
    var expanded by rememberSaveable(modelDetails.model.id) { mutableStateOf(true) }
    val brandAccent = Color(modelDetails.brand.accentColorHex)
    val pieces = modelDetails.pieces
    val totalQty = pieces.sumOf { it.stockQty }
    val lowCount = pieces.count { it.stockQty <= it.minStockAlert }
    val minPrice = pieces.minOfOrNull { it.priceSyp }
    val maxPrice = pieces.maxOfOrNull { it.priceSyp }
    val priceSummary = when {
        minPrice == null -> "بدون سعر"
        minPrice == maxPrice -> CurrencyFormatter.format(minPrice, currencyScale)
        else -> "${CurrencyFormatter.format(minPrice, currencyScale)} – ${CurrencyFormatter.format(maxPrice ?: minPrice, currencyScale)}"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
            .testTag("inv_model_card_${modelDetails.model.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (lowCount > 0) WarningAmber.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Subtle Brand Color Accent Stripe
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(brandAccent)
            )

            Column(modifier = Modifier.weight(1f)) {
                // Header Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brandAccent.copy(alpha = 0.045f))
                        .padding(start = 12.dp, end = 8.dp, top = 12.dp, bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                BrandBadge(brand = modelDetails.brand)
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "#${modelDetails.model.modelCode}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = modelDetails.model.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = modelDetails.model.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (modelDetails.model.fabricInfo.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "الخامة: ${modelDetails.model.fabricInfo}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onEditModel,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("edit_model_${modelDetails.model.id}")
                            ) {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "تعديل الموديل",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = onDeleteModel,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("delete_model_${modelDetails.model.id}")
                            ) {
                                Icon(
                                    Icons.Filled.DeleteOutline,
                                    contentDescription = "حذف الموديل",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Summary Stats + Collapse Toggle Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { expanded = !expanded }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            MiniModelStat(
                                text = "${pieces.size} أصناف",
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                            MiniModelStat(
                                text = "المخزون: $totalQty",
                                containerColor = if (lowCount > 0) WarningAmberContainer else SuccessGreenContainer,
                                contentColor = if (lowCount > 0) WarningAmber else SuccessGreen
                            )
                            MiniModelStat(
                                text = priceSummary,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (expanded) "طي القطع" else "عرض القطع",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (pieces.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "لا توجد قطع مضافة لهذا الموديل بعد",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        } else {
                            pieces.forEach { piece ->
                                PieceInventoryTile(
                                    piece = piece,
                                    currencyScale = currencyScale,
                                    onAdjustStock = { delta -> onAdjustStock(piece.id, delta) },
                                    onEdit = { onEditPiece(piece) },
                                    onDelete = { onDeletePiece(piece) }
                                )
                            }
                        }

                        FilledTonalButton(
                            onClick = onAddPiece,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("add_piece_to_model_${modelDetails.model.id}"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إضافة قطعة (لون / قياس / سعر)", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniModelStat(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        border = BorderStroke(0.75.dp, contentColor.copy(alpha = 0.18f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun PieceInventoryTile(
    piece: PieceVariantEntity,
    currencyScale: CurrencyScale,
    onAdjustStock: (Int) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isOut = piece.stockQty <= 0
    val isLow = piece.stockQty in 1..piece.minStockAlert

    val statusColor = when {
        isOut -> AlertRed
        isLow -> WarningAmber
        else -> SuccessGreen
    }
    val statusBg = when {
        isOut -> AlertRedContainer
        isLow -> WarningAmberContainer
        else -> SuccessGreenContainer
    }
    val statusLabel = when {
        isOut -> "نفد المخزون"
        isLow -> "مخزون منخفض"
        else -> "متوفر"
    }
    val statusIcon = when {
        isOut -> Icons.Filled.ErrorOutline
        isLow -> Icons.Filled.WarningAmber
        else -> Icons.Filled.CheckCircle
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inv_piece_row_${piece.id}"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                isOut -> AlertRed.copy(alpha = 0.4f)
                isLow -> WarningAmber.copy(alpha = 0.4f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: Color swatch + Color & Size + SKU + Price pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ColorSwatchDot(colorName = piece.color)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${piece.color} • ${piece.sizeLabel}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (piece.skuBarcode.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Filled.QrCode2,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
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

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f))

            // Row 2: Stock Status Badge + Tactile Stepper Capsule + Edit/Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = statusBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                // Controls: Stepper Capsule + Edit + Delete
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onAdjustStock(-1) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("stock_dec_${piece.id}")
                            ) {
                                Icon(
                                    Icons.Filled.Remove,
                                    contentDescription = "إنقاص المخزون",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "${piece.stockQty}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = { onAdjustStock(1) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("stock_inc_${piece.id}")
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "زيادة المخزون",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { onAdjustStock(12) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "+12",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("edit_piece_${piece.id}")
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "تعديل القطعة",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("delete_piece_${piece.id}")
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
}

@Composable
private fun ColorSwatchDot(colorName: String) {
    val normalized = colorName.trim()
    val swatchColor = when {
        normalized.contains("أبيض") || normalized.contains("ابيض") -> Color(0xFFFAFAFA)
        normalized.contains("أسود") || normalized.contains("اسود") -> Color(0xFF222222)
        normalized.contains("سكري") -> Color(0xFFF3E7C9)
        normalized.contains("رمادي") -> Color(0xFF9098A0)
        normalized.contains("حديدي") -> Color(0xFF545E66)
        normalized.contains("كحلي") -> Color(0xFF1B3459)
        normalized.contains("فوشي") -> Color(0xFFD62B70)
        normalized.contains("زهر") -> Color(0xFFF49AB6)
        else -> null
    }

    if (swatchColor != null) {
        Surface(
            modifier = Modifier.size(14.dp),
            shape = CircleShape,
            color = swatchColor,
            border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outline)
        ) {}
    } else {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            Color(0xFFE06C75),
                            Color(0xFFE5C07B),
                            Color(0xFF98C379),
                            Color(0xFF61AFEF),
                            Color(0xFFC678DD),
                            Color(0xFFE06C75)
                        )
                    )
                )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrandManagementCard(
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("brand_card_${brand.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Accent Header Band
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accent.copy(alpha = 0.08f))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = accent,
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = brand.name.trim().take(2).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = brand.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (brand.notes.isNotBlank()) {
                            Text(
                                text = brand.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("edit_brand_${brand.id}")
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "تعديل الماركة",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("delete_brand_${brand.id}")
                    ) {
                        Icon(
                            Icons.Filled.DeleteOutline,
                            contentDescription = "حذف الماركة",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Stats & Quick Browse Action
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MiniModelStat(
                        text = "$modelsCount موديل",
                        containerColor = accent.copy(alpha = 0.12f),
                        contentColor = accent
                    )
                    MiniModelStat(
                        text = "$variantsCount صنف",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    MiniModelStat(
                        text = "$totalStockQty قطعة بالمخزن",
                        containerColor = SuccessGreenContainer,
                        contentColor = SuccessGreen
                    )
                    if (lowStockVariants > 0) {
                        MiniModelStat(
                            text = "$lowStockVariants نواقص",
                            containerColor = WarningAmberContainer,
                            contentColor = WarningAmber
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onBrowseBrand,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("استعراض موديلات ${brand.name}")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryEmptyState(
    hasActiveFilters: Boolean,
    onClearFilters: () -> Unit,
    onAddModel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(76.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Text(
                text = if (hasActiveFilters) "لا توجد موديلات مطابقة للبحث" else "المخزون فارغ حالياً",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (hasActiveFilters) {
                    "جرّب تغيير كلمات البحث أو إزالة الفلاتر لعرض جميع الموديلات."
                } else {
                    "أضف أول موديل وابدأ بمتابعة الكميات والأسعار لكل لون وقياس."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (hasActiveFilters) {
                OutlinedButton(onClick = onClearFilters, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إعادة ضبط الفلاتر")
                }
            } else {
                Button(onClick = onAddModel, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إضافة موديل جديد")
                }
            }
        }
    }
}
