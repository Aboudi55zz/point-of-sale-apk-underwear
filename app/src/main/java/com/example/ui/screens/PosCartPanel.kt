package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.CartItem
import com.example.ui.theme.AlertRedContainer
import com.example.ui.theme.SuccessGreen
import com.example.util.CurrencyFormatter
import com.example.util.CurrencyScale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartCheckoutContent(
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
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Receipt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "فاتورة البيع الحالية",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${cartItems.sumOf { it.quantity }} قطعة محددة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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
            OutlinedCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp, horizontal = 20.dp),
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
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text("السلة فارغة", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "اضغط على أي قطعة من القائمة لإضافتها إلى الفاتورة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            cartItems.forEach { item ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("cart_dec_${item.pieceId}")
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
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("cart_inc_${item.pieceId}")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "زيادة الكمية", modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { onRemoveItem(item.pieceId) },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = AlertRedContainer.copy(alpha = 0.45f),
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    Icons.Filled.DeleteOutline,
                                    contentDescription = "حذف من السلة",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            val fieldShape = RoundedCornerShape(14.dp)
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
                    shape = fieldShape,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("checkout_discount_input")
                )
                OutlinedTextField(
                    value = paidInput,
                    onValueChange = { paidInput = it },
                    label = { Text("المبلغ المقبوض") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = fieldShape,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("checkout_paid_input")
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SuggestionChip(
                    onClick = { paidInput = CurrencyFormatter.toInputString(total, currencyScale) },
                    label = { Text("المبلغ تماماً") },
                    shape = CircleShape
                )
                quickCashAmountsOld.forEach { amt ->
                    SuggestionChip(
                        onClick = { paidInput = CurrencyFormatter.toInputString(amt, currencyScale) },
                        label = { Text(CurrencyFormatter.format(amt, currencyScale)) },
                        shape = CircleShape
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("اسم الزبون (اختياري)") },
                    singleLine = true,
                    shape = fieldShape,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("ملاحظة") },
                    singleLine = true,
                    shape = fieldShape,
                    modifier = Modifier.weight(1f)
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("الإجمالي النهائي", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = CurrencyFormatter.format(total, currencyScale),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
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
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { onConfirmCheckout(customerName, paidOldSyp, note) },
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("confirm_checkout_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "تأكيد البيع وإصدار الفاتورة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
