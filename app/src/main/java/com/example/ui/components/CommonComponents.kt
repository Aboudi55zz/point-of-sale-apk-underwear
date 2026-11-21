package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.BrandEntity
import com.example.data.local.InvoiceWithItems
import com.example.ui.PosViewModel
import com.example.ui.theme.SuccessGreen
import com.example.util.CurrencyFormatter
import com.example.util.CurrencyScale

@Composable
fun BrandBadge(
    brand: BrandEntity,
    modifier: Modifier = Modifier
) {
    val accent = Color(brand.accentColorHex)
    Surface(
        modifier = modifier,
        color = accent.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Text(
                text = brand.name,
                style = MaterialTheme.typography.labelMedium,
                color = accent,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BrandFilterRow(
    brands: List<BrandEntity>,
    selectedBrandId: Long?,
    onSelectBrand: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val allSelected = selectedBrandId == null
        FilterChip(
            selected = allSelected,
            onClick = { onSelectBrand(null) },
            leadingIcon = if (allSelected) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else null,
            label = { Text("كل الماركات") },
            modifier = Modifier.testTag("filter_brand_all"),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        brands.forEach { brand ->
            val selected = selectedBrandId == brand.id
            FilterChip(
                selected = selected,
                onClick = { onSelectBrand(if (selected) null else brand.id) },
                leadingIcon = if (selected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null,
                label = { Text(brand.name) },
                modifier = Modifier.testTag("filter_brand_${brand.id}"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
fun CategoryFilterRow(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PosViewModel.CATEGORIES.forEach { cat ->
            val selected = selectedCategory == cat
            FilterChip(
                selected = selected,
                onClick = { onSelectCategory(cat) },
                leadingIcon = if (selected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null,
                label = { Text(cat) },
                modifier = Modifier.testTag("filter_cat_$cat"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}

@Composable
fun ReceiptDialog(
    invoiceWithItems: InvoiceWithItems,
    storeName: String,
    currencyScale: CurrencyScale,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val invoice = invoiceWithItems.invoice
    val changeSyp = (invoice.paidSyp - invoice.totalSyp).coerceAtLeast(0L)

    fun buildReceiptShareText(): String {
        val sb = StringBuilder()
        sb.appendLine(storeName)
        sb.appendLine("فاتورة رقم: ${invoice.invoiceNumber}")
        sb.appendLine("التاريخ: ${CurrencyFormatter.formatDateTime(invoice.createdAt)}")
        sb.appendLine("الزبون: ${invoice.customerName}")
        sb.appendLine("----------------------------")
        invoiceWithItems.items.forEach { item ->
            sb.appendLine(
                "${item.modelName} (#${item.modelCode}) - ${item.brandName}\n" +
                    "  ${item.color} | ${item.sizeLabel} | ${item.quantity} × ${
                        CurrencyFormatter.format(item.unitPriceSyp, currencyScale)
                    } = ${CurrencyFormatter.format(item.lineTotalSyp, currencyScale)}"
            )
        }
        sb.appendLine("----------------------------")
        if (invoice.discountSyp > 0) {
            sb.appendLine("المجموع: ${CurrencyFormatter.format(invoice.subtotalSyp, currencyScale)}")
            sb.appendLine("الحسم: -${CurrencyFormatter.format(invoice.discountSyp, currencyScale)}")
        }
        sb.appendLine("الإجمالي المطلوب: ${CurrencyFormatter.format(invoice.totalSyp, currencyScale)}")
        sb.appendLine("المدفوع: ${CurrencyFormatter.format(invoice.paidSyp, currencyScale)}")
        if (changeSyp > 0) {
            sb.appendLine("الباقي: ${CurrencyFormatter.format(changeSyp, currencyScale)}")
        }
        return sb.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "إيصال مكتمل",
                        tint = SuccessGreen
                    )
                    Text(
                        text = "إيصال البيع",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "إغلاق")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = storeName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${invoice.invoiceNumber} • ${CurrencyFormatter.formatDateTime(invoice.createdAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "الزبون: ${invoice.customerName}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (invoice.note.isNotBlank()) {
                            Text(
                                text = "ملاحظة: ${invoice.note}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                invoiceWithItems.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${item.modelName} (#${item.modelCode})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${item.brandName} • ${item.color} • ${item.sizeLabel}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${item.quantity} × ${CurrencyFormatter.format(item.unitPriceSyp, currencyScale)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = CurrencyFormatter.format(item.lineTotalSyp, currencyScale),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                if (invoice.discountSyp > 0L) {
                    ReceiptTotalRow(
                        label = "المجموع الفرعي",
                        value = CurrencyFormatter.format(invoice.subtotalSyp, currencyScale)
                    )
                    ReceiptTotalRow(
                        label = "الحسم",
                        value = "-${CurrencyFormatter.format(invoice.discountSyp, currencyScale)}",
                        valueColor = MaterialTheme.colorScheme.error
                    )
                }
                ReceiptTotalRow(
                    label = "الإجمالي",
                    value = CurrencyFormatter.format(invoice.totalSyp, currencyScale),
                    bold = true,
                    valueColor = MaterialTheme.colorScheme.primary
                )
                ReceiptTotalRow(
                    label = "المدفوع",
                    value = CurrencyFormatter.format(invoice.paidSyp, currencyScale)
                )
                if (changeSyp > 0L) {
                    ReceiptTotalRow(
                        label = "الباقي للزبون",
                        value = CurrencyFormatter.format(changeSyp, currencyScale),
                        bold = true,
                        valueColor = SuccessGreen
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("receipt_done_button")
            ) {
                Text("تم")
            }
        },
        dismissButton = {
            FilledTonalButton(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, buildReceiptShareText())
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "مشاركة الفاتورة"))
                },
                modifier = Modifier.testTag("receipt_share_button")
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("مشاركة")
            }
        }
    )
}

@Composable
private fun ReceiptTotalRow(
    label: String,
    value: String,
    bold: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold,
            color = valueColor
        )
    }
}
