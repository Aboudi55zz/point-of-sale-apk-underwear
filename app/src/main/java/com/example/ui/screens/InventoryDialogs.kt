package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.BrandEntity
import com.example.data.local.PieceVariantEntity
import com.example.data.local.ProductModelEntity
import com.example.ui.PosViewModel
import com.example.util.CurrencyFormatter
import com.example.util.CurrencyScale

private val BRAND_COLORS = listOf(
    0xFF08514AL,
    0xFF1F5E78L,
    0xFFB87B1EL,
    0xFF6D4C7DL,
    0xFFA63D40L,
    0xFF3E5641L
)

@Composable
private fun DialogTitleHeader(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = tint.copy(alpha = 0.14f)
        ) {
            Box(modifier = Modifier.padding(9.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Column {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BrandEditorDialog(
    initialBrand: BrandEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, notes: String, accentColorHex: Long) -> Unit
) {
    var name by remember { mutableStateOf(initialBrand?.name ?: "") }
    var notes by remember { mutableStateOf(initialBrand?.notes ?: "") }
    var selectedColor by remember { mutableLongStateOf(initialBrand?.accentColorHex ?: BRAND_COLORS.first()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val fieldShape = RoundedCornerShape(12.dp)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            DialogTitleHeader(
                icon = Icons.Filled.Storefront,
                title = if (initialBrand == null) "إضافة ماركة جديدة" else "تعديل الماركة",
                subtitle = "إدارة اسم الشركة ولون الشارة المميز"
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMsg = null },
                    label = { Text("اسم الماركة / الشركة *") },
                    singleLine = true,
                    shape = fieldShape,
                    isError = errorMsg != null,
                    supportingText = errorMsg?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth().testTag("brand_name_input")
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات (اختياري)") },
                    maxLines = 2,
                    shape = fieldShape,
                    modifier = Modifier.fillMaxWidth().testTag("brand_notes_input")
                )
                Text("لون الشارة", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BRAND_COLORS.forEach { hex ->
                        val isSelected = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .then(
                                    if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                )
                                .clickable { selectedColor = hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "محدد",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMsg = "يرجى إدخال اسم الماركة"
                    } else {
                        onSave(name, notes, selectedColor)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_brand_button")
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun ModelEditorDialog(
    brands: List<BrandEntity>,
    initialModel: ProductModelEntity?,
    defaultBrandId: Long?,
    currencyScale: CurrencyScale,
    onDismiss: () -> Unit,
    onSave: (
        brandId: Long,
        modelCode: String,
        name: String,
        category: String,
        fabricInfo: String,
        initialColor: String?,
        initialSize: String?,
        initialPriceSyp: Long?,
        initialStock: Int?
    ) -> Unit
) {
    val fallbackBrandId = initialModel?.brandId ?: defaultBrandId ?: brands.firstOrNull()?.id ?: 0L
    var selectedBrandId by remember { mutableLongStateOf(fallbackBrandId) }
    var modelCode by remember { mutableStateOf(initialModel?.modelCode ?: "") }
    var name by remember { mutableStateOf(initialModel?.name ?: "") }
    var category by remember { mutableStateOf(initialModel?.category ?: "رجالي") }
    var fabricInfo by remember { mutableStateOf(initialModel?.fabricInfo ?: "") }

    // Initial piece fields when creating a brand new model
    val addFirstPiece = initialModel == null
    var firstColor by remember { mutableStateOf("أبيض") }
    var firstSize by remember { mutableStateOf("S..XXL") }
    var firstPriceText by remember { mutableStateOf("") }
    var firstStockText by remember { mutableStateOf("20") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val fieldShape = RoundedCornerShape(12.dp)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            DialogTitleHeader(
                icon = Icons.Filled.Category,
                title = if (initialModel == null) "إضافة موديل جديد" else "تعديل الموديل",
                subtitle = if (initialModel == null) "تعريف الموديل وإضافة أول قطعة له" else "تعديل بيانات الموديل الأساسية"
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("الماركة", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    brands.forEach { b ->
                        FilterChip(
                            selected = selectedBrandId == b.id,
                            onClick = { selectedBrandId = b.id },
                            label = { Text(b.name) }
                        )
                    }
                }
                OutlinedTextField(
                    value = modelCode,
                    onValueChange = { modelCode = it; errorMsg = null },
                    label = { Text("رقم الموديل *") },
                    singleLine = true,
                    shape = fieldShape,
                    modifier = Modifier.fillMaxWidth().testTag("model_code_input")
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMsg = null },
                    label = { Text("اسم الموديل / المواصفات *") },
                    singleLine = true,
                    shape = fieldShape,
                    modifier = Modifier.fillMaxWidth().testTag("model_name_input")
                )
                Text("القسم", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PosViewModel.MODEL_CATEGORIES.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                OutlinedTextField(
                    value = fabricInfo,
                    onValueChange = { fabricInfo = it },
                    label = { Text("نوع القماش (مثال: قطن، ليكرا، صوف)") },
                    singleLine = true,
                    shape = fieldShape,
                    modifier = Modifier.fillMaxWidth()
                )

                if (addFirstPiece) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "القطعة الأولى للموديل",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = firstColor,
                                    onValueChange = { firstColor = it },
                                    label = { Text("اللون") },
                                    singleLine = true,
                                    shape = fieldShape,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = firstSize,
                                    onValueChange = { firstSize = it },
                                    label = { Text("القياس") },
                                    singleLine = true,
                                    shape = fieldShape,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = firstPriceText,
                                    onValueChange = { firstPriceText = it; errorMsg = null },
                                    label = { Text("السعر (${currencyScale.shortUnit}) *") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    shape = fieldShape,
                                    modifier = Modifier.weight(1.3f).testTag("model_initial_price_input")
                                )
                                OutlinedTextField(
                                    value = firstStockText,
                                    onValueChange = { firstStockText = it },
                                    label = { Text("الكمية") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = fieldShape,
                                    modifier = Modifier.weight(0.9f)
                                )
                            }
                        }
                    }
                }

                errorMsg?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedBrandId == 0L) {
                        errorMsg = "يرجى إضافة ماركة أولاً"
                        return@Button
                    }
                    if (modelCode.isBlank() || name.isBlank()) {
                        errorMsg = "رقم الموديل واسم الموديل مطلوبان"
                        return@Button
                    }
                    val parsedPrice = if (addFirstPiece) {
                        CurrencyFormatter.parseInputToOldSyp(firstPriceText, currencyScale)
                            ?: run {
                                errorMsg = "أدخل سعراً صحيحاً للقطعة الأولى"
                                return@Button
                            }
                    } else null

                    onSave(
                        selectedBrandId,
                        modelCode,
                        name,
                        category,
                        fabricInfo,
                        if (addFirstPiece) firstColor else null,
                        if (addFirstPiece) firstSize else null,
                        parsedPrice,
                        firstStockText.toIntOrNull() ?: 20
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_model_button")
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun PieceEditorDialog(
    modelLabel: String,
    initialPiece: PieceVariantEntity?,
    currencyScale: CurrencyScale,
    onDismiss: () -> Unit,
    onSave: (
        color: String,
        sizeLabel: String,
        priceSyp: Long,
        stockQty: Int,
        minAlert: Int,
        skuBarcode: String
    ) -> Unit
) {
    var color by remember { mutableStateOf(initialPiece?.color ?: "أبيض") }
    var sizeLabel by remember { mutableStateOf(initialPiece?.sizeLabel ?: "M*L*XL") }
    var priceText by remember {
        mutableStateOf(
            initialPiece?.let { CurrencyFormatter.toInputString(it.priceSyp, currencyScale) } ?: ""
        )
    }
    var stockText by remember { mutableStateOf(initialPiece?.stockQty?.toString() ?: "20") }
    var minAlertText by remember { mutableStateOf(initialPiece?.minStockAlert?.toString() ?: "5") }
    var skuBarcode by remember { mutableStateOf(initialPiece?.skuBarcode ?: "") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val fieldShape = RoundedCornerShape(12.dp)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            DialogTitleHeader(
                icon = Icons.Filled.Inventory2,
                title = if (initialPiece == null) "إضافة قطعة جديدة" else "تعديل القطعة",
                subtitle = modelLabel
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("اللون *") },
                        singleLine = true,
                        shape = fieldShape,
                        modifier = Modifier.weight(1f).testTag("piece_color_input")
                    )
                    OutlinedTextField(
                        value = sizeLabel,
                        onValueChange = { sizeLabel = it },
                        label = { Text("القياس *") },
                        singleLine = true,
                        shape = fieldShape,
                        modifier = Modifier.weight(1f).testTag("piece_size_input")
                    )
                }
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it; errorMsg = null },
                    label = { Text("سعر القطعة (${currencyScale.shortUnit}) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = fieldShape,
                    modifier = Modifier.fillMaxWidth().testTag("piece_price_input")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it; errorMsg = null },
                        label = { Text("الكمية بالمخزون") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = fieldShape,
                        modifier = Modifier.weight(1f).testTag("piece_stock_input")
                    )
                    OutlinedTextField(
                        value = minAlertText,
                        onValueChange = { minAlertText = it },
                        label = { Text("حد التنبيه") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = fieldShape,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = skuBarcode,
                    onValueChange = { skuBarcode = it },
                    label = { Text("باركود / رمز إضافي (اختياري)") },
                    singleLine = true,
                    shape = fieldShape,
                    modifier = Modifier.fillMaxWidth()
                )
                errorMsg?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = CurrencyFormatter.parseInputToOldSyp(priceText, currencyScale)
                    if (price == null) {
                        errorMsg = "يرجى إدخال سعر صحيح"
                        return@Button
                    }
                    val stock = stockText.trim().toIntOrNull()
                    if (stock == null || stock < 0) {
                        errorMsg = "يرجى إدخال كمية صحيحة"
                        return@Button
                    }
                    onSave(
                        color.ifBlank { "متعدد" },
                        sizeLabel.ifBlank { "موحد" },
                        price,
                        stock,
                        (minAlertText.toIntOrNull() ?: 5).coerceAtLeast(0),
                        skuBarcode
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_piece_button")
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            DialogTitleHeader(
                icon = Icons.Filled.DeleteForever,
                title = title,
                tint = MaterialTheme.colorScheme.error
            )
        },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.testTag("confirm_delete_button")
            ) {
                Text("حذف")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
