package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.InvoiceWithItems
import com.example.data.local.SaleInvoiceEntity
import com.example.ui.components.ReceiptDialog
import com.example.util.CurrencyFormatter
import com.example.util.CurrencyScale
import java.util.Calendar

@Composable
fun SalesHistoryScreen(
    invoices: List<InvoiceWithItems>,
    storeName: String,
    currencyScale: CurrencyScale,
    onDeleteInvoice: (SaleInvoiceEntity, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedInvoice by remember { mutableStateOf<InvoiceWithItems?>(null) }
    var deletingInvoice by remember { mutableStateOf<SaleInvoiceEntity?>(null) }

    val startOfTodayMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val todayTotal = remember(invoices, startOfTodayMillis) {
        invoices.filter { it.invoice.createdAt >= startOfTodayMillis }.sumOf { it.invoice.totalSyp }
    }
    val allTimeTotal = remember(invoices) { invoices.sumOf { it.invoice.totalSyp } }
    val totalItemsSold = remember(invoices) { invoices.sumOf { inv -> inv.items.sumOf { it.quantity } } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1.35f),
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "مبيعات اليوم",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = CurrencyFormatter.format(todayTotal, currencyScale),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            RevenueSummaryCard(
                label = "الفواتير",
                value = "${invoices.size}",
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                valueColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(0.7f)
            )
            RevenueSummaryCard(
                label = "القطع المباعة",
                value = "$totalItemsSold",
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f),
                valueColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(0.8f)
            )
        }

        if (invoices.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "إجمالي المبيعات الكلي: ${CurrencyFormatter.format(allTimeTotal, currencyScale)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (invoices.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("لا توجد فواتير مبيعات بعد", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "ستظهر هنا كل فاتورة تقوم بإصدارها من نقطة البيع",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(invoices, key = { it.invoice.id }) { entry ->
                    val inv = entry.invoice
                    val qtySum = entry.items.sumOf { it.quantity }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("invoice_card_${inv.id}")
                            .clickable { selectedInvoice = entry },
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = inv.invoiceNumber,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = inv.customerName,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$qtySum قطعة • ${CurrencyFormatter.formatDateTime(inv.createdAt)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = CurrencyFormatter.format(inv.totalSyp, currencyScale),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { deletingInvoice = inv },
                                modifier = Modifier.testTag("delete_invoice_${inv.id}")
                            ) {
                                Icon(
                                    Icons.Filled.DeleteOutline,
                                    contentDescription = "حذف الفاتورة",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedInvoice?.let { entry ->
        ReceiptDialog(
            invoiceWithItems = entry,
            storeName = storeName,
            currencyScale = currencyScale,
            onDismiss = { selectedInvoice = null }
        )
    }

    deletingInvoice?.let { inv ->
        var restoreStock by remember { mutableStateOf(true) }
        AlertDialog(
            onDismissRequest = { deletingInvoice = null },
            title = { Text("حذف الفاتورة ${inv.invoiceNumber}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("هل تريد حذف هذه الفاتورة من السجل؟")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { restoreStock = !restoreStock }
                    ) {
                        Checkbox(checked = restoreStock, onCheckedChange = { restoreStock = it })
                        Text("إرجاع القطع المباعة إلى المخزون")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteInvoice(inv, restoreStock)
                        deletingInvoice = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingInvoice = null }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
private fun RevenueSummaryCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    valueColor: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(1.dp, valueColor.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = valueColor.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}
